<?php

/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();
$salt="0476089252";

date_default_timezone_set('America/New_York');

///////////////////////////Connection block//////////////////////////////////////// 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT();
	$conn=$db->connect();
/////////////////////////////////////////////////////////////////////////////////// 
	
// check for required fields
if (isset($_POST['ingredientName'])) {
    
	$ingredientName = mysqli_real_escape_string($conn, $_POST['ingredientName']);
	$oldIngredientName = mysqli_real_escape_string($conn, $_POST['oldIngredientName']);	
    $calories = mysqli_real_escape_string($conn, $_POST["calories"]);
    $protein = mysqli_real_escape_string($conn, $_POST["protein"]);
    $fat = mysqli_real_escape_string($conn, $_POST["fat"]);
	$carbs = mysqli_real_escape_string($conn, $_POST["carbs"]);
    $type = mysqli_real_escape_string($conn, $_POST["type"]);
    $notes = mysqli_real_escape_string($conn, $_POST["notes"]);
	$gramAmount = mysqli_real_escape_string($conn, $_POST["gramAmount"]);
	$userName = mysqli_real_escape_string($conn, $_POST["userName"]);
	$dateUpdated = date("Y-m-d H:i:s");
	$token = $_POST["token"];

	$result = mysqli_query($conn, "SELECT joinDate from user where userName= '$userName'");
	$row = mysqli_fetch_array($result);
	
	if($token!== mysqli_real_escape_string($conn, crypt(md5($salt), md5($row[0])))){
		// successfully inserted into database
        $response["success"] = 0;
        $response["message"] = "Your are not correctly loged in.\nTry loging in again.";

        // echoing JSON response
        echo json_encode($response);
		die();
	}
	
	//making it out of 100
	$calories = round(($calories/$gramAmount)*100, 1);
    $protein = round(($protein/$gramAmount)*100, 1);
    $fat = round(($fat/$gramAmount)*100, 1);
	$carbs = round(($carbs/$gramAmount)*100, 1);
	
    // mysql update row with matched ingredeint Name
    $result = mysqli_query($conn, "UPDATE ingredientlist SET ingredientName='$ingredientName', calories = '$calories', protein = '$protein', fat = '$fat', "
			."carbs = '$carbs', type = '$type', notes = '$notes', dateUpdated = '$dateUpdated'  WHERE ingredientName = '$oldIngredientName'");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Ingredient successfully updated.";
        
        // echoing JSON response
        echo json_encode($response);
    } else {
	
		// failed to insert row
		$error = mysqli_error($conn);
		$pos=stripos($error, "Duplicate");
		//use !== because duplicate can be a position 0
		if($pos !== false)
			$error="That ingredient name is already in use.";
			
        $response["success"] = 0;
        $response["message"] = $error;
        
        // echoing JSON response
        echo json_encode($response);
        
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>

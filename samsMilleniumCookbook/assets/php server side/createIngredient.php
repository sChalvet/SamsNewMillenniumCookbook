<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();
$salt="0476089252";

///////////////////////////Connection block//////////////////////////////////////// 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT();
	$conn=$db->connect();
/////////////////////////////////////////////////////////////////////////////////// 
	
// check for required fields
if (isset($_POST["ingredientName"])) {
    
	
	$ingredientName = mysqli_real_escape_string($conn, $_POST['ingredientName']);		
    $calories = mysqli_real_escape_string($conn, $_POST["calories"]);
    $protein = mysqli_real_escape_string($conn, $_POST["protein"]);
    $fat = mysqli_real_escape_string($conn, $_POST["fat"]);
	$carbs = mysqli_real_escape_string($conn, $_POST["carbs"]);
    $type = mysqli_real_escape_string($conn, $_POST["type"]);
    $notes =mysqli_real_escape_string($conn, $_POST["notes"]);
	$gramAmount =mysqli_real_escape_string($conn, $_POST["gramAmount"]);
	$addedBy = mysqli_real_escape_string($conn, $_POST["addedBy"]);
	$token = $_POST["token"];

	$result = mysqli_query($conn, "SELECT joinDate from user where userName= '$addedBy'");
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

    // mysql inserting a new row
    $result = mysqli_query($conn, "INSERT INTO ingredientlist(ingredientName, calories, protein, fat, carbs, notes, addedBy, type)"
					."VALUES('$ingredientName', '$calories', '$protein', '$fat', '$carbs', '$notes', '$addedBy', '$type')");

	
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Ingredient successfully created.";

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
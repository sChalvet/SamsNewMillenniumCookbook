<?php

/*
 * Following code will delete an ingredient from table
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
if (isset($_POST['ingredientName'])) {
    $ingredientName = $_POST['ingredientName'];
	$userName = mysqli_real_escape_string($conn, $_POST["userName"]);
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



    // mysql update row with matched pid
    $result = mysqli_query($conn, "DELETE FROM ingredientlist WHERE ingredientName = '$ingredientName'");
    
    // check if row deleted or not
    if (mysqli_affected_rows($conn) > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Ingredient successfully deleted";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No ingredient found";

        // echo no users JSON
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
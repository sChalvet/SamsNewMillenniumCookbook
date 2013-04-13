<?php

/*
 * Following code will get user information (to be edited by user)
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
if (isset($_POST["nickName"])) {
	$nickName = strip_tags(substr($_POST['nickName'],0,20));
	$token = $_POST["token"];

	$nickName = mysqli_real_escape_string($conn, $nickName);
	
	$result = mysqli_query($conn, "SELECT joinDate from user where userName= '$nickName' LIMIT 1");
	$row = mysqli_fetch_array($result);
	
	if($token!== mysqli_real_escape_string($conn, crypt(md5($salt), md5($row[0])))){
		// successfully inserted into database
        $response["success"] = 0;
        $response["message"] = "Your are not correctly loged in.\nTry loging in again.";

        // echoing JSON response
        echo json_encode($response);
		die();
	}	

	//checking to see if this nickname has been used
    $result = mysqli_query($conn, "SELECT userId, userName, firstName, lastName, email, testQuestion FROM user WHERE userName = '$nickName' LIMIT 1");
	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_array($result);
		// get test question
		
		// temp user array
        $product = array();
        $product["userId"] = $row[0];
		$product["userName"] = $row[1];
		$product["firstName"] = $row[2];
		$product["lastName"] = $row[3];
		$product["email"] = $row[4];
		$product["testQuestion"] = $row[5];

		$response["success"] = 1;
        $response["message"] = "Found user information.";
		
		$response["product"] = array();
        // push product into final response array
        array_push($response["product"], $product);
		
        // echoing JSON response
        echo json_encode($response);
	}else{
		// failed to find that userName
		$response["success"] = 0;
		$response["message"] = "User name does not exist";
			
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
<?php

/*
 * Following code will create a new User Account
 */
date_default_timezone_set('America/New_York');
 
 
// array for JSON response
$response = array();

// check for required fields
if (isset($_POST["nickName"])) {
    
	
	$nickName = strip_tags(substr($_POST['nickName'],0,20));		
    $email = $_POST["email"];
    $firstName = $_POST["firstName"];
    $lastName = $_POST["lastName"];
	$password = strip_tags(substr($_POST['password'],0,30));
	$testQuestion = $_POST["testQuestion"];
	$testAnswer = $_POST["testAnswer"];
	$dateUpdated = date("Y-m-d H:i:s");

	// include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

	$nickName = mysqli_real_escape_string($conn, $nickName);
	$email = mysqli_real_escape_string($conn, $email);
	$firstName = mysqli_real_escape_string($conn, $firstName);
	$lastName = mysqli_real_escape_string($conn, $lastName);
	$password = mysqli_real_escape_string($conn, $password);
	$testQuestion = mysqli_real_escape_string($conn, $testQuestion);
	$testAnswer = mysqli_real_escape_string($conn, $testAnswer);

	//checking to see if this nickname has been used
    $result = mysqli_query($conn, "SELECT userName FROM user WHERE userName = '$nickName'");
	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_array($result);
		// NickName is taken
        $response["success"] = 2;
        $response["message"] = "Sorry, this Nickname has already been taken";

        // echoing JSON response
        echo json_encode($response);
	}else{
		
		$encryptedAnswer = md5($testAnswer);
		$encryptedPass = mysqli_real_escape_string($conn,crypt(md5($password), md5($encryptedAnswer)));
		
		// mysql inserting a new row
		$result = mysqli_query($conn, "INSERT INTO user(userName, email, joinDate, firstName, lastName, password, testQuestion, testAnswer)"
						."VALUES('$nickName', '$email', '$dateUpdated', '$firstName', '$lastName', '$encryptedPass', '$testQuestion', '$encryptedAnswer')");

		
		// check if row inserted or not
		if ($result) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "Account successfully created.";

			// echoing JSON response
			echo json_encode($response);
		} else {
			// failed to insert row
			$error = mysqli_error();
			$response["success"] = 0;
			$response["message"] = $error;
			
			// echoing JSON response
			echo json_encode($response);
		}
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>
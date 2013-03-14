<?php

/*
 * Following code will update a user Account
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST["userId"])) {
	
	$userId = mysqli_real_escape_string($conn, $_POST['userId']);
	$nickName = mysqli_real_escape_string($conn, strip_tags(substr($_POST['nickName'],0,20)));
	$email = mysqli_real_escape_string($conn, $_POST['email']);
	$firstName = mysqli_real_escape_string($conn, $_POST['firstName']);
	$lastName = mysqli_real_escape_string($conn, $_POST['lastName']);
	$oldPassword = mysqli_real_escape_string($conn, strip_tags(substr($_POST['oldPassword'],0,30)));		
    $newPassword = mysqli_real_escape_string($conn, strip_tags(substr($_POST["newPassword"],0,30)));
    $testNewQuestion = mysqli_real_escape_string($conn, $_POST["testNewQuestion"]);
    $testNewAnswer = mysqli_real_escape_string($conn, $_POST["testNewAnswer"]);
	$testOldAnswer = mysqli_real_escape_string($conn, $_POST["testOldAnswer"]);


    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();
	
   //checking to see if this nickname matches the current password
    $result = mysqli_query($conn, "SELECT userName, password, testAnswer FROM user WHERE userId = '$userId' LIMIT 1");
	if( $result){
		if (mysqli_num_rows($result) > 0) {
			$row = mysqli_fetch_array($result);
			
			$response["product"] = array();
			
			$encryptedAnswer = md5($testOldAnswer);
			
			if($encryptedAnswer===$row[2]){
			
					$encryptedPass = mysqli_real_escape_string($conn, crypt(md5($oldPassword), md5($encryptedAnswer)));
					
					if($encryptedPass==$row[1]){	
						
						//calling updateAcc() to actualy update the account
						updateAcc($userId, $nickName, $email, $firstName, $lastName, $newPassword, $testNewQuestion, $testNewAnswer, $conn);

					}else{
					
						// NickName is taken
						$response["success"] = 0;
						$response["message"] = "Password does not match";

						// echoing JSON response
						echo json_encode($response);
					}
			}else{
				// no nickName found
				$response["success"] = 0;
				$response["message"] = "Test Answer did not match";

				// echoing JSON response
				echo json_encode($response);
			
			}
		}else{
		
				// no nickName found
				$response["success"] = 0;
				$response["message"] = "user $nickName was not found";

				// echoing JSON response
				echo json_encode($response);
		}
		}else{

			// failed to insert row
			$error = mysqli_error();
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


function updateAcc($userId, $nickName, $email, $firstName, $lastName, $newPassword, $testNewQuestion, $testNewAnswer, $conn){


	$encryptedAnswer = md5($testAnswer);
	$encryptedPass = mysqli_real_escape_string($conn,crypt(md5($password), md5($encryptedAnswer)));

    // mysql inserting a new row
	$result = mysqli_query($conn, "UPDATE user SET userName='$nickName', email='$email', firstName='$firstName', lastName='$lastName', ='$email'"
						." WHERE userId = '$userId'");
	$product = array();
	$product["firstName"] = $row[2];
	$product["lastName"] = $row[3];
	$product["email"] = $row[4];

	// push single product into final response array
	array_push($response["product"], $product);
						
	// Good Account
	$response["success"] = 1;
	$response["message"] = "password matches";
					
	// echoing JSON response
	echo json_encode($response);





}
	
	

	
>?
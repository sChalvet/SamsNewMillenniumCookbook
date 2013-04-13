<?php

/*
 * Following code will update a user Account
 */

// array for JSON response
$response = array();
$webMasterEmail = "samscookbookteam@gmail.com";
$salt = "0476089252";

///////////////////////////Connection block//////////////////////////////////////// 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT();
	$conn=$db->connect();
/////////////////////////////////////////////////////////////////////////////////// 

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
	$token = mysqli_real_escape_string($conn, $_POST["token"]);


	//$product["token"] = crypt(md5($nickName), md5($encryptedAnswer));

	
   //checking to see if this nickname matches the current password
    $result = mysqli_query($conn, "SELECT userName, password, testAnswer, joinDate FROM user WHERE userId = '$userId' LIMIT 1");
	if( $result){
		if (mysqli_num_rows($result) > 0) {
			$row = mysqli_fetch_array($result);
			
			$response["product"] = array();
			
			//checking to see if user is properly loged in
			if($token!==mysqli_real_escape_string($conn, crypt(md5($salt), md5($row[3])))){
			
				// NickName is taken
				$response["success"] = 0;
				$response["message"] = "You are not properly logged in";

				// echoing JSON response
				echo json_encode($response);
				die();
			}
			
			
			
			$encryptedAnswer = md5($testOldAnswer);
			$oldEncryptedAnswer = $row[2];
			$joinDate = $row[3];
			
			if($encryptedAnswer===$oldEncryptedAnswer){
			
					$encryptedPass = mysqli_real_escape_string($conn, crypt(md5($oldPassword), md5($encryptedAnswer)));
					
					if($encryptedPass==$row[1]){	
						
						//calling updateAcc() to actualy update the account
						updateAcc($userId, $nickName, $email, $firstName, $lastName, $oldPassword, $newPassword, $testNewQuestion, $testNewAnswer, $oldEncryptedAnswer, $webMasterEmail, $joinDate,  $conn);

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


function updateAcc($userId, $nickName, $email, $firstName, $lastName, $oldPassword, $newPassword, $testNewQuestion, $testNewAnswer, $encryptedAnswer, $webMasterEmail, $joinDate, $conn){

	$sqlTestQandA="";
	$sqlNewPass="";
	// array for JSON response
	$response = array();

	if($testNewQuestion !== ""){
		$encryptedAnswer = md5($testNewAnswer);
		$sqlTestQandA= ", testQuestion= '$testNewQuestion', testAnswer= '$encryptedAnswer'";
	}
	
	if($newPassword !==""){
		$encryptedPass = mysqli_real_escape_string($conn, crypt(md5($newPassword), md5($encryptedAnswer)));
		$sqlNewPass= ", password= '$encryptedPass'";
	}else{
		$encryptedPass = mysqli_real_escape_string($conn, crypt(md5($oldPassword), md5($encryptedAnswer)));
		$sqlNewPass= ", password= '$encryptedPass'";	
	}

    // mysql inserting a new row
	$result = mysqli_query($conn, "UPDATE user SET userName='$nickName', email='$email', firstName='$firstName', lastName='$lastName'".$sqlTestQandA."".$sqlNewPass.""
						." WHERE userId = '$userId'");
	
	if(!$result){
	
		// query failed
		$response["success"] = 0;
		$response["message"] = "Failed to update";
					
		// echoing JSON response
		echo json_encode($response);
		die();
	}
	
	$product = array();
	$product["token"] = mysqli_real_escape_string($conn, crypt(md5($salt), md5($joinDate )));
	//$product["test"] = $sqlTestQandA;
	//$product["pass"] = $sqlNewPass;

	sendEmail($email, $firstName, $webMasterEmail);
	
	$response["product"] = array();
	// push single product into final response array
	array_push($response["product"], $product);
	
	// successful update
	$response["success"] = 1;
	$response["message"] = "Update successful";
					
	// echoing JSON response
	echo json_encode($response);



}
	
	function sendEmail($email, $firstName, $webMasterEmail){
	
	$to      = $email;
	$subject = 'Sam\'s Cookbook';
	$message = '
				<html>
				<head>
				</head>
				<body>
					<center>
					<h1>Greetings from Sam\'s Cookbook!!</h1><br/>
					<p>'.$firstName.', your account with us has been successfully updated!</p>
					<p>We hope you enjoy the app and continue to do so.</p><br/><br/><br/>
					</center>
				</body>
				<footer>
					<p><font size=1>If you did not update your account on Sam\'s Cookbook then send us an email at '.$webMasterEmail.'</font></p>
				</footer>
				</html>
				';

	// To send HTML mail, the Content-type header must be set
	$headers  = 'MIME-Version: 1.0' . "\r\n";
	$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";

	// Additional headers
	$headers .= 'From: Sam\'s Cookbook <'.$webMasterEmail .'>' . "\r\n";

	mail($to, $subject, $message, $headers);

}
	

	
?>
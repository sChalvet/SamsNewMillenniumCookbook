<?php

/*
 * Following code will reset user password
 */
date_default_timezone_set('America/New_York');
 
 $webMasterEmail = "sams.cookbook@gmail.com";
 
// array for JSON response
$response = array();

// check for required fields
if (isset($_POST["userName"])) {
    
	
	$nickName = strip_tags(substr($_POST['userName'],0,20));		
	$testAnswer = $_POST["testAnswer"];
	$date = date("Y-m-d H:i:s");

	// include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

	$nickName = mysqli_real_escape_string($conn, $nickName);
	$testAnswer = mysqli_real_escape_string($conn, $testAnswer);

	//checking to see if this nickname has been used
    $result = mysqli_query($conn, "SELECT testAnswer, firstname, email FROM user WHERE userName = '$nickName'");
	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_array($result);
		
		$encryptedAnswer = md5($testAnswer);
		
		if($row[0]===$encryptedAnswer){
		
			$encryptedPass = mysqli_real_escape_string($conn,crypt(md5($date), md5($encryptedAnswer)));
			
			//update password
			$result = mysqli_query($conn, "UPDATE user SET password='$encryptedPass' WHERE userName = '$nickName'");
			
			if($result){
				
				sendEmail($row[2], $row[1], $webMasterEmail, $encryptedPass);
				
			}else{
			
				// failed to find row
				$response["success"] = 0;
				$response["message"] = "Failed to Update.";
			
				// echoing JSON response
				echo json_encode($response);
			
			}
		}else{
		
		// failed to find row
		$response["success"] = 0;
		$response["message"] = "Test Answer did not match.";
			
		// echoing JSON response
		echo json_encode($response);
		
		}
	}else{
		
		// failed to find row
		$response["success"] = 0;
		$response["message"] = "No test answers for that user name";
			
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


function sendEmail($email, $firstName, $webMasterEmail, $newPass){
	
	$to      = $email;
	$subject = 'Password reset for Sam\'s New Millennium Cookbook';
	$message = '
				<html>
				<head>
				</head>
				<body>
					<center>
					<h1>Your Password has been reset</h1><br/>
					<p>'.$firstName.', your new password for Sam\'s New Millennium Cookbook has been reset to: "'.$newPass.'"</p>
					<p>Please change it to something you like better as soon as you can. Thanks.</p><br/><br/><br/>
					</center>
				</body>
				<footer>
					<p><font size=1>If you did not try to change your password please email us at '.$webMasterEmail.'</font></p>
				</footer>
				</html>
				';

	// To send HTML mail, the Content-type header must be set
	$headers  = 'MIME-Version: 1.0' . "\r\n";
	$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";

	// Additional headers
	$headers .= 'From: Sams Cookbook <'.$webMasterEmail.'>' . "\r\n";

	$ok= mail($to, $subject, $message, $headers);
	
	if($ok){
		
		// Password reset
		$response["success"] = 1;
		$response["message"] = "Password reset.";

		// echoing JSON response
		echo json_encode($response);
	
	}else{
			// Password reset
			$response["success"] = 1;
			$response["message"] = "Password reset, but email failed to send.";

			// echoing JSON response
			echo json_encode($response);
	
	}

}


?>
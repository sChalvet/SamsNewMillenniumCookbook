<?php

/*
 * Following code will send the feedback
 * that the user entered to the webmaster 
 */
 
 $webMasterEmail = "samscookbookteam@gmail.com";
 
// array for JSON response
$response = array();

// check for required fields
if (isset($_POST["comment"])) {
   	
	$comment = strip_tags($_POST['comment']);		
	$name = $_POST["author"];
				
	sendEmail($webMasterEmail, $name, $comment);
					
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}


function sendEmail($webMasterEmail, $name, $comment){
	$comment = "\"".$comment."\"";
	$to      = $webMasterEmail;
	$subject = 'Feedback for Sam\'s Cookbook';
	$message = '
				<html>
				<head>
				</head>
				<body>
					<center>
					<h1>Feedback from '.$name.'</h1><br/>
					<p>User comment:<b>'. $comment .'</b> change it to something you like better as soon as you can. Thanks.</p><br/><br/><br/>
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
	$headers .= 'From: Sam\'s Cookbook <'.$webMasterEmail.'>' . "\r\n";

	$ok= mail($to, $subject, $message, $headers);
	
	if($ok){
		
		// Password reset
		$response["success"] = 1;
		$response["message"] = "Feedback sent.";

		// echoing JSON response
		echo json_encode($response);
	
	}else{
			// Password reset
			$response["success"] = 0;
			$response["message"] = "Failed to send feedback.";

			// echoing JSON response
			echo json_encode($response);	
	}

}


?>
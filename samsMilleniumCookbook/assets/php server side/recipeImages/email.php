
<?php
$to      = 'samos6@gmail.com';
$subject = 'the subject';
$message = 'hello';
$headers = 'From: webmaster@example.com' . "\r\n" .
    'Reply-To: webmaster@example.com' . "\r\n" .
    'X-Mailer: PHP/' . phpversion();

	$sent=mail($to, $subject, $message, $headers);
if($sent){
	echo "message sent";
}else{
	echo "message failed to send";
}
?>

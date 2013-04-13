<?php

/*
 * Following code will log the user in
 */

// array for JSON response
$response = array();
$salt = "0476089252";

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
	$password = strip_tags(substr($_POST['password'],0,30));
	
	$nickName = mysqli_real_escape_string($conn, $nickName);
	$password = mysqli_real_escape_string($conn, $password);

	//checking to see if this nickname matches the password
    $result = mysqli_query($conn, "SELECT userName, password, firstName, lastName, email, testAnswer, joinDate FROM user WHERE userName = '$nickName' LIMIT 1");
	if( $result){
		if (mysqli_num_rows($result) > 0) {
			$row = mysqli_fetch_array($result);
			
			$response["product"] = array();
			
			$encryptedAnswer=$row[5];
			
			$encryptedPass = mysqli_real_escape_string($conn, crypt(md5($password), md5($encryptedAnswer)));
			
			if($encryptedPass==$row[1]){	
				
				$product = array();
				$product["nickName"] = $row[0];
				$product["firstName"] = $row[2];
				$product["lastName"] = $row[3];
				$product["email"] = $row[4];
				$product["token"] = mysqli_real_escape_string($conn, crypt(md5($salt), md5($row[6])));

				// push single product into final response array
				array_push($response["product"], $product);
				
				// Good Account
				$response["success"] = 1;
				$response["message"] = "password matches";
			
				// echoing JSON response
				echo json_encode($response);
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
?>
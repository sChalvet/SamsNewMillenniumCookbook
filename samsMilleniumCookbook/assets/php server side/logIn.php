<?php

/*
 * Following code will create a new User Account
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_REQUEST["nickName"])) {
    
	
	$nickName = $_REQUEST['nickName'];		
    $password = $_REQUEST["password"];


    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

	//checking to see if this nickname matches the password
    $result = mysqli_query($conn, "SELECT userName, password, firstName, lastName, email FROM user WHERE userName = '$nickName'");
	if( $result){
		if (mysqli_num_rows($result) > 0) {
			$row = mysqli_fetch_array($result);
			
			$response["product"] = array();
			
			if($password==$row[1]){	
				
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
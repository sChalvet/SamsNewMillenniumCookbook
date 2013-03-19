<?php

/*
 * Following code will retreive the test question from the user acount
 */
// array for JSON response
$response = array();

// check for required fields
if (isset($_POST["userName"])) {
    
	
	$nickName = strip_tags(substr($_POST['userName'],0,20));		

	// include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

	$nickName = mysqli_real_escape_string($conn, $nickName);

	//checking to see if this nickname has been used
    $result = mysqli_query($conn, "SELECT testQuestion FROM user WHERE userName = '$nickName'");
	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_array($result);
		// get test question
		
		// temp user array
        $product = array();
        $product["testQuestion"] = $row[0];

		$response["success"] = 1;
        $response["message"] = "Found test question.";
		
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
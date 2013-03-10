<?php

/**
* This code put an image in the a folder
* to save 
*************/

// array for JSON response
$response = array();


// check for required fields
if (isset($_REQUEST['image'])) {
    
	$base=$_REQUEST['image'];
	$recipeName=$_REQUEST['recipeName'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();
	
	$binary=base64_decode($base);
	header('Content-Type: image/jpg; charset=utf-8');
	$file = fopen('recipeImages/'.$recipeName.'.jpg', 'wb');

    // check if row inserted or not
    if ($file!=FALSE) {
	
		if(fwrite($file, $binary)!=FALSE){	
			fclose($file);
			
			// successfully uploaded
			$response["success"] = 1;
			$response["message"] = "pic successfully uploaded.";	
			$response["hasImage"] = 1;
			
		}else{
			fclose($file);
			// failed to write
			$response["success"] = 0;
			$response["message"] = "failed to write.";
		}
        
        // echoing JSON response
        echo json_encode($response);
    } else {
	
        $response["success"] = 0;
        $response["message"] = "failed to open file";
        
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
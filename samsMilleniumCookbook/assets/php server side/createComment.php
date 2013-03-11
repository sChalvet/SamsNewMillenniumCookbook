<?php

/*
 * Following code will create a new comment row for a recipe
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_REQUEST["recipeName"])) {

	$recipeName = $_REQUEST['recipeName'];		
    $comment = $_REQUEST["comment"];
    $rating = $_REQUEST["rating"];
    $authorName = $_REQUEST["author"];


    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();
	
    // mysql inserting a new row
    $result = mysqli_query($conn, "INSERT INTO recipecomments(recipeName, comment, rating, authorName)"
					."VALUES('$recipeName', '$comment', '$rating', '$authorName')");

	
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Comment successfully created.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
		$error = mysqli_error($conn);
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
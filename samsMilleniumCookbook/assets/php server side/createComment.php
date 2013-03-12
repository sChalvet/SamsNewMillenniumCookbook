<?php

/*
 * Following code will create a new comment row for a recipe
 */

// array for JSON response
$response = array();

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();
	
// check for required fields
if (isset($_POST["recipeName"])) {

	$recipeName = mysqli_real_escape_string($_POST['recipeName']);		
    $comment = mysqli_real_escape_string($_POST["comment"]);
    $rating = mysqli_real_escape_string($_POST["rating"]);
    $authorName = mysqli_real_escape_string($_POST["author"]);



	
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
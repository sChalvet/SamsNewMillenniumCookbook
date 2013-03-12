<?php

/*
 * Following code will update a recipe Comment
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST["recipeName"])) {

	$recipeName = mysqli_real_escape_string($conn, $_POST['recipeName']);		
    $comment = mysqli_real_escape_string($conn, $_POST["comment"]);
    $rating = $_POST["rating"];
    $authorName = mysqli_real_escape_string($conn, $_POST["author"]);


    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();
	
    // mysql inserting a new row
	$result = mysqli_query($conn, "UPDATE recipecomments SET comment='$comment', rating='$rating'"
						." WHERE recipeName = '$recipeName' AND authorName='$authorName'");

	
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Comment successfully updated.";

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
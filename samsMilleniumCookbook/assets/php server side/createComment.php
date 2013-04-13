<?php

/*
 * Following code will create a new comment row for a recipe
 */

// array for JSON response
$response = array();
$salt="0476089252";

///////////////////////////Connection block//////////////////////////////////////// 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT();
	$conn=$db->connect();
/////////////////////////////////////////////////////////////////////////////////// 
	
// check for required fields
if (isset($_POST["recipeName"])) {

	$recipeName = mysqli_real_escape_string($conn, $_POST['recipeName']);		
    $comment = mysqli_real_escape_string($conn, $_POST["comment"]);
    $rating = mysqli_real_escape_string($conn, $_POST["rating"]);
    $authorName = mysqli_real_escape_string($conn, $_POST["author"]);
	$token = $_POST["token"];

	$result = mysqli_query($conn, "SELECT joinDate from user where userName= '$authorName'");
	$row = mysqli_fetch_array($result);
	
	if($token!== mysqli_real_escape_string($conn, crypt(md5($salt), md5($row[0])))){
		// successfully inserted into database
        $response["success"] = 0;
        $response["message"] = "Your are not correctly loged in.\nTry loging in again.";

        // echoing JSON response
        echo json_encode($response);
		die();
	}
	
	//geting id from recipe
	$resultId = mysqli_query($conn, "SELECT recipeId FROM recipe WHERE recipeName = '$recipeName'");
	$row = mysqli_fetch_array($resultId);
	$recipeId = $row[0];
	
	//geting id from user
	$resultUserId = mysqli_query($conn, "SELECT userId FROM user WHERE userName = '$authorName'");
	$row = mysqli_fetch_array($resultUserId);
	$userId = $row[0];
		
	
    // mysql inserting a new row
    $result = mysqli_query($conn, "INSERT INTO recipecomments(recipeId, recipeName, comment, rating, authorName, authorId)"
					."VALUES('$recipeId', '$recipeName', '$comment', '$rating', '$authorName', '$userId')");

	
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
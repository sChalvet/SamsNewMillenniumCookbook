<?php

/*
 * This is for requesting the details of a 
 * single comment for edit
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
$conn=$db->connect();

// check for post data
if (isset($_POST["recipeName"])) {
    $recipeName = mysqli_real_escape_string($conn, $_POST['recipeName']);
	$author = mysqli_real_escape_string($conn, $_POST['author']);

    // get a product from products table
   $result = mysqli_query($conn, "SELECT comment, rating FROM recipecomments WHERE recipeName = '$recipeName' AND authorName= '$author'");

    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
			
			$response["product"] = array();
			
            $row = mysqli_fetch_array($result);
			//$rowPic = mysql_fetch_array($getPic);
	
			$product = array();
			
			$product["comment"] = $row[0];
			$product["rating"] = $row[1];
			
            array_push($response["product"], $product);
			
			$response["message"] = "Found the comment";
			$response["success"] = 1;

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No Comment found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No Comment found";

        // echo no users JSON
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
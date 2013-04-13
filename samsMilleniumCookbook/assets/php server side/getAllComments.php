<?php

/*
 * This is for requesting the all of the comments from
 * a single recipe
 */

// array for JSON response
$response = array();

///////////////////////////Connection block//////////////////////////////////////// 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT();
	$conn=$db->connect();
/////////////////////////////////////////////////////////////////////////////////// 

// check for post data
if (isset($_POST["recipeName"])) {
    $recipeName = mysqli_real_escape_string($conn, $_POST['recipeName']);

    // get comments from recipecomments table
	$result = mysqli_query($conn, "SELECT authorName, postTime, comment, rating FROM recipecomments WHERE recipeName ='$recipeName' ORDER BY postTime DESC");
	
	// check for empty result
    if (!empty($result)) {
		
		if (mysqli_num_rows($result) > 0) {
			$response["products"] = array();
		
			while ($row = mysqli_fetch_array($result)) {
	
				$product = array();
				
				$product["author"] = $row[0];
				$product["postTime"] = $row[1];
				$product["comment"] = $row[2];
				$product["rating"] = $row[3];	
				
				array_push($response["products"], $product);
			}
			// success
			$response["success"] = 1;
			$response["message"] = "All is well";
		
		
			// echoing JSON response
			echo json_encode($response);
		}else{
		$response["success"] = 0;
		$response["message"] = "no comments for this recipe";
		// echoing JSON response
		echo json_encode($response);
		
		}
	
	
    } else {
        // no Comments found
        $response["success"] = 2;
        $response["message"] = "No Comments found";

        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "recipeName is missing";

    // echoing JSON response
	echo json_encode($response);
}
?>
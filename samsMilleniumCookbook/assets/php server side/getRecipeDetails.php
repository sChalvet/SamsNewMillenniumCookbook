<?php

/*
 * This is for requesting the details of a 
 * single recipe
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for post data
if (isset($_GET["recipeName"])) {
    $recipeName = $_GET['recipeName'];

    // get a product from products table
   $result = mysql_query("SELECT userName, numRatings, ingredientDiscription, directions, rating, prepTime, cookTime FROM recipe WHERE recipeName = '$recipeName'");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
			
			$response["product"] = array();
			
            $row = mysql_fetch_array($result);
	
			$product = array();
			
			$product["author"] = $row[0];
			$product["numReviews"] = $row[1];
			$product["ingredientList"] = $row[2];
			$product["cookingDirections"] = $row[3];
			$product["rating"] = $row[4];
			$product["prepTime"] = $row[5];
			$product["cookTime"] = $row[6];			

            array_push($response["product"], $product);
			
			$response["success"] = 1;

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No recipe found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No recipe found";

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
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
   $result = mysql_query("SELECT userName, ingredientDiscription, directions, prepTime, cookTime FROM recipe WHERE recipeName = '$recipeName'");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
			$rating=0;
			$numRatings=0;
			//$getPic = mysql_query("SELECT img FROM picture WHERE picId = 1") or die(mysql_error());
			
			$response["product"] = array();
			
            $row = mysql_fetch_array($result);
			//$rowPic = mysql_fetch_array($getPic);
	
			$product = array();
			
			$product["author"] = $row[0];
			$product["ingredientList"] = $row[1];
			$product["cookingDirections"] = $row[2];
			$product["prepTime"] = $row[3];
			$product["cookTime"] = $row[4];
			
			$countResult = mysql_query("SELECT count(*) FROM recipecomments where recipename = '$recipeName'") or die(mysql_error());
			if(mysql_num_rows($countResult) > 0){
				$CountRow = mysql_fetch_array($countResult);
				$numRatings=$CountRow[0];
			}
			
			$product["numRatings"] = $numRatings;
			$response["message"] = "Found $numRatings ratings";
			
			$ratingResult = mysql_query("SELECT sum(rating) FROM recipecomments where recipename = '$recipeName'") or die(mysql_error());
			if(mysql_num_rows($ratingResult) > 0){
				$ratingRow = mysql_fetch_array($ratingResult);
				if($CountRow[0]>0)
					$rating=$ratingRow[0]/$CountRow[0];
			}	
			$product["rating"] = $rating;
			$response["message"] .= ", found total rating of $rating";
			//$product["image"] = imageCreateFromString($rowPic[0]);			
	
			//$im = imageCreateFromString($rowPic[0]);
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
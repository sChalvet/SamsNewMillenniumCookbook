<?php

/*
 * This is for requesting the details of a *
 * single ingredient
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for post data
if (isset($_GET["ingredientName"])) {
    $ingredientName = $_GET['ingredientName'];

    // get a product from products table
   $result = mysql_query("SELECT calories, protein, fat, carbs, notes, addedBy, type FROM ingredientlist WHERE ingredientName = '$ingredientName'");
		//$result = mysql_query("SELECT calories, protein, fat, carbs, notes, addedBy, type FROM ingredientlist WHERE ingredientName = 'Apples'");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
			
			$response["product"] = array();
			
            $row = mysql_fetch_array($result);
	
			$product = array();
			
			$product["calories"] = $row[0];
			$product["protein"] = $row[1];
			$product["fat"] = $row[2];
			$product["carbs"] = $row[3];
			$product["notes"] = $row[4];
			$product["addedBy"] = $row[5];
			$product["type"] = $row[6];
			
			
			/*$product["ingredientName"] = $row[0];
			$product["calories"] = $row[1];
			$product["protein"] = $row[2];
			$product["fat"] = $row[3];
			$product["carbs"] = $row[4];
			$product["notes"] = $row[5];
			$product["addedBy"] = $row[6];
			$product["type"] = $row[7];
			$product["dateCreated"] = $row[8];
			$product["dateUpdated"] = $row[9];*/
			

            array_push($response["product"], $product);
			
			$response["success"] = 1;

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No product found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No product found";

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
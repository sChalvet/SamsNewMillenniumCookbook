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
if (isset($_POST["ingredientName"])) {
    $ingredientName = $_POST['ingredientName'];

    // get a product from products table
   $result = $db->connect()->query("SELECT calories, protein, fat, carbs, notes, addedBy, type FROM ingredientlist WHERE ingredientName = '$ingredientName'");

    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
			
			$response["product"] = array();
			
            $row = mysqli_fetch_array($result);
	
			$product = array();
			
			$product["calories"] = $row[0];
			$product["protein"] = $row[1];
			$product["fat"] = $row[2];
			$product["carbs"] = $row[3];
			$product["notes"] = $row[4];
			$product["addedBy"] = $row[5];
			$product["type"] = $row[6];
			
            array_push($response["product"], $product);
			
			$response["success"] = 1;

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No Ingredient found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No Ingredient found";

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
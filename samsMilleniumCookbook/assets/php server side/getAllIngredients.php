<?php

/*
 * Following code will list all the ingredients
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

	
// get all products from products table
$result = mysql_query("SELECT ingredientName FROM ingredientlist") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["products"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
        $product["ingredientName"] = $row[0];
		//$product["addedBy"] = $row[1];
		//$product["type"] = $row[2];
		
        /* $product["calories"] = $row[1];
       $product["protein"] = $row[2];
        $product["fat"] = $row[3];
        $product["carbs"] = $row[4];
        $product["notes"] = $row[5];
		$product["addedBy"] = $row[6];
		$product["type"] = $row[7];
		$product["dateCreated"] = $row[8];
		$product["dateUpdated"] = $row[9];*/

	//echo "<script type='text/javascript'>alert('".$product["ingredientName"]."');</script>";

        // push single product into final response array
        array_push($response["products"], $product);
    }
    // success
    $response["success"] = 1;

	
	
    // echoing JSON response
    echo json_encode($response);
	
	//echo "<script type='text/javascript'>alert('".$response."');</script>";
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No products found";

    // echo no users JSON
    echo json_encode($response);
}
?>

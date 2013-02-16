<?php

/*
 * Following code will list all the recipes
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

	
// get all products from products table
$result = mysql_query("SELECT recipeName, summery, rating, userName, numRatings, prepTime, cookTime FROM recipe") or die(mysql_error());
//$result = mysql_query("SELECT recipeName FROM recipe") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["products"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
        $product["recipeName"] = $row[0];
		$product["summery"] = $row[1];
		$product["rating"] = $row[2];
		$product["author"] = $row[3];
		$product["numRatings"] = $row[4];
		$product["prepTime"] = $row[5];
		$product["cookTime"] = $row[6];
		

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

<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_GET["recipename"])) {
//if(true){
    
	//var_dump($_GET);
	
	$recipename = $_GET['recipename'];		
    $author = $_GET["author"];
    $imgredientList = $_GET["imgredientList"];
    $cookingDirections = $_GET["cookingDirections"];
	$cookTime = $_GET["cookTime"];
    $prepTime = $_GET["prepTime"];
    $summery = $_GET["summery"];
	$type = $_GET["type"];

    //var_dump( $error);

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO recipe(recipename, userName, ingredientDiscription, directions, cookTime, prepTime, summery, type)"
					."VALUES('$recipename', '$author', '$imgredientList', '$cookingDirections', '$cookTime', '$prepTime', '$summery', '$type')");

	
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Recipe successfully created.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
		$error = mysql_error();
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
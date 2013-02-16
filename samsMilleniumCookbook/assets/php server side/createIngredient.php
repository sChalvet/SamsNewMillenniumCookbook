<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_GET["ingredientName"])) {
//if(true){
    
	//var_dump($_GET);
	
	$ingredientName = $_GET['ingredientName'];		
    $calories = $_GET["calories"];
    $protein = $_GET["protein"];
    $fat = $_GET["fat"];
	$carbs = $_GET["carbs"];
    $type = $_GET["type"];
    $notes = $_GET["notes"];
	$addedBy = $_GET["addedBy"];
    
	/*if( $calories==""||$protein==""||$fat==""||$carbs==""){
	
	// one of the fields is blanck
		$error = "Make sure you put something in each field.";
        $response["success"] = 0;
        $response["message"] = $error;
        
        // echoing JSON response
        echo json_encode($response);
		
	}*/

    //var_dump( $error);

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO ingredientlist(ingredientName, calories, protein, fat, carbs, notes, addedBy, type)"
					."VALUES('$ingredientName', '$calories', '$protein', '$fat', '$carbs', '$notes', '$addedBy', '$type')");

	
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Ingredient successfully created.";

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
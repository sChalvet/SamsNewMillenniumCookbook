<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_REQUEST["ingredientName"])) {
    
	
	$ingredientName = $_REQUEST['ingredientName'];		
    $calories = $_REQUEST["calories"];
    $protein = $_REQUEST["protein"];
    $fat = $_REQUEST["fat"];
	$carbs = $_REQUEST["carbs"];
    $type = $_REQUEST["type"];
    $notes = $_REQUEST["notes"];
	$addedBy = $_REQUEST["addedBy"];


    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

    // mysql inserting a new row
    $result = mysqli_query($conn, "INSERT INTO ingredientlist(ingredientName, calories, protein, fat, carbs, notes, addedBy, type)"
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
		$error = mysqli_error($conn);
		$pos=stripos($error, "Duplicate");
		//use !== because duplicate can be a position 0
		if($pos !== false)
			$error="That ingredient name is already in use.";
			
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
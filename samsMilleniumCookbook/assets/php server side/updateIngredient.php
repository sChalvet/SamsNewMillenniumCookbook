<?php

/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();

date_default_timezone_set('America/New_York');

// check for required fields
if (isset($_REQUEST['ingredientName'])) {
    
	$ingredientName = $_REQUEST['ingredientName'];
	$oldIngredientName = $_REQUEST['oldIngredientName'];	
    $calories = $_REQUEST["calories"];
    $protein = $_REQUEST["protein"];
    $fat = $_REQUEST["fat"];
	$carbs = $_REQUEST["carbs"];
    $type = $_REQUEST["type"];
    $notes = $_REQUEST["notes"];
	$dateUpdated = date("Y-m-d H:i:s");
	
	//var_dump($dateUpdated);

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

    // mysql update row with matched pid
    $result = mysqli_query($conn, "UPDATE ingredientlist SET ingredientName='$ingredientName', calories = '$calories', protein = '$protein', fat = '$fat', "
			."carbs = '$carbs', type = '$type', notes = '$notes', dateUpdated = '$dateUpdated'  WHERE ingredientName = '$oldIngredientName'");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Ingredient successfully updated.";
        
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

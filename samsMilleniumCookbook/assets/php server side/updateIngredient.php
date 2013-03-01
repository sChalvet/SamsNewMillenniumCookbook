<?php

/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_GET['ingredientName'])) {
    
	$ingredientName = $_GET['ingredientName'];		
    $calories = $_GET["calories"];
    $protein = $_GET["protein"];
    $fat = $_GET["fat"];
	$carbs = $_GET["carbs"];
    $type = $_GET["type"];
    $notes = $_GET["notes"];
	$dateUpdated = date("Y-m-d H:i:s");
	
	//var_dump($dateUpdated);

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

    // mysql update row with matched pid
    $result = mysqli_query($conn, "UPDATE ingredientList SET calories = '$calories', protein = '$protein', fat = '$fat', "
			."carbs = '$carbs', type = '$type', notes = '$notes', dateUpdated = '$dateUpdated'  WHERE ingredientName = '$ingredientName'");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Ingredient successfully updated.";
        
        // echoing JSON response
        echo json_encode($response);
    } else {
        
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>

<?php

/*
 * Following code will delete a ingredient from table
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_REQUEST['ingredientName'])) {
    $ingredientName = $_REQUEST['ingredientName'];

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

    // mysql update row with matched pid
    $result = mysqli_query($conn, "DELETE FROM ingredientList WHERE ingredientName = '$ingredientName'");
    
    // check if row deleted or not
    if (mysqli_affected_rows($conn) > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Ingredient successfully deleted";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No ingredient found";

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
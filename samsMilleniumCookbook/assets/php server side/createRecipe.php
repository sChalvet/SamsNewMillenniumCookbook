<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_REQUEST["recipeName"])) {
//if(true){
    
	$i=0;
	//gets the whole list of ingredients
	while(isset($_REQUEST["ingredientName".$i])){
		$arrayIngredientName[$i] = "'".$_REQUEST["ingredientName".$i]."'";
		$arrayAmount[$i] = "'".$_REQUEST["amount".$i]."'";
		$arrayMeasurement[$i] = "'".$_REQUEST["measurement".$i]."'";
		$arrayDiscription[$i] = "'".$_REQUEST["discription".$i]."'";
		$arrayImportant[$i] = "'".$_REQUEST["important".$i]."'";
		$i++;
	}
	
	$recipeName = $_REQUEST['recipeName'];		
    $author = $_REQUEST["author"];
    $ingredientList = $_REQUEST["ingredientList"];
    $cookingDirections = $_REQUEST["cookingDirections"];
	$cookTime = $_REQUEST["cookTime"];
    $prepTime = $_REQUEST["prepTime"];
    $summery = $_REQUEST["summery"];
	$type = $_REQUEST["type"];
	$servings = $_REQUEST["servings"];
	$hasImage = $_REQUEST["hasImage"];

    //var_dump( $error);

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

	//checking to see if this recipeName has been used
    $result = mysqli_query($conn, "SELECT userName FROM recipe WHERE recipeName = '$recipeName'");
	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_array($result);
		// Recipe name is taken
        $response["success"] = 2;
        $response["message"] = "Sorry, this recipe name has already been taken by: ".$row[0];

        // echoing JSON response
        echo json_encode($response);
	}else{
		
		
		// mysql inserting a new row
		$result1 = mysqli_query($conn, "INSERT INTO recipe(recipeName, userName, ingredientDiscription, directions, cookTime, prepTime, summery, type, servings, hasImage)"
						."VALUES('$recipeName', '$author', '$ingredientList', '$cookingDirections', '$cookTime', '$prepTime', '$summery', '$type', '$servings', '$hasImage')");

		//empties the table for that recipeName in case there have been changes made or ingredients removed
		$result = mysqli_query($conn, "DELETE FROM recipeingredients WHERE recipeName = '$recipeName'");
		
		for($i=0; $i< sizeof($arrayIngredientName); $i++){
		
			// mysql inserting a new row for each ingredient
			$result2 = mysqli_query($conn, "INSERT INTO recipeingredients(recipeName, ingredientName, amount, measurement, description, important)"
						."VALUES ('$recipeName', $arrayIngredientName[$i], $arrayAmount[$i], $arrayMeasurement[$i], $arrayDiscription[$i], $arrayImportant[$i])");
		}
		
		
		// check if row inserted or not
		if ($result1) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "Recipe successfully created.";

			// echoing JSON response
			echo json_encode($response);
		} else {
			// failed to insert row
			$error = mysqli_error($conn);
			$response["success"] = 3;
			$response["message"] = $error;
			
			// echoing JSON response
			echo json_encode($response);
		}
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>
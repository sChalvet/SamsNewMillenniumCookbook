<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_GET["recipeName"])) {
//if(true){
    
	$i=0;
	//gets the whole list of ingredients
	while(isset($_GET["ingredientName".$i])){
		$arrayIngredientName[$i] = "'".$_GET["ingredientName".$i]."'";
		$arrayAmount[$i] = "'".$_GET["amount".$i]."'";
		$arrayMeasurement[$i] = "'".$_GET["measurement".$i]."'";
		$arrayDiscription[$i] = "'".$_GET["discription".$i]."'";
		$arrayImportant[$i] = "'".$_GET["important".$i]."'";
		$i++;
	}
	
	$recipeName = $_GET['recipeName'];		
    $author = $_GET["author"];
    $ingredientList = $_GET["ingredientList"];
    $cookingDirections = $_GET["cookingDirections"];
	$cookTime = $_GET["cookTime"];
    $prepTime = $_GET["prepTime"];
    $summery = $_GET["summery"];
	$type = $_GET["type"];
	$servings = $_GET["servings"];

    //var_dump( $error);

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

	//checking to see if this recipeName has been used
    $result = mysql_query("SELECT userName FROM recipe WHERE recipeName = '$recipeName'");
	if (mysql_num_rows($result) > 0) {
		$row = mysql_fetch_array($result);
		// Recipe name is taken
        $response["success"] = 2;
        $response["message"] = "Sorry, this recipe name has already been taken by: ".$row[0];

        // echoing JSON response
        echo json_encode($response);
	}else{
		
		
		// mysql inserting a new row
		$result1 = mysql_query("INSERT INTO recipe(recipeName, userName, ingredientDiscription, directions, cookTime, prepTime, summery, type, servings)"
						."VALUES('$recipeName', '$author', '$ingredientList', '$cookingDirections', '$cookTime', '$prepTime', '$summery', '$type', '$servings')");

		//empties the table for that recipeName in case there have been changes made or ingredients removed
		$result = mysql_query("DELETE FROM recipeingredients WHERE recipeName = '$recipeName'");
		
		for($i=0; $i< sizeof($arrayIngredientName); $i++){
		
			// mysql inserting a new row for each ingredient
			$result2 = mysql_query("INSERT INTO recipeingredients(recipeName, ingredientName, amount, measurement, description, important)"
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
			$error = mysql_error();
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
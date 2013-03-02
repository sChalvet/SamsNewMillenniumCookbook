<?php

/*
 * Following code will update a recipe
 * All recipe details are read from HTTP GET Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_GET["recipeName"])) {
    
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
	$oldRecipeName = $_GET['oldRecipeName'];	
    $author = $_GET["author"];
    $ingredientList = $_GET["ingredientList"];
    $cookingDirections = $_GET["cookingDirections"];
	$cookTime = $_GET["cookTime"];
    $prepTime = $_GET["prepTime"];
    $summery = $_GET["summery"];
	$type = $_GET["type"];
	$servings = $_GET["servings"];
	$dateUpdated = date("Y-m-d H:i:s");

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

	//checking to see if this recipeName has been used
    $result = mysqli_query($conn, "SELECT userName FROM recipe WHERE recipeName = '$oldRecipeName'");
	$row = mysqli_fetch_array($result);
	if ($row[0]!=$author) {
	
		// Not the author of recipe
        $response["success"] = 2;
        $response["message"] = "Sorry $author, you are not the author of this recipe. ".$row[0]." is.";

        // echoing JSON response
        echo json_encode($response);
	}else{
		
		
		// mysql inserting a new row
		$result1 = mysqli_query($conn, "UPDATE recipe SET recipeName='$recipeName', ingredientDiscription='$ingredientList', directions='$cookingDirections', cookTime='$cookTime',"
							." prepTime='$prepTime', summery='$summery', type='$type', servings='$servings', modifyDate='$dateUpdated'"
						." WHERE recipeName = '$oldRecipeName'");

		//empties the table for that recipeName in case there have been changes made or ingredients removed
		$result = mysqli_query($conn, "DELETE FROM recipeingredients WHERE recipeName = '$oldRecipeName'");
		
		for($i=0; $i< sizeof($arrayIngredientName); $i++){
		
			// mysql inserting a new row for each ingredient
			$result2 = mysqli_query($conn, "INSERT INTO recipeingredients(recipeName, ingredientName, amount, measurement, description, important)"
						."VALUES ('$recipeName', $arrayIngredientName[$i], $arrayAmount[$i], $arrayMeasurement[$i], $arrayDiscription[$i], $arrayImportant[$i])");
		}
		
		
		// check if row inserted or not
		if ($result1) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "Recipe successfully updated.";

			// echoing JSON response
			echo json_encode($response);
		} else {
			// failed to insert row
			$error = mysqli_error();
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
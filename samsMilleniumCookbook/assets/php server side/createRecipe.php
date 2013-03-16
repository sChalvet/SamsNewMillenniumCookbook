<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();
$salt="0476089252";

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
	$conn=$db->connect();

// check for required fields
if (isset($_POST["recipeName"])) {
    
	$i=0;
	//gets the whole list of ingredients
	while(isset($_POST["ingredientName".$i])){
		$arrayIngredientName[$i] = "'".mysqli_real_escape_string($conn, $_POST["ingredientName".$i])."'";
		$arrayAmount[$i] = "'".$_POST["amount".$i]."'";
		$arrayMeasurement[$i] = "'".$_POST["measurement".$i]."'";
		$arrayDiscription[$i] = "'".mysqli_real_escape_string($conn, $_POST["discription".$i])."'";
		$arrayImportant[$i] = "'".$_POST["important".$i]."'";
		$i++;
	}
	
	$recipeName = mysqli_real_escape_string($conn, $_POST['recipeName']);		
    $author = mysqli_real_escape_string($conn, $_POST["author"]);
    $ingredientList = $_POST["ingredientList"];
    $cookingDirections = mysqli_real_escape_string($conn, $_POST["cookingDirections"]);
	$cookTime = $_POST["cookTime"];
    $prepTime = $_POST["prepTime"];
    $summery = mysqli_real_escape_string($conn, $_POST["summery"]);
	$type = $_POST["type"];
	$servings = $_POST["servings"];
	$hasImage = $_POST["hasImage"];
	$token = $_POST["token"];

	$result = mysqli_query($conn, "SELECT joinDate from user where userName= '$author'");
	$row = mysqli_fetch_array($result);
	
	if($token!== mysqli_real_escape_string($conn, crypt(md5($salt), md5($row[0])))){
		// successfully inserted into database
        $response["success"] = 0;
        $response["message"] = "Your are not correctly loged in.\nTry loging in again.";

        // echoing JSON response
        echo json_encode($response);
		die();
	}



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

		$recipeId = mysqli_insert_id($conn);
		//empties the table for that recipeName in case there have been changes made or ingredients removed
		$result = mysqli_query($conn, "DELETE FROM recipeingredients WHERE recipeName = '$recipeName'");
		
		for($i=0; $i< sizeof($arrayIngredientName); $i++){
		
			// mysql inserting a new row for each ingredient
			$result2 = mysqli_query($conn, "INSERT INTO recipeingredients(recipeId, recipeName, ingredientName, amount, measurement, description, important)"
						."VALUES ($recipeId, '$recipeName', $arrayIngredientName[$i], $arrayAmount[$i], $arrayMeasurement[$i], $arrayDiscription[$i], $arrayImportant[$i])");
		}
		
		// check if row inserted or not
		if (!$result2) {
			// successfully inserted into database
			$response["success"] = 0;
			$response["message"] = "Failed, "+mysqli_error($conn);

			// echoing JSON response
			echo json_encode($response);
		} 
		
		// check if row inserted or not
		if ($result1) {
			// successfully inserted into database
			$response["success"] = 1;
			$response["message"] = "Recipe successfully created."+$recipeId;

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
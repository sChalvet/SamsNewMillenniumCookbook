<?php

/*
 * Following code will update a recipe
 * All recipe details are read from HTTP GET Request
 */

// array for JSON response
$response = array();

date_default_timezone_set('America/New_York');

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
		$arrayIngredientName[$i] = "'".$_POST["ingredientName".$i]."'";
		$arrayAmount[$i] = "'".$_POST["amount".$i]."'";
		$arrayMeasurement[$i] = "'".$_POST["measurement".$i]."'";
		$arrayDiscription[$i] = "'".mysqli_real_escape_string($conn, $_POST["discription".$i])."'";
		$arrayImportant[$i] = "'".$_POST["important".$i]."'";
		$i++;
	}
	
	$recipeName = mysqli_real_escape_string($conn, $_POST['recipeName']);
	$oldRecipeName = mysqli_real_escape_string($conn, $_POST['oldRecipeName']);	
    $author = mysqli_real_escape_string($conn, $_POST["author"]);
    $ingredientList = mysqli_real_escape_string($conn, $_POST["ingredientList"]);
    $cookingDirections = mysqli_real_escape_string($conn, $_POST["cookingDirections"]);
	$cookTime = mysqli_real_escape_string($conn, $_POST["cookTime"]);
    $prepTime = mysqli_real_escape_string($conn, $_POST["prepTime"]);
    $summery = mysqli_real_escape_string($conn, $_POST["summery"]);
	$type = mysqli_real_escape_string($conn, $_POST["type"]);
	$servings = mysqli_real_escape_string($conn, $_POST["servings"]);
	$hasImage = mysqli_real_escape_string($conn, $_POST["hasImage"]);
	$dateUpdated = date("Y-m-d H:i:s");
	
	//$recipeName = mysqli_real_escape_string($conn, $recipeName);



	//checking to see if this recipeName has been used
    $result = mysqli_query($conn, "SELECT userName, hasImage FROM recipe WHERE recipeName = '$oldRecipeName'");
	$row = mysqli_fetch_array($result);
	
	//determines if user already had a picture
	$hadImage = $row[1];
	
	if (strcasecmp($row[0], $author)) {
	
		// Not the author of recipe
        $response["success"] = 2;
        $response["message"] = "Sorry $author, you are not the author of this recipe. ".$row[0]." is.";

        // echoing JSON response
        echo json_encode($response);
	}else{
			
		// mysql inserting a new row
		$result1 = mysqli_query($conn, "UPDATE recipe SET recipeName='$recipeName', ingredientDiscription='$ingredientList', directions='$cookingDirections', cookTime='$cookTime',"
							." prepTime='$prepTime', summery='$summery', type='$type', servings='$servings', modifyDate='$dateUpdated', hasImage='$hasImage'"
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
			
			//if Recipe already had an image we need to rename it
			if($hadImage){
				//this makes "Test Recipe" into "Test_Recipe" important for searching for images
				$newImageName = str_replace(" ", "_", $recipeName);
				$oldImageName = str_replace(" ", "_", $oldRecipeName);
			
				if(!rename("recipeImages/".$oldImageName.".jpg", "recipeImages/".$newImageName.".jpg")){
					// successfully inserted into database
					$response["success"] = 0;
					$response["message"] = "Failed to rename Image.";

					// echoing JSON response
					echo json_encode($response);
				}else{
				
					// successfully inserted into database and renamed image
					$response["success"] = 1;
					$response["message"] = "Recipe successfully updated.";

					// echoing JSON response
					echo json_encode($response);
				
				}
			}else{
			
				// successfully inserted into database
				$response["success"] = 1;
				$response["message"] = "Recipe successfully updated.";

				// echoing JSON response
				echo json_encode($response);
			}
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
<?php

/*
 * This is for requesting the details of a 
 * single recipe in order to edit it.
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
$conn=$db->connect();

// check for post data
if (isset($_GET["recipeName"])) {
    $recipeName = $_GET['recipeName'];

    // get a product from products table
   $result = mysqli_query($conn, "SELECT summery, prepTime, cookTime, servings, directions, type FROM recipe WHERE recipeName = '$recipeName'");

    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
		
			$rating=0;
			$numRatings=0;
			//$getPic = mysql_query("SELECT img FROM picture WHERE picId = 1") or die(mysql_error());
			
			$response["product"] = array();
			
            $row = mysqli_fetch_array($result);
			//$rowPic = mysql_fetch_array($getPic);
	
			$product = array();
			
			$product["summery"] = $row[0];
			$product["prepTime"] = $row[1];
			$product["cookTime"] = $row[2];
			$product["servings"] = $row[3];
			$product["cookingDirections"] = $row[4];
			$product["type"] = $row[5];
			$product["imageUrl"] = "http://3.bp.blogspot.com/-Hzcfxomkius/TgQ4Do1I5YI/AAAAAAAABkQ/IBIdX39Lq-4/s1600/Golden-Gun-29593.jpg";
		
			//getting all of the ingredients for this recipe
			$ingredientList = mysqli_query($conn, "SELECT ingredientName, amount, measurement, description, important FROM recipeingredients WHERE recipeName = '$recipeName'");
			
			//finding the number of ingredients in the recipe
			$numIngredients = mysqli_num_rows($ingredientList);
			$product["numIngredients"] = $numIngredients;
			
			$i=0;
			//saving all of the ingredients into variables
			while ($row = mysqli_fetch_array($ingredientList)) {

			$product["ingredientName$i"] = $row[0];
			$product["amount$i"] = $row[1];
			$product["measurement$i"] = $row[2];
			$product["description$i"] = $row[3];
			$product["important$i"] = $row[4];
			$i++;
			}

            array_push($response["product"], $product);
			
			$response["success"] = 1;

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No recipe found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No recipe found";

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
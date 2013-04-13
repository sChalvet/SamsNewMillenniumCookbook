<?php

/*
 * This returns the details of a 
 * single recipe thoguh JSON
 */

// array for JSON response
$response = array();

///////////////////////////Connection block//////////////////////////////////////// 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT();
	$conn=$db->connect();
/////////////////////////////////////////////////////////////////////////////////// 

// check for post data
if (isset($_POST["recipeName"])) {
    $recipeName = $_POST['recipeName'];

    // get a product from products table
   $result = mysqli_query($conn, "SELECT userName, ingredientDiscription, directions, prepTime, cookTime, hasImage, servings, recipeId FROM recipe WHERE recipeName = '$recipeName'");

    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
		
			$rating=0;
			$numRatings=0;
			
			$response["product"] = array();
			
            $row = mysqli_fetch_array($result);
			//$rowPic = mysql_fetch_array($getPic);
	
			$product = array();
			
			$product["author"] = $row[0];
			$product["ingredientList"] = $row[1];
			$product["cookingDirections"] = $row[2];
			$product["prepTime"] = $row[3];
			$product["cookTime"] = $row[4];
			$product["hasImage"] = $row[5];
			$product["servings"] = $row[6];
			$product["recipeId"] = $row[7];
			
			//if row[5] (hasImage) is true then its got a url
			if(intval($row[5])){
				//this makes "Test Recipe" into "Test_Recipe" important for searching for images
				$imageName = str_replace(" ", "_", $recipeName);
				$product["imageUrl"] = "recipeImages/".$imageName.".jpg";
			}else{
				$product["imageUrl"] = "no pic";
			}
			
			$countResult = mysqli_query($conn, "SELECT count(*) FROM recipecomments where recipename = '$recipeName'");
			if(mysqli_num_rows($countResult) > 0){
				$CountRow = mysqli_fetch_array($countResult);
				$numRatings=$CountRow[0];
			}
			
			$product["numRatings"] = $numRatings;
			$response["message"] = "Found $numRatings ratings";
			
			$ratingResult = mysqli_query($conn, "SELECT sum(rating) FROM recipecomments where recipename = '$recipeName'");
			if(mysqli_num_rows($ratingResult) > 0){
				$ratingRow = mysqli_fetch_array($ratingResult);
				if($CountRow[0]>0)
					$rating=$ratingRow[0]/$CountRow[0];
			}
			$product["rating"] = round($rating);
			
			//getting all the nutrition facts
			$nutritionInfo = mysqli_query($conn, "SELECT r.ingredientId, r.amount, r.measurement, i.calories, i.fat, i.carbs, i.protein, i.type, i.ingredientName, r.description " 
													."FROM recipeingredients AS r JOIN ingredientlist AS i ON r.ingredientId = i.ingredientId "
													."WHERE i.ingredientId IN (Select ingredientId FROM recipeingredients WHERE recipeName = '$recipeName') "
													."AND r.recipeName = '$recipeName' "
													."GROUP BY r.ingredientId");

													
/*
SELECT r.ingredientId, r.amount, r.measurement, i.calories, i.fat, i.carbs, i.protein, i.type, i.ingredientName, r.description 
FROM recipeingredients AS r JOIN ingredientlist AS i ON r.ingredientId = i.ingredientId
WHERE i.ingredientId IN (Select ingredientId FROM recipeingredients WHERE recipeName = 'Nother Test') 
AND r.recipeName = 'Nother Test'
GROUP BY r.ingredientId
*/
			$n=0;
			while ($rowFacts = mysqli_fetch_array($nutritionInfo)) {
			
				$product["ingredientId$n"] = $rowFacts[0];
				$product["amount$n"] = $rowFacts[1];
				$product["measurement$n"] = $rowFacts[2];
				$product["calories$n"] = $rowFacts[3];
				$product["protein$n"] = $rowFacts[4];
				$product["fat$n"] = $rowFacts[5];
				$product["carbs$n"] = $rowFacts[6];
				$product["type$n"] = $rowFacts[7];
				$product["ingredientName$n"] = $rowFacts[8];
				$product["description$n"] = $rowFacts[9];
				
				$n++;
			
			}
				$product["numIngredients"] = $n;


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
        $response["message"] = "No recipe found, empty results";

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
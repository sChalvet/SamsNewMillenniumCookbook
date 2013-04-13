<?php

/*
 * Following code will list all the recipes that contain the specified ingredients
 */

 //http://localhost/recipeApp/getAllRecipes.php?recipeType=Fish&cookTime=Any&author=Van%20Keizer&keyWord=&foodName=Any
 
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
if (isset($_POST["list0"])) {
    $array[0] = "'".$_POST["list0"]."'";
	$i=1;
	while(isset($_POST["list".$i])){
		$array[$i] = "'".$_POST["list".$i]."'";
		$i++;
	}
	
	$typeOfSearch= $_POST["searchType"];
	
	$ingredientName = implode(', ' , $array);
	
	if($typeOfSearch==0){
		
		$query="SELECT a.recipeName, r.summery, r.userName, r.prepTime, r.cookTime, r.hasImage,  sum(a.important) AS importantTotal
				FROM recipeingredients AS a JOIN recipe AS r
                ON a.recipeName = r.recipeName
				GROUP BY a.recipeName
				HAVING importantTotal = (
				SELECT count(b.important) as importantFound
				FROM recipeingredients AS b
				WHERE b.ingredientName IN ($ingredientName)
				AND b.recipeName = a.recipeName
				AND important = 1
				HAVING importantFound > 0)";
	}else{
		$query="SELECT a.recipeName, r.summery, r.userName, r.prepTime, r.cookTime, r.hasImage, count(a.recipeName) AS count
				FROM recipeingredients AS a JOIN recipe AS r
				ON a.recipeName = r.recipeName
				WHERE a.ingredientName IN ($ingredientName)
				GROUP BY a.recipeName
				HAVING count = ".count($array)."";
	}
	
	// get all products from products table
	$result = mysqli_query($conn, $query);

	
	
	if(!$result){
	
		// no recipes found
		$response["success"] = 0;
		$response["message"] = "No recipes found";

		// echo no users JSON
		echo json_encode($response);
	
	}
	
	
	// check for empty result
	if (mysqli_num_rows($result)) {

		$response["products"] = array();
		
		while ($row = mysqli_fetch_array($result)) {

			$rating=0;
			$numRatings=0;
			
			
			$product = array();
			$product["recipeName"] = $row[0];
			$product["summery"] = $row[1];
			$product["author"] = $row[2];
			$product["prepTime"] = $row[3];
			$product["cookTime"] = $row[4];
			//if row[5] (hasImage) is true then its got a url
			if(intval($row[5])){
				//this makes "Test Recipe" into "Test_Recipe" important for searching for images
				$imageName = str_replace(" ", "_", $row[0]);
				$product["imageUrl"] = "recipeImages/".$imageName.".jpg";
			}else{
				$product["imageUrl"] = "no pic";
			}
			
			$countResult = mysqli_query($conn, "SELECT count(*) FROM recipecomments where recipename = '$row[0]'");
			if(mysqli_num_rows($countResult) > 0){
				$CountRow = mysqli_fetch_array($countResult);
				$numRatings=$CountRow[0];
			}
			
			$product["numRatings"] = $numRatings;
			$response["message"] = "Found $numRatings ratings";
			
			$ratingResult = mysqli_query($conn, "SELECT sum(rating) FROM recipecomments where recipename = '$row[0]'");
			if(mysqli_num_rows($ratingResult) > 0){
				$ratingRow = mysqli_fetch_array($ratingResult);
				if($CountRow[0]>0)
					$rating=$ratingRow[0]/$CountRow[0];
			}	
			$product["rating"] = round($rating);
			$response["message"] .= ", found total rating of $rating";

			// push single recipe into final response array
			array_push($response["products"], $product);
		}
		// success
		$response["message"] = "Success";
		$response["success"] = 1;

		// echoing JSON response
		echo json_encode($response);
	} else {
		// no products found
		$response["success"] = 0;
		$response["message"] = "No recipes found";

		// echo no users JSON
		echo json_encode($response);
	}
}else{

	// did not receive data
	$response["success"] = 0;
	$response["message"] = "Missing field(s)";

	// echo no users JSON
	echo json_encode($response);

}
?>

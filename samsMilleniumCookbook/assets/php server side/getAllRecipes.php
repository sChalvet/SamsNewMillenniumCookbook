<?php

/*
 * Following code will list all the recipes
 */

 //http://localhost/recipeApp/getAllRecipes.php?recipeType=Fish&cookTime=Any&author=Van%20Keizer&keyWord=&foodName=Any
 
// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
$conn=$db->connect();

// check for post data
if (isset($_POST["foodName"])) {
	
	$foodName = mysqli_real_escape_string($conn, $_POST['foodName']);
	$author = mysqli_real_escape_string($conn, $_POST['author']);
	$recipeType = mysqli_real_escape_string($conn, $_POST['recipeType']);
	$keyWord = mysqli_real_escape_string($conn, $_POST['keyWord']);
	$cookTime = mysqli_real_escape_string($conn, $_POST['cookTime']);
	
	//used to store the query
	$cookTimeQuery="";
	$foodNameQuery="";
	$recipeTypeQuery="";
	$authorQuery="";
	$keyWordQuery="";
	
	//used to decide if query is being used
	$iscookTime=false;
	$isfoodName=false;
	$isrecipeType=false;
	$isauthor=false;
	$iskeyWord=false;
	
	$query="SELECT r.recipeName, r.summery, r.userName, r.prepTime, r.cookTime, r.hasImage FROM recipe AS r";
	
	if($recipeType !== "Any"){
		$recipeTypeQuery=" WHERE type = '$recipeType'";
		$isrecipeType=true;
		//echo "inside recipetype </br>";
	}
	
	if($cookTime !== "Any"){
		
		switch ($cookTime) {
		case "10 min or Less":
        $num=" <= 10";
        break;
		case "20 min":
        $num=" <= 20";
        break;
		case "30 min":
        $num=" <= 30";
        break;
		case "40 min":
        $num=" <= 40";
        break;
		case "50 min":
        $num=" <= 50";
        break;
		case "1 hour":
        $num= "<= 60";
        break;
		case "2 hour":
        $num=" <= 120";
        break;
		case "Longer than 2 hours":
        $num=" > 120" ;
        break;
		case "Over Night":
        $num=" > 360";
        break;
}
		//echo "inside case </br>";
		$cookTimeQuery=" r.cookTime+r.prepTime $num";
		$iscookTime=true;
	}
	
	
	if($author !== ""){
		//echo "inside author if</br>";
		$authorQuery=" r.userName LIKE '%$author%'";
		$isauthor=true;
	}
	
	if($keyWord !== ""){
		//echo "inside keyWord if</br>";
		
		$keyWordArray= explode(" ", $keyWord);
		$keyWordSearch = "";
		
		for($i=0; $i<count($keyWordArray); $i++){
			$keyWordSearch .=  ($i > 0 )? " OR ":"";
			
			$keyWordSearch .= "r.recipeName LIKE '%$keyWordArray[$i]%' "
								."OR i.ingredientName LIKE '%$keyWordArray[$i]%' "
								."OR r.summery LIKE '%$keyWordArray[$i]%' "
								."OR l.type LIKE '%$keyWordArray[$i]%' "
								."OR r.directions LIKE '%$keyWordArray[$i]%' "
								."OR r.type LIKE '%$keyWordArray[$i]%' ";
		}
		
		$keyWordQuery=" r.recipeName IN (SELECT r.recipeName "
							."FROM recipe AS r "
							."JOIN recipeingredients AS i ON r.recipeName = i.recipeName "
							."JOIN ingredientlist AS l ON i.ingredientName = l.ingredientName "
							."WHERE $keyWordSearch "
							."GROUP BY r.recipeName)";
		$iskeyWord=true;
	}
	
	if($foodName !== "Any"){
	
		$foodNameQuery=" r.recipeName IN (SELECT r.recipeName, r.summery, r.userName, r.prepTime, r.cookTime, r.hasImage "
							."FROM recipe AS r "
							."JOIN recipeingredients AS i ON r.recipeName = i.recipeName "
							."JOIN ingredientlist AS l ON i.ingredientName = l.ingredientName "
							."WHERE l.type  = '$foodName' "
							."AND i.important = 1 "
							."GROUP BY r.recipeName)";
		$isfoodName=true;
	}
	
	//adding AND if any of the previous query's are not empty	
	if( $isrecipeType && $iscookTime )
		$cookTimeQuery = " AND".$cookTimeQuery;
	if( ($isrecipeType || $iscookTime) &&  $isauthor )
		$authorQuery = " AND".$authorQuery;
	if( ($isrecipeType || $iscookTime ||  $isauthor) && $iskeyWord )
		$keyWordQuery = " AND".$keyWordQuery;
	if( ($isrecipeType || $iscookTime ||  $isauthor || $iskeyWord) && $isfoodName )
		$foodNameQuery = " AND".$foodNameQuery;
	if( !$isrecipeType && ($isfoodName || $iscookTime ||  $isauthor || $iskeyWord))
		$query.=" WHERE";
	
	
	// get all products from products table
	$result = mysqli_query($conn, $query.$recipeTypeQuery.$cookTimeQuery.$authorQuery.$keyWordQuery.$foodNameQuery);
	
	$temp=$query.$recipeTypeQuery.$cookTimeQuery.$authorQuery.$keyWordQuery.$foodNameQuery;
	//echo "</br>$temp </br></br>$isrecipeType,  $iscookTime,  $isauthor, $iskeyWord, $isfoodName</br>";
	
	
	if(!$result){
	
		// no recipes found
		$response["success"] = 0;
		$response["message"] = "No recipes found, query= $temp";

		// echo no users JSON
		echo json_encode($response);
		die();
	
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
			
			$ratingResult = mysqli_query($conn, "SELECT sum(rating) FROM recipecomments where recipename = '$row[0]'");
			if(mysqli_num_rows($ratingResult) > 0){
				$ratingRow = mysqli_fetch_array($ratingResult);
				if($CountRow[0]>0)
					$rating=$ratingRow[0]/$CountRow[0];
			}	
			$product["rating"] = round($rating);

			// push single recipe into final response array
			array_push($response["products"], $product);
		}
		// success
		$response["message"] = ", Query= $temp";
		$response["success"] = 1;

		// echoing JSON response
		echo json_encode($response);
	} else {
		// no products found
		$response["success"] = 0;
		$response["message"] = "No recipes found, Query= $temp";

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

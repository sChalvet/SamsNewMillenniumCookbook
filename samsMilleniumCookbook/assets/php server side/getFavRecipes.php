<?php

/**
 * Following code will list recipes that 
 * match the contents of an array
 */

if (isset($_GET["list0"])) {
    $array[0] = "'".$_GET["list0"]."'";
	$i=1;
	while(isset($_GET["list".$i])){
		$array[$i] = "'".$_GET["list".$i]."'";
		$i++;
	}
	
	$recipeName = implode(', ' , $array);
	
	//var_dump( $_GET);
	//var_dump( $recipeName);
	
	// array for JSON response
	$response = array();

	// include db connect class
	require_once __DIR__ . '/db_connect.php';

	// connecting to db
	$db = new DB_CONNECT();
	$conn=$db->connect();
	
	// get all products from products table
	$result = mysqli_query($conn, "SELECT recipeName, summery, userName, prepTime, cookTime FROM recipe WHERE recipeName IN ($recipeName)") or die(mysql_error());


	// check for empty result
	if (mysqli_num_rows($result) > 0) {
		// looping through all results
		// products node
		$response["products"] = array();
		//$getPic = mysql_query("SELECT img FROM recipe WHERE picId = '1'") or die(mysql_error());
		
		while ($row = mysqli_fetch_array($result)) {
			
			$rating=0;
			$numRatings=0;
			
			$product = array();
			$product["recipeName"] = $row[0];
			$product["summery"] = $row[1];
			$product["author"] = $row[2];
			$product["prepTime"] = $row[3];
			$product["cookTime"] = $row[4];
			
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
			$product["rating"] = $rating;
			$response["message"] .= ", found total rating of $rating";

			// push single recipe into final response array
			array_push($response["products"], $product);
		}
		// success
		$response["success"] = 1;

		
		
		// echoing JSON response
		echo json_encode($response);
		
	} else {
		// no products found
		$response["success"] = 0;
		$response["message"] = "No products found";

		// echo no users JSON
		echo json_encode($response);
	}
	
	}else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
	echo json_encode($response);
}
?>

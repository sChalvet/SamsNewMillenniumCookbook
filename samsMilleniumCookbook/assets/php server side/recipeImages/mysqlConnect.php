<?php

/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();


// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();
$response["product"] = array();
$product = array();
$ingredientName='Almond';

    // get a product from products table
   // $result = mysql_query("SELECT calories, protein, fat, carbs FROM ingredientlist WHERE ingredientName = '$ingredientName'");
		$result = mysql_query("SELECT type FROM ingredientlist WHERE ingredientName = '$ingredientName'");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {

			
            $row = mysql_fetch_array($result);

			
			$product["type"] = $row[0];
			
			/*$product["calories"] = $row[0];
			$product["protein"] = $row[1];
			$product["fat"] = $row[2];
			$product["carbs"] = $row[3];
			$product["notes"] = $row[4];
			$product["addedBy"] = $row[5];
			$product["type"] = $row[6];
			
				//preference access
				SharedPreferences prefs;
				String userName="";
				String password="";
				
				//setting user name and password from preferences
				prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				userName =prefs.getString("nickName", "guest");
				password =prefs.getString("password", "");
			
			android:background="@drawable/background"
			                <RatingBar
                    android:id="@+id/ratingBarListRecipe"
                    style="@style/recipeRatingBar"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:isIndicator="true"
                    android:numStars="2"
                    android:stepSize="0.1" />
					
					android:listSelector="@drawable/list_ingredient_gradient_selector"
					
					
					
	<string name="urlGetIngredientDetails">http://joe.cs.unca.edu/~chalvesa/app/getIngredientDetails.php</string>
    <string name="urlUpdateIngredient">http://joe.cs.unca.edu/~chalvesa/app/updateIngredient.php</string>
    <string name="urlDeleteIngredient">http://joe.cs.unca.edu/~chalvesa/app/deleteIngredient.php</string>
    <string name="urlCreateNewIngredient">http://joe.cs.unca.edu/~chalvesa/app/createIngredient.php</string>
    <string name="urlGetAllComments">http://joe.cs.unca.edu/~chalvesa/app/getAllComments.php</string>
    <string name="url_create_product">http://joe.cs.unca.edu/~chalvesa/app/create_product.php</string>
    <string name="urlCreateRecipe">http://joe.cs.unca.edu/~chalvesa/app/createRecipe.php</string>
    <string name="urlUpdateRecipe">http://joe.cs.unca.edu/~chalvesa/app/updateRecipe.php</string>
    <string name="urlCreateComment">http://joe.cs.unca.edu/~chalvesa/app/createComment.php</string>
    <string name="urlGetAllRecipes">http://joe.cs.unca.edu/~chalvesa/app/getAllRecipes.php</string>
    <string name="urlGetAllIngredients">http://joe.cs.unca.edu/~chalvesa/app/getAllIngredients.php</string>
    <string name="urlGetFavRecipes">http://joe.cs.unca.edu/~chalvesa/app/getFavRecipes.php</string>
    <string name="urlGetRecipeDetails">http://joe.cs.unca.edu/~chalvesa/app/getRecipeDetails.php</string>
    <string name="urlEditRecipe">http://joe.cs.unca.edu/~chalvesa/app/editRecipe.php</string>
    <string name="urlCreateAccount">http://joe.cs.unca.edu/~chalvesa/app/createAccount.php</string>
    <string name="urlLogin">http://joe.cs.unca.edu/~chalvesa/app/logIn.php</string>
    <string name="urlEditComment">http://joe.cs.unca.edu/~chalvesa/app/editComment.php</string>
    <string name="urlUpdateComment">http://joe.cs.unca.edu/~chalvesa/app/updateComment.php</string>
    <string name="urlGetAllRecipesByIngredient">http://joe.cs.unca.edu/~chalvesa/app/getAllRecipesByIngredient.php</string>
    <string name="urlUploadImage">http://joe.cs.unca.edu/~chalvesa/app/uploadImage.php</string>
    <string name="urlResetPassword">http://joe.cs.unca.edu/~chalvesa/app/resetPassword.php</string>
    <string name="urlGetTestQuestion">http://joe.cs.unca.edu/~chalvesa/app/getTestQuestion.php</string>
    <string name="urlUpdateAccount">http://joe.cs.unca.edu/~chalvesa/app/updateAccount.php</string>
    <string name="urlGetUserInfo">http://joe.cs.unca.edu/~chalvesa/app/getUserInfo.php</string>
    <string name="urlRoot">http://joe.cs.unca.edu/~chalvesa/app/</string>				
					
					
					
	<string name="urlGetIngredientDetails">http://192.168.254.45/recipeApp/getIngredientDetails.php</string>
    <string name="urlUpdateIngredient">http://192.168.254.45/recipeApp/updateIngredient.php</string>
    <string name="urlDeleteIngredient">http://192.168.254.45/recipeApp/deleteIngredient.php</string>
    <string name="urlCreateNewIngredient">http://192.168.254.45/recipeApp/createIngredient.php</string>
    <string name="urlGetAllComments">http://192.168.254.45/recipeApp/getAllComments.php</string>
    <string name="url_create_product">http://192.168.254.45/recipeApp/create_product.php</string>
    <string name="urlCreateRecipe">http://192.168.254.45/recipeApp/createRecipe.php</string>
    <string name="urlUpdateRecipe">http://192.168.254.45/recipeApp/updateRecipe.php</string>
    <string name="urlCreateComment">http://192.168.254.45/recipeApp/createComment.php</string>
    <string name="urlGetAllRecipes">http://192.168.254.45/recipeApp/getAllRecipes.php</string>
    <string name="urlGetAllIngredients">http://192.168.254.45/recipeApp/getAllIngredients.php</string>
    <string name="urlGetFavRecipes">http://192.168.254.45/recipeApp/getFavRecipes.php</string>
    <string name="urlGetRecipeDetails">http://192.168.254.45/recipeApp/getRecipeDetails.php</string>
    <string name="urlEditRecipe">http://192.168.254.45/recipeApp/editRecipe.php</string>
    <string name="urlCreateAccount">http://192.168.254.45/recipeApp/createAccount.php</string>
    <string name="urlLogin">http://192.168.254.45/recipeApp/logIn.php</string>
    <string name="urlEditComment">http://192.168.254.45/recipeApp/editComment.php</string>
    <string name="urlUpdateComment">http://192.168.254.45/recipeApp/updateComment.php</string>
    <string name="urlGetAllRecipesByIngredient">http://192.168.254.45/recipeApp/getAllRecipesByIngredient.php</string>
    <string name="urlUploadImage">http://192.168.254.45/recipeApp/uploadImage.php</string>
    <string name="urlResetPassword">http://192.168.254.45/recipeApp/resetPassword.php</string>
    <string name="urlGetTestQuestion">http://192.168.254.45/recipeApp/getTestQuestion.php</string>
    <string name="urlUpdateAccount">http://192.168.254.45/recipeApp/updateAccount.php</string>
    <string name="urlGetUserInfo">http://192.168.254.45/recipeApp/getUserInfo.php</string>
    <string name="urlRoot">http://192.168.254.45/recipeApp/</string>	
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
	// Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item){
	 
	        switch (item.getItemId()){
	 
	        case R.id.menuHome:
	        	Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
				// Closing all previous activities
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
	            return true;
	 
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }    				
			
			$product["ingredientName"] = $row[0];
			$product["calories"] = $row[1];
			$product["protein"] = $row[2];
			$product["fat"] = $row[3];
			$product["carbs"] = $row[4];
			$product["notes"] = $row[5];
			$product["addedBy"] = $row[6];
			$product["type"] = $row[7];
			$product["dateCreated"] = $row[8];
			$product["dateUpdated"] = $row[9];*/
			
            $response["success"] = 1;

            // user node
            

            array_push($response["product"], $product);

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No product found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No product found";

        // echo no users JSON
        echo json_encode($response);
    }

?>
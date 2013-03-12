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
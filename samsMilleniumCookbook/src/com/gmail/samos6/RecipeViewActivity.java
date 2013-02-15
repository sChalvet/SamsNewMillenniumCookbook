package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class RecipeViewActivity extends Activity {
	

	TextView txtRecipeName;
	TextView txtAuthor;
	TextView txtNumReviews;
	TextView txtIngredientList;
	TextView txtCookingDirections;
	TextView txtCookTime;
	TextView txtPrepTime;
	RatingBar rtbRating;
	ImageView imgPicture;
	SlidingDrawer slidingDrawer;
	Button btnReviews;
	
	String recipeName;
	String author= "";
	String numReviews= "";
	String ingredientList= "";
	String cookingDirections= "";
	String rating= "";
	String cookTime= "Cook time: ";
	String prepTime= "Prep time: ";
	

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single ingredient url
	private static final String urlGetRecipeDetails = "http://10.0.2.2/recipeApp/getRecipeDetails.php";
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_NUMREVIEWS = "numReviews";
	private static final String TAG_INGREDIENTLIST = "ingredientList";
	private static final String TAG_COOKINGDIRECTIONS = "cookingDirections";
	private static final String TAG_RATINGS = "rating";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_PREPTIME = "prepTime";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_recipe);
		
		
		// getting ingredient details from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		recipeName = intent.getStringExtra(TAG_RECIPENAME);
			
		// Initializing all of the text fields and buttons
		btnReviews = (Button) findViewById(R.id.btnRecipeViewReviews);
		txtRecipeName = (TextView) findViewById(R.id.txtRecipeViewRecipeName);
		txtAuthor = (TextView) findViewById(R.id.txtRecipeViewAuthor);
		txtNumReviews= (TextView) findViewById(R.id.txtRecipeViewNumReviews);
		txtIngredientList= (TextView) findViewById(R.id.txtRecipeViewIngredientList);
		txtCookingDirections= (TextView) findViewById(R.id.txtRecipeViewDirections);
		txtCookTime= (TextView) findViewById(R.id.txtRecipeViewCooktime);
		txtPrepTime= (TextView) findViewById(R.id.txtRecipeViewPreptime);
		imgPicture= (ImageView) findViewById(R.id.recipeViewImage);	
		slidingDrawer= (SlidingDrawer) findViewById(R.id.recipeViewSlidingDrawer);
		rtbRating= (RatingBar) findViewById(R.id.recipeViewRatingBar);
			
		Log.d("ViewRecipe_just in", recipeName);
	
		new GetRecipeDetails().execute();
		
		// save button click event
		btnReviews.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Log.d("ViewRecipe_btnReviews onclick", "inside");

			}
		});

	}
	
	/***************************************************************************************************
	 *  									void addDetails()
	 *  
	 *  			This method adds all of the recipe details from the query. 
	 * 
	 ****************************************************************************************************/
	public void addDetails(){
		
		
		txtRecipeName.setText(recipeName);
		txtAuthor.setText(author);
		txtNumReviews.setText(numReviews);
		txtIngredientList.setText(ingredientList);
		txtCookingDirections.setText(cookingDirections);
		txtCookTime.setText(cookTime);
		txtPrepTime.setText(prepTime);
		//rtbRating.setLabelFor(5);//.setText(rating);
		
	}

	/**
	 * Background Async Task to Get complete recipe details
	 * */
	class GetRecipeDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RecipeViewActivity.this);
			pDialog.setMessage("Loading Recipe details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... args) {

					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("recipeName", recipeName));

						// getting Ingredient details by making HTTP request
						JSONObject json = jsonParser.makeHttpRequest( urlGetRecipeDetails, "GET", params);

						// check your log for json response
						Log.d("Single recipe Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray products = json.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first ingredient object from JSON Array
							JSONObject product = products.getJSONObject(0);

							//Getting details from the query
							Log.d("RecipeView_DoinBackGround", "setting all of the details");
							author = product.getString(TAG_AUTHOR);
							numReviews = product.getString(TAG_NUMREVIEWS);
							ingredientList = product.getString(TAG_INGREDIENTLIST);
							cookingDirections = product.getString(TAG_COOKINGDIRECTIONS);
							rating = product.getString(TAG_RATINGS);
							prepTime += product.getString(TAG_PREPTIME);
							cookTime += product.getString(TAG_COOKTIME);
							

						}else{	
							// ingredient with that name not found
							//Toast.makeText(getApplicationContext(), "Nothing found", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
			/*	}
			});*/
					
					Log.d("RecipeView_DoinBackGround", "before return");
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("RecipeView_PostExecute", "in post execute");
			//is called to add all of the details to the fields
			addDetails();
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

}

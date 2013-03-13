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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	TextView txtServings;
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
	Button btnSave;
	Button btnFavorite;
	Button btnEdit;
	
	//preference access
	SharedPreferences prefs;
	String userName="";
	String password="";
	
	String recipeName;
	String author= "";
	String servings= "";
	String numRatings= "";
	String ingredientList= "";
	String cookingDirections= "";
	String rating= "";
	String cookTime= "Cook time: ";
	String prepTime= "Prep time: ";
	String rawImage= "";
	
	Bitmap pic;
	int hasImage=0;
	
	// single ingredient url
	String urlGetRecipeDetails;	
	String urlRoot;				//stores the root url
    String imageUrl = "";		//stores the file path
    
    // ImageLoader class instance
    Images_ImageLoader imgLoader;
	
	//used to see if user canceled the AsyncTask
    Boolean bCancelled=false;
    Boolean successful=false;
	String message="";

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_NUMRATINGS = "numRatings";
	private static final String TAG_INGREDIENTLIST = "ingredientList";
	private static final String TAG_COOKINGDIRECTIONS = "cookingDirections";
	private static final String TAG_RATINGS = "rating";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_PREPTIME = "prepTime";
	private static final String TAG_SERVINGS = "servings";
	private static final String TAG_IMAGEURL = "imageUrl";
	private static final String TAG_HASIMAGE = "hasImage";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_recipe);
		
		imgLoader = new Images_ImageLoader(getApplicationContext());
		
		//setting user name and password from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		userName =prefs.getString("nickName", "guest");
		password =prefs.getString("password", "");
		
		//getting url from resources
		urlGetRecipeDetails = getResources().getString(R.string.urlGetRecipeDetails);
		urlRoot = getResources().getString(R.string.urlRoot);
		
		// getting ingredient details from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		recipeName = intent.getStringExtra(TAG_RECIPENAME);
			
		// Initializing all of the text fields and buttons
		btnReviews = (Button) findViewById(R.id.btnRecipeViewViewRatings);
		btnSave = (Button) findViewById(R.id.btnRecipeViewSaveRecipe);
		btnFavorite = (Button) findViewById(R.id.btnRecipeViewFavoriteRecipe);
		btnEdit = (Button) findViewById(R.id.btnRecipeViewEdit);
		
		txtRecipeName = (TextView) findViewById(R.id.txtRecipeViewRecipeName);
		txtServings = (TextView) findViewById(R.id.txtRecipeViewServings);
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
		
		// See Reviews button click event
		btnReviews.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(!userName.equalsIgnoreCase("guest")){
					Log.d("ViewRecipe_btnReviews onclick", "inside");
					Intent intent = new Intent(getApplicationContext(), ListRecipeCommentsActivity.class);
					
					// sending recipeName to next activity
					intent.putExtra(TAG_RECIPENAME, recipeName);
					
					// starting new activity and expecting some response back
					startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "I'm sorry you must first login", Toast.LENGTH_LONG).show();
				}

			}
		});
		
		// Save Recipe For latter button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Log.d("ViewRecipe_btnSave onclick", "inside");
				db.addSavedRecipe(recipeName);
				Toast.makeText(getApplicationContext(), recipeName+" has been saved for later", Toast.LENGTH_LONG).show();

			}
		});
		
		// Favorite Recipe button click event
		btnFavorite.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Log.d("ViewRecipe_btnFavorite onclick", "inside");
				db.addFavoriteRecipe(recipeName);
				Toast.makeText(getApplicationContext(), recipeName+" has been added to your Favorite Recipes", Toast.LENGTH_LONG).show();

			}
		});
		
		// Favorite Recipe button click event
		btnEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Log.d("ViewRecipe_btnEdit onclick", "inside");
				
				if(userName.equalsIgnoreCase(author)){
					Intent intent = new Intent(getApplicationContext(), EditRecipeActivity.class);
					
					// sending recipeName to next activity
					intent.putExtra(TAG_RECIPENAME, recipeName);
					
					//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					// starting new activity and expecting some response back
					startActivityForResult(intent, 100);
				}else{
					Toast.makeText(getApplicationContext(), "Sorry "+userName+" only "+author+" can edit this recipe.", Toast.LENGTH_LONG).show();
				}
			}
		});

	}
	
	// Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_view_menu, menu);
        return true;
    }
	
	// Response from EditRecipe Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user may have changed the recipe
			// reload this screen again
			Intent intent = getIntent();
			recipeName = intent.getStringExtra(TAG_RECIPENAME);
			finish();
			startActivity(intent);
		}

	}
	
	
	/***************************************************************************************************
	 *  									void addDetails()
	 *  
	 *  			This method adds all of the recipe details from the query. 
	 * 
	 ****************************************************************************************************/
	public void addDetails(){
		
		//pic = BitmapFactory.decodeFile(rawImage);
		Log.d("RecipeView_addDetails image=", rawImage);
		txtRecipeName.setText(recipeName);
		txtAuthor.setText(author);
		txtNumReviews.setText(numRatings);
		txtIngredientList.setText(ingredientList);
		txtCookingDirections.setText(cookingDirections);
		txtCookTime.setText(cookTime);
		txtPrepTime.setText(prepTime);
		txtServings.setText(servings);
		if(hasImage==1)
			imgLoader.DisplayImage(urlRoot+imageUrl, imgPicture);
		rtbRating.setRating(Float.valueOf(rating)/2);
		
	}
	
	/**
	 * Enables user to cancel the AsychTask by hitting the back button
	 */
	OnCancelListener cancelListener=new OnCancelListener(){
	    @Override
	    public void onCancel(DialogInterface arg0){
	    	//used to see if user canceled the AsyncTask
	    	bCancelled=true;
	        finish();
	    }
	};

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
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... args) {

					// Check for success tag
					int success;
					//if the AsyncTask has Not been cancelled then continue
					if(!bCancelled) try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair(TAG_RECIPENAME, recipeName));

						// getting Ingredient details by making HTTP request
						JSONObject json = jsonParser.makeHttpRequest( urlGetRecipeDetails, "POST", params);

						// check your log for json response
						Log.d("Single recipe Details", json.toString());
						
						//reset variable just in case
						successful=false;
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							
							successful=true;
							// successfully received product details
							JSONArray products = json.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first ingredient object from JSON Array
							JSONObject product = products.getJSONObject(0);

							//Getting details from the query
							Log.d("RecipeView_DoinBackGround", "setting all of the details");
							author = product.getString(TAG_AUTHOR);
							numRatings = product.getString(TAG_NUMRATINGS);
							ingredientList = product.getString(TAG_INGREDIENTLIST);
							cookingDirections = product.getString(TAG_COOKINGDIRECTIONS);
							rating = product.getString(TAG_RATINGS);
							prepTime += product.getString(TAG_PREPTIME);
							cookTime += product.getString(TAG_COOKTIME);
							imageUrl = product.getString(TAG_IMAGEURL);
							servings = product.getString(TAG_SERVINGS);
							hasImage = Integer.parseInt(product.getString(TAG_HASIMAGE));
							

						}else{	
							// recipe with that name not found
							message= json.getString(TAG_MESSAGE);
							Log.d("Recipe View failed: ", message);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					
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
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item){
	 
	        switch (item.getItemId()){
	 
	        case R.id.menuHome:
	        	Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
				// Closing all previous activities
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
	            return true;
	            
	        case R.id.share:
	            String message = userName+" "+getString(R.string.likes)+" "+recipeName+" from "+getString(R.string.app_name);
        		Intent share = new Intent(Intent.ACTION_SEND);
        		share.setType("text/plain");
        		share.putExtra(Intent.EXTRA_TEXT, message);
        		//share.putExtra(Intent.EXTRA_SUBJECT, message);

        		startActivity(Intent.createChooser(share, getString(R.string.howShare)));
	            return true;
	            
	            

	 
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }

}

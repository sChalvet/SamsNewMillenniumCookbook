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
import android.graphics.Bitmap;
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
public class CreateRecipeActivity extends Activity {
	

	EditText txtRecipeName;
	EditText txtIngredientList;
	EditText txtCookingDirections;
	EditText txtCookTime;
	EditText txtPrepTime;
	EditText txtSummery;
	Spinner spnrRecipeType;
	ImageView imgPicture;
	SlidingDrawer slidingDrawer;
	
	Button btnPublish;
	Button btnTakePhoto;
	
	String recipeName= "";
	String ingredientList= "";
	String cookingDirections= "";
	String cookTime= "";
	String prepTime= "";
	String summery= "";
	String author= "Van Keizer";
	String[] recipeType;
	
	boolean successful = false;
	String message = "";
	
	ArrayAdapter<String> spin_adapter;

	// Progress Dialog
	private ProgressDialog pDialog;
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single ingredient url
	//private static final String urlCreateRecipe = "http://10.0.2.2/recipeApp/createRecipe.php";
	//private static final String urlUpdateRecipe = "http://10.0.2.2/recipeApp/updateRecipe.php";
	String urlCreateRecipe;
	String urlUpdateRecipe; 
	
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
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_TYPE = "type";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_recipe);
		
		//getting url from resources
		urlCreateRecipe = getResources().getString(R.string.urlCreateRecipe);
		urlUpdateRecipe = getResources().getString(R.string.urlUpdateRecipe);
		
		recipeType = getResources().getStringArray(R.array.recipeType);
		
		
		//setting the recipe type spinner
		spnrRecipeType = (Spinner) findViewById(R.id.spnrCreateRecipeType);
		spin_adapter = new ArrayAdapter<String>(CreateRecipeActivity.this, android.R.layout.simple_spinner_item, recipeType);
		spnrRecipeType.setAdapter(spin_adapter);
		
		//implement a call to this activity to edit recipe
		if(false){
			// getting ingredient details from intent
			Intent intent = getIntent();
			
			// getting data past from intent
			recipeName = intent.getStringExtra(TAG_RECIPENAME);
		}
			
		// Initializing all of the text fields and buttons
		txtRecipeName = (EditText) findViewById(R.id.inputCreateRecipeName);
		txtIngredientList= (EditText) findViewById(R.id.inputRecipeCreateIngredientList);
		txtCookingDirections= (EditText) findViewById(R.id.inputRecipeCreateDirections);
		txtCookTime= (EditText) findViewById(R.id.inputRecipeCreateCooktime);
		txtPrepTime= (EditText) findViewById(R.id.inputRecipeCreatePreptime);
		txtSummery= (EditText) findViewById(R.id.inputRecipeCreateSummery);
		imgPicture= (ImageView) findViewById(R.id.recipeCreateImage);	
		slidingDrawer= (SlidingDrawer) findViewById(R.id.recipeCreateSlidingDrawer);
		
		btnPublish= (Button) findViewById(R.id.btnCreateRecipeSubmit);
		btnTakePhoto= (Button) findViewById(R.id.btnCreateRecipeTakePhoto);
			
		Log.d("CreateRecipe_just in", "Inside");
	
		
		
		// publish to DB button click event
		btnPublish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d("CreateRecipe_btnPost onclick", "inside");
				
				new CreateNewRecipe().execute();
				

			}
		});
		
		// take pic button click event
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d("CreateRecipe_btnTakePhoto onclick", "inside");
				
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
				

			}
		});

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
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 0){
			Bitmap theImage = (Bitmap) data.getExtras().get("data");
			imgPicture.setImageBitmap(theImage);
			
		}
		
		
	}
	
	/**
	 * Background Async Task to Create new Recipe
	 * */
	class CreateNewRecipe extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateRecipeActivity.this);
			pDialog.setMessage("Creating Recipe..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
			
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			
			String recipeName= txtRecipeName.getText().toString();
			String ingredientList= txtIngredientList.getText().toString();
			String cookingDirections= txtCookingDirections.getText().toString();
			String cookTime= txtCookTime.getText().toString();
			String prepTime= txtPrepTime.getText().toString();
			String summery= txtSummery.getText().toString();
			String recipeType = spnrRecipeType.getSelectedItem().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_RECIPENAME, recipeName));
			params.add(new BasicNameValuePair(TAG_INGREDIENTLIST, ingredientList));
			params.add(new BasicNameValuePair(TAG_COOKINGDIRECTIONS, cookingDirections));
			params.add(new BasicNameValuePair(TAG_COOKTIME, cookTime));
			params.add(new BasicNameValuePair(TAG_PREPTIME, prepTime));
			params.add(new BasicNameValuePair(TAG_SUMMERY, summery));
			params.add(new BasicNameValuePair(TAG_TYPE, recipeType));
			params.add(new BasicNameValuePair(TAG_AUTHOR, author));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(urlCreateRecipe, "GET", params);

			//if asyncTask has Not been cancelled then continue
			if (!bCancelled) try {
				
				// check log cat for response
				Log.d("Create Response", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successful=true;
					// successfully created Recipe
					//Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
					//startActivity(i);
					Log.d("CreateRecipe_Background", "Success! Recipe Created");
					// closing this screen
					finish();
				} else {
					// failed to create Recipe
					 message = json.getString(TAG_MESSAGE);
					Log.d("CreateRecipe_Background", "oops! Failed to create recipe"+message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			if(successful)
				Toast.makeText(getApplicationContext(), "Recipe Created", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}

	}

}


/***************************************************************************************************************/


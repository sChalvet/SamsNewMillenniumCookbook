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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditIngredientActivity extends Activity {
	

	EditText txtCalories;
	EditText txtProtein;
	EditText txtFat;
	EditText txtCarbs;
	EditText txtNotes;
	EditText txtIngredientName;
	TextView txtAddedBy;
	Spinner spnrType;
	
	
	Button btnSave;
	Button btnPublish;
	Button btnDelete;


	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single ingredient url
	private static final String urlGetIngredientDetails = "http://10.0.2.2/recipeApp/getIngredientDetails.php";

	// url to update product
	private static final String urlUpdateIngredient = "http://10.0.2.2/recipeApp/updateIngredient.php";
		
	// url to delete product
	private static final String url_delete_product = "http://10.0.2.2/recipeApp/delete_product.php";

	// url to update product
	private static final String urlCreateNewIngredient = "http://10.0.2.2/recipeApp/createIngredient.php";
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_INGREDIENTNAME = "ingredientName";
	private static final String TAG_CALORIES = "calories";
	private static final String TAG_PROTEIN = "protein";
	private static final String TAG_FAT = "fat";
	private static final String TAG_CARBS = "carbs";
	private static final String TAG_NOTES = "notes";
	private static final String TAG_ADDEDBY = "addedBy";
	private static final String TAG_TYPE = "type";
	//private static final String TAG_DATECREATED = "dateCreated";
	//private static final String TAG_DATEUPDATED = "dateUpdated";
	
	private static final String TAG_LISTINGREDIENT = "listIngredient";
	private static final String TAG_PANTRY = "pantry";
	private static final String TAG_ADDINGREDIENT = "addIngredient";
	private static final String TAG_ORIGIN = "origin";
	
	ArrayAdapter<String> spin_adapter; //used for the food type spinner
	
	String calories = "";
	String protein = "";
	String fat = "";
	String carbs = "";
	String type = "";
	String notes = "null";
	String addedBy = "";
	String ingredientName;
	String [] foodType;//will be set in onCreate
	Boolean canEdit=false;
	String origin= "";
	String user= "";
	String defaultDiscription="";
	String message="";
	Boolean successful= false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_ingredient);
		
		//getting the foodType Array from the resources
		foodType = getResources().getStringArray(R.array.foodType);
		defaultDiscription= getResources().getString(R.string.defaultDiscription);
		
		// getting ingredient details from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		ingredientName = intent.getStringExtra(TAG_INGREDIENTNAME);
		origin = intent.getStringExtra(TAG_ORIGIN);
		
		//getting the user name
		user="Van Keize";
		//user=db.getUserName();
	
		
		// Initializing all of the text fields and buttons
		btnSave = (Button) findViewById(R.id.btnSaveEditIngredient);
		btnPublish = (Button) findViewById(R.id.btnPublishIngredient);
		btnDelete = (Button) findViewById(R.id.btnDeleteEditIngredient);
		
		txtNotes = (EditText) findViewById(R.id.inputNotes);
		txtCalories = (EditText) findViewById(R.id.inputCalorie);
		txtProtein= (EditText) findViewById(R.id.inputProtein);
		txtFat= (EditText) findViewById(R.id.inputFat);
		txtCarbs= (EditText) findViewById(R.id.inputCarbs);
		txtIngredientName= (EditText) findViewById(R.id.txtviewIngredientName);
		
		txtAddedBy= (TextView) findViewById(R.id.txtviewAddedBy);

			
		spnrType= (Spinner) findViewById(R.id.inputType);
		spin_adapter = new ArrayAdapter<String>(EditIngredientActivity.this, android.R.layout.simple_spinner_item, foodType);
		spnrType.setAdapter(spin_adapter);
		
		//makes the ingredient name underlined
		SpannableString content = new SpannableString(ingredientName);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		txtIngredientName.setText(content);
		
		
		Log.d("EditIngr_just in", ingredientName);

		//if redirected from Add Ingredient button then the publish button is set to visible
		if(origin.equalsIgnoreCase(TAG_ADDINGREDIENT)){
			canEdit=true;
			btnPublish.setVisibility(1);

			Log.d("EditIngredient_Origine test", "the origine is addIngredient btn");
			
			//call this method now because it is a new ingredient
			addDetails();
	
		}else{
			txtIngredientName.setEnabled(false);
			// Getting complete ingredient details in background thread
			new GetIngredientDetails().execute();
		}

		
		
		
		Log.d("EditIngr_Back from GetIngre...", type);
		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update ingredient
				new SaveIngredientDetails().execute();
				Log.d("EditIngr_btn Save onclick", "updating");

			}
		});
		
		// publish button click event
		btnPublish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				//new SaveIngredientDetails().execute();
				Log.d("EditIngr_btn publish onclick", "creating new ingredient");
				new CreateNewIngredient().execute();
			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting product in background thread
				new DeleteIngredient().execute();
			}
		});

	}
	
	/***************************************************************************************************
	 *  							void addDetails()
	 *  
	 *  This method adds all of the ingredient details from the query. It also decides whether has the 
	 *  right to edit certain feature or where the EditIngredient Class was called from.
	 * 
	 ****************************************************************************************************/
	public void addDetails(){
		
		if(notes.equalsIgnoreCase("null")||notes.equalsIgnoreCase("")){
			txtNotes.setHint(defaultDiscription);
		}else{
			txtNotes.setText(notes);
		}
		
		txtCalories.setText(calories);
		txtProtein.setText(protein);
		txtFat.setText(fat);
		txtCarbs.setText(carbs);
		
		
		//makes the users name underlined
		//sets it to be the users name if the origin is from Add Ingredient button
		if(origin.equalsIgnoreCase(TAG_ADDINGREDIENT)){
			
			txtIngredientName.setText(null);
			txtIngredientName.setHint("New Ingredient");
						
			SpannableString content = new SpannableString(user);
			content.setSpan(new StyleSpan(Typeface.ITALIC), 0, content.length(), 0);
			txtAddedBy.setText(content);
			
		}else{
			
			SpannableString content = new SpannableString(addedBy);
			content.setSpan(new StyleSpan(Typeface.ITALIC), 0, content.length(), 0);
			txtAddedBy.setText(content);
		}

		//finds what type of food it is and sets it in the spinner
		for(int i=0; i<foodType.length;i++){
			if(type.equalsIgnoreCase(foodType[i])){
				spnrType.setSelection(i, true);
				Log.d("EditIngredient_addDetails spiner =", spnrType.getSelectedItem().toString());
				break;
			}
		}
		
		//checks to see if the user is also the author of the ingredient
		if(user.equalsIgnoreCase(addedBy)){
			
			Log.d("EditIngredient_AddDetails", "The user is the author");
			btnSave.setVisibility(1);
			btnDelete.setVisibility(1);
			canEdit=true;
		}
		
		if(!canEdit){
			txtCalories.setInputType(0);
			txtProtein.setInputType(0);
			txtFat.setInputType(0);
			txtCarbs.setInputType(0);
			txtNotes.setInputType(0);
			spnrType.setEnabled(false);
		}
		

		
	}

	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetIngredientDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditIngredientActivity.this);
			pDialog.setMessage("Loading Ingredient details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... args) {


			// updating UI from Background Thread
			/*runOnUiThread(new Runnable() {
				public void run() {*/
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("ingredientName", ingredientName));

						// getting Ingredient details by making HTTP request
						JSONObject json = jsonParser.makeHttpRequest( urlGetIngredientDetails, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray products = json.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first ingredient object from JSON Array
							JSONObject product = products.getJSONObject(0);

							//Getting details from the query
							Log.d("EditIngredient_DoinBackGround", "setting all of the details");
							calories = product.getString(TAG_CALORIES);
							protein = product.getString(TAG_PROTEIN);
							fat = product.getString(TAG_FAT);
							carbs = product.getString(TAG_CARBS);
							type = product.getString(TAG_TYPE);
							notes = product.getString(TAG_NOTES);
							addedBy = product.getString(TAG_ADDEDBY);

						}else{	
							// ingredient with that name not found
							//Toast.makeText(getApplicationContext(), "Nothing found", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
			/*	}
			});*/
					
					Log.d("EditIngredient_DoInBackground", "before return");
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("EditIngredient_PostExecute", "in post execute");
			//is called to add all of the details to the fields
			addDetails();
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	/**
	 * Background Async Task to  Save ingredient Details
	 * */
	class SaveIngredientDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditIngredientActivity.this);
			pDialog.setMessage("Saving ingredient Changes...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			String calories = txtCalories.getText().toString();
			String protein = txtProtein.getText().toString();
			String fat = txtFat.getText().toString();
			String carbs = txtCarbs.getText().toString();
			String type = spnrType.getSelectedItem().toString();
			String notes = txtNotes.getText().toString();
			String ingredientName = txtIngredientName.getText().toString();		
			
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ingredientName", ingredientName));
			params.add(new BasicNameValuePair("calories", calories));
			params.add(new BasicNameValuePair("protein", protein));
			params.add(new BasicNameValuePair("fat", fat));
			params.add(new BasicNameValuePair("carbs", carbs));
			params.add(new BasicNameValuePair("type", type));
			params.add(new BasicNameValuePair("notes", notes));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(urlUpdateIngredient, "GET", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
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
			Toast.makeText(getApplicationContext(), "Ingredient Updated", Toast.LENGTH_LONG).show();
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Ingredient
	 * */
	class DeleteIngredient extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditIngredientActivity.this);
			pDialog.setMessage("Deleting Product...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {
			

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ingredientName", ingredientName));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_product, "POST", params);

				// check your log for json response
				Log.d("Delete Ingredient", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
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
			// dismiss the dialog once product deleted
			Toast.makeText(getApplicationContext(), "Ingredient Deleted", Toast.LENGTH_LONG).show();
			pDialog.dismiss();

		}

	}
	
	
	/**
	 * Background Async Task to Create new ingredient
	 * */
	class CreateNewIngredient extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditIngredientActivity.this);
			pDialog.setMessage("Creating Ingredient..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating new ingredient
		 * */
		protected String doInBackground(String... args) {
			
			String calories = txtCalories.getText().toString();
			String protein = txtProtein.getText().toString();
			String fat = txtFat.getText().toString();
			String carbs = txtCarbs.getText().toString();
			String type = spnrType.getSelectedItem().toString();
			String notes = txtNotes.getText().toString();
			String addedBy = txtAddedBy.getText().toString();
			String ingredientName = txtIngredientName.getText().toString();		

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ingredientName", ingredientName));
			params.add(new BasicNameValuePair("calories", calories));
			params.add(new BasicNameValuePair("protein", protein));
			params.add(new BasicNameValuePair("fat", fat));
			params.add(new BasicNameValuePair("carbs", carbs));
			params.add(new BasicNameValuePair("type", type));
			params.add(new BasicNameValuePair("notes", notes));
			params.add(new BasicNameValuePair("addedBy", addedBy));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(urlCreateNewIngredient, "GET", params);
			
			// check log cat for response
			Log.d("EditIngredient_create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				message = json.getString(TAG_MESSAGE);

				if (success == 1) {
					
					// successfully created ingredient
					successful=true;
					//Toast.makeText(getApplicationContext(), "Ingredient Created", Toast.LENGTH_SHORT).show();
					
					
					Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
					startActivity(i);
					
					// closing this screen
					finish();
				} else {
					Log.d("EditIngredient failed:", message);
					//Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
				Toast.makeText(getApplicationContext(), "Ingredient Created", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}

	}
}

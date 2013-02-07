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
	Button btnDelete;


	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single ingredient url
	private static final String urlGetIngredientDetails = "http://10.0.2.2/recipeApp/getIngredientDetails.php";

	// url to update product
	private static final String url_update_product = "http://10.0.2.2/android_connect/update_product.php";
	
	// url to delete product
	private static final String url_delete_product = "http://10.0.2.2/android_connect/delete_product.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_INGREDIENTNAME = "ingredientName";
	private static final String TAG_CALORIES = "calories";
	private static final String TAG_PROTEIN = "protein";
	private static final String TAG_FAT = "fat";
	private static final String TAG_CARBS = "carbs";
	private static final String TAG_NOTES = "notes";
	private static final String TAG_ADDEDBY = "addedBy";
	private static final String TAG_TYPE = "type";
	private static final String TAG_DATECREATED = "dateCreated";
	private static final String TAG_DATEUPDATED = "dateUpdated";
	
	private static final String TAG_LISTINGREDIENT = "listIngredient";
	private static final String TAG_PANTRY = "pantry";
	private static final String TAG_ADDINGREDIENT = "addIngredient";
	private static final String TAG_ORIGINE = "origine";
	
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
	String origine= "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_ingredient);
		
		//getting the foodType Array from the resources
		foodType = getResources().getStringArray(R.array.foodType);
		
		// getting ingredient details from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		ingredientName = intent.getStringExtra(TAG_INGREDIENTNAME);
		origine = intent.getStringExtra(TAG_ORIGINE);
		
		if(origine.equalsIgnoreCase("addIngredient")){
			canEdit=true;
		}
		
		// Initializing all of the text fields and buttons
		btnSave = (Button) findViewById(R.id.btnSaveEditIngredient);
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

		// Getting complete ingredient details in background thread
		// if canEdit is true at this point then new ingredient is being added
		if(canEdit){
			addDetails();
		}else{
			new GetIngredientDetails().execute();
		}

		//txtCalories.setText(type);
		
		
		
		Log.d("EditIngr_Back from GetIngre...", type);
		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				//new SaveIngredientDetails().execute();
				Log.d("EditIngr_btn Save onclick", type);
				txtCalories.setText(type);
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
		
		if(notes.equalsIgnoreCase("null")){
			txtNotes.setHint("describe this ingredient");
		}else{
			txtNotes.setText(notes);
		}
		
		txtCalories.setText(calories);
		txtProtein.setText(protein);
		txtFat.setText(fat);
		txtCarbs.setText(carbs);
		
		
		//makes the users name underlined
		SpannableString content = new SpannableString(addedBy);
		content.setSpan(new StyleSpan(Typeface.ITALIC), 0, content.length(), 0);
		txtAddedBy.setText(content);

		//finds what type of food it is and sets it in the spinner
		for(int i=0; i<foodType.length;i++){
			if(type.equalsIgnoreCase(foodType[i])){
				spnrType.setSelection(i, true);
				Log.d("EditIngredient_addDetails spiner =", spnrType.getSelectedItem().toString());
				break;
			}
		}
		
		if(origine.equalsIgnoreCase(TAG_LISTINGREDIENT)||origine.equalsIgnoreCase(TAG_PANTRY))
		txtIngredientName.setInputType(0);
		txtCalories.setInputType(0);
		txtProtein.setInputType(0);
		txtFat.setInputType(0);
		txtCarbs.setInputType(0);
		spnrType.setEnabled(false);
		

		
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

						// getting product details by making HTTP request
						// Note that product details url will use GET request
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
	 * Background Async Task to  Save product Details
	 * */
	class SaveIngredientDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditIngredientActivity.this);
			pDialog.setMessage("Saving product ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String calories = txtCalories.getText().toString();
			String protein = txtProtein.getText().toString();
			String fat = txtFat.getText().toString();
			String carbs = txtCarbs.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_INGREDIENTNAME, ingredientName));
			params.add(new BasicNameValuePair(TAG_CALORIES, calories));
			params.add(new BasicNameValuePair(TAG_CARBS, carbs));
			params.add(new BasicNameValuePair(TAG_PROTEIN, protein));
			params.add(new BasicNameValuePair(TAG_FAT, fat));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product,
					"POST", params);

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
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Product
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
			pDialog.dismiss();

		}

	}
}

package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmail.samos6.CreateRecipeActivity.CreateNewRecipe;
import com.gmail.samos6.MainScreenActivity.LoginClass;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
	EditText txtGramAmount;
	TextView txtAddedBy;
	Spinner spnrType;
	
	
	Button btnSave;
	Button btnPublish;
	Button btnDelete;
	
	//preference access
	SharedPreferences prefs;
	String userName="";
	String password="";


	// Progress Dialog
	private ProgressDialog pDialog;
	private AlertDialog.Builder alert;
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	// single ingredient url
	String urlGetIngredientDetails;

	// url to update product
	String urlUpdateIngredient;
		
	// url to delete product
	String urlDeleteIngredient;

	// url to update product
	String urlCreateNewIngredient;
	
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
	private static final String TAG_GRAMAMOUNT = "gramAmount";
	private static final String TAG_TYPE = "type";
	//private static final String TAG_DATECREATED = "dateCreated";
	//private static final String TAG_DATEUPDATED = "dateUpdated";
	
	private static final String TAG_LISTINGREDIENT = "listIngredient";
	private static final String TAG_PANTRY = "pantry";
	private static final String TAG_ADDINGREDIENT = "addIngredient";
	private static final String TAG_REFRESHINGREDIENT = "refreshIngredient";
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
	String oldIngredientName="";
	String gramAmount="";
	String [] foodType;//will be set in onCreate
	Boolean canEdit=false;
	String origin= "";
	String defaultDiscription="";
	String message="";
	Boolean successful= false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_ingredient);
		
		//setting user name and password from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		userName =prefs.getString("nickName", "guest");
		password =prefs.getString("password", "");
		
		//getting URL's from resources
		urlGetIngredientDetails= getResources().getString(R.string.urlGetIngredientDetails);
		urlUpdateIngredient= getResources().getString(R.string.urlUpdateIngredient);
		urlDeleteIngredient= getResources().getString(R.string.urlDeleteIngredient);
		urlCreateNewIngredient= getResources().getString(R.string.urlCreateNewIngredient);
		
		//getting the foodType Array from the resources
		foodType = getResources().getStringArray(R.array.foodType);
		defaultDiscription= getResources().getString(R.string.defaultDiscription);
		
		// getting ingredient details from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		oldIngredientName = ingredientName = intent.getStringExtra(TAG_INGREDIENTNAME);
		origin = intent.getStringExtra(TAG_ORIGIN);
		
	
		
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
		txtGramAmount= (EditText) findViewById(R.id.inputGrams);
		
		txtAddedBy= (TextView) findViewById(R.id.txtviewAddedBy);

			
		spnrType= (Spinner) findViewById(R.id.inputType);
		spin_adapter = new ArrayAdapter<String>(EditIngredientActivity.this, android.R.layout.simple_spinner_item, foodType);
		spnrType.setAdapter(spin_adapter);
		
		//makes the ingredient name underlined
		SpannableString content = new SpannableString(ingredientName);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		txtIngredientName.setText(content);
		
		
		Log.d("EditIngr_just in", ingredientName+" old ingredientname: "+oldIngredientName);

		//if redirected from Add Ingredient button then the publish button is set to visible
		if(origin.equalsIgnoreCase(TAG_ADDINGREDIENT) || origin.equalsIgnoreCase(TAG_REFRESHINGREDIENT)){
			canEdit=true;
			btnPublish.setVisibility(1);

			Log.d("EditIngredient_Origine test", "the origine is addIngredient btn");
			
			//call this method now because it is a new ingredient
			addDetails();
	
		}else{
			//txtIngredientName.setEnabled(false);
			// Getting complete ingredient details in background thread
			new GetIngredientDetails().execute();
		}

		
		
		
		Log.d("EditIngr_Back from GetIngre...", type);
		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update ingredient
				Log.d("EditIngr_btn Save onclick", "updating");

				String ingredientName = txtIngredientName.getText().toString();	
				
				String msg = "";
				boolean incomplete=false;
				
				
				if(ingredientName.matches("")){
					msg = "You need an ingredient name.";
					incomplete=true;
				}else{
					new SaveIngredientDetails().execute();
				}
				
				if(incomplete)
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
		
		// publish button click event
		btnPublish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				Log.d("EditIngr_btn publish onclick", "creating new ingredient");
				
				String ingredientName = txtIngredientName.getText().toString();	
				
				String msg = "";
				boolean incomplete=false;
				
				
				if(ingredientName.matches("")){
					msg = "You need an ingredient name.";
					incomplete=true;
				}else{
					new CreateNewIngredient().execute();
				}
				
				if(incomplete)
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting ingredient in background thread
				Log.d("EditIngr_btn delete onclick", "deleting ingredient");
				new DeleteIngredient().execute();
			}
		});

	}
	
	// Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
    
	/**
	 * This method is called when an ingredient is stale (the name doesn't match in the MySQL DB)
	 * the user can choose to add it or it gets removed from his SQLite DB
	 */
	private void addIngredient(){
		
		alert = new AlertDialog.Builder(EditIngredientActivity.this);
		alert.setTitle("Add Ingredient");
		alert.setMessage("Sorry, this Ingredient was either renamed or deleted from the database.\n" +
							"Would you like to add it again?");

		alert
		.setCancelable(false)
		.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				
				Intent intent = new Intent(getApplicationContext(), EditIngredientActivity.class);
				// sending ingredientName to next activity
				intent.putExtra(TAG_INGREDIENTNAME, ingredientName);
				intent.putExtra(TAG_ORIGIN, TAG_REFRESHINGREDIENT);	
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, 100);
				
				dialog.cancel();
			}
		  })
		.setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				//removing ingredient from db since user does not want to add it
				if(db.hasIngredient(ingredientName)){
					db.deleteIngredient(ingredientName);
					Log.d("inside alert get ingredient", "inside if");					
				}
				dialog.cancel();
				Intent i = getIntent();
				// send result code 100 to notify about ingredient update
				setResult(100, i);
				finish();
			}
		});

		// create alert dialog
		AlertDialog alertChoice = alert.create();

		alertChoice.show();

		
		
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
						
			SpannableString content = new SpannableString(userName);
			content.setSpan(new StyleSpan(Typeface.ITALIC), 0, content.length(), 0);
			txtAddedBy.setText(content);
			
		}else if(origin.equalsIgnoreCase(TAG_REFRESHINGREDIENT)){		
			SpannableString content = new SpannableString(userName);
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
		if(userName.equalsIgnoreCase(addedBy)){
			
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
					//reset this for upcoming test
					successful=false;
					
					//if asyncTask has not been cancelled then continue
					if (!bCancelled) try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("ingredientName", ingredientName));

						// getting Ingredient details by making HTTP request
						JSONObject json = jsonParser.makeHttpRequest( urlGetIngredientDetails, "POST", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							
							successful=true;
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
							message = json.getString(TAG_SUCCESS);
							Log.d("ingredient not fount: ", message);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					
					Log.d("EditIngredient_DoInBackground", "before return");
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("EditIngredient_PostExecute", "in post execute");
			pDialog.dismiss();
			if(successful){
			//is called to add all of the details to the fields
			addDetails();
			}else{
				//if the ingredient name was changed then the users personal database is stale
				//User is asked if they want to add ingredient in addIngredient().
				addIngredient();
			}
			
			
		}
	}

	/**************************************************************************************************
	 * 							Background Async Task to Save Ingredient changes
	 **************************************************************************************************/
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
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			calories = txtCalories.getText().toString();
			protein = txtProtein.getText().toString();
			fat = txtFat.getText().toString();
			carbs = txtCarbs.getText().toString();
			type = spnrType.getSelectedItem().toString();
			notes = txtNotes.getText().toString();
			ingredientName = txtIngredientName.getText().toString();
			gramAmount = txtGramAmount.getText().toString();
			
			//reseting it
			successful=false;
			
			
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("oldIngredientName", oldIngredientName));
			params.add(new BasicNameValuePair("ingredientName", ingredientName));
			params.add(new BasicNameValuePair("calories", calories));
			params.add(new BasicNameValuePair("protein", protein));
			params.add(new BasicNameValuePair("fat", fat));
			params.add(new BasicNameValuePair("carbs", carbs));
			params.add(new BasicNameValuePair("type", type));
			params.add(new BasicNameValuePair("notes", notes));
			params.add(new BasicNameValuePair("gramAmount", gramAmount));

			// sending modified data through http request
			JSONObject json = jsonParser.makeHttpRequest(urlUpdateIngredient, "POST", params);

			//if asyncTask has not been cancelled then continue
			if (!bCancelled) try {
				int success = json.getInt(TAG_SUCCESS);
				message = json.getString(TAG_MESSAGE);
				
				if (success == 1) {
					
					successful = true;
					

					
					// successfully updated
					Intent i = getIntent();
					//send result code 100 to notify about product update
					setResult(100, i);
					finish();
					
				} else {
					// failed to update product
					Log.d("update Ingredient", "failed: "+message);
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
			pDialog.dismiss();
			if(successful){
				//if the user changes the name of an ingredient this this updates the DB
				if(db.hasIngredient(oldIngredientName) && !oldIngredientName.equalsIgnoreCase(ingredientName)){
					Log.d("inside post edit ingredient", "inside if");
					db.deleteIngredient(oldIngredientName);
					db.addIngredient(ingredientName);					
				}
				Toast.makeText(getApplicationContext(), "Ingredient Updated", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
			
		}
	}

	/**************************************************************************************************
	 * 							Background Async Task to Delete Ingredient
	 **************************************************************************************************/
	class DeleteIngredient extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditIngredientActivity.this);
			pDialog.setMessage("Deleting ingredient...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {
			

			// Check for success tag
			int success;
			
			//if asyncTask has not been cancelled then continue
			if (!bCancelled) try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ingredientName", ingredientName));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(urlDeleteIngredient, "POST", params);

				// check your log for json response
				Log.d("Delete Ingredient", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					
					
					db.deleteIngredient(ingredientName);
					
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
					
					/*Intent i = new Intent();
					
					if(origin.equalsIgnoreCase(TAG_LISTINGREDIENT)){  
						 i = new Intent(getApplicationContext(), ListIngredientActivity.class);
					}
					else{
						 i = new Intent(getApplicationContext(), PantryActivity.class);
					}
					
					
					startActivity(i);*/
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
	
	
	/**************************************************************************************************
	 * 							Background Async Task to Create New Ingredient
	 **************************************************************************************************/
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
			pDialog.setOnCancelListener(cancelListener);
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
			String gramAmount = txtGramAmount.getText().toString();	

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
			params.add(new BasicNameValuePair("gramAmount", gramAmount));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(urlCreateNewIngredient, "POST", params);
		
			//if asyncTask has not been cancelled then continue
			if (!bCancelled) try {	
				
				// check log cat for response
				Log.d("EditIngredient_create Response", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);
				message = json.getString(TAG_MESSAGE);

				if (success == 1) {
					
					// successfully created ingredient
					successful=true;
					//Toast.makeText(getApplicationContext(), "Ingredient Created", Toast.LENGTH_SHORT).show();
					
					Intent i = getIntent();
					// send result code 100 to notify about ingredient update
					setResult(100, i);
					finish();
					
					/*Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
					startActivity(i);
					
					// closing this screen
					finish();*/
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
}

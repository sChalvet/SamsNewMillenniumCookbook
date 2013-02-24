package com.gmail.samos6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GetIngredientActivity extends ListActivity {
	
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	SamsListAdapter adapter;
	Button btnDone;
	Button btnAdd;
    ListView lv;

	ArrayList<HashMap<String, String>> productsList;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	

	String urlGetAllIngredients;
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
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
	private static final String TAG_ADDINGREDIENT = "addIngredient";
	private static final String TAG_ORIGIN = "origin";
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;
	
	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_ingredients_for_create_recipe);
		
		//getting url from resources
		urlGetAllIngredients = getResources().getString(R.string.urlGetAllIngredients);
	
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllIngredients().execute();

		// Get listview
		lv = getListView(); 
		
		
		btnDone = (Button) findViewById(R.id.btnGetIngredientDone);
		btnAdd = (Button) findViewById(R.id.btnGetIngredientCreate);
		
				
		// save selected ingredients click event
		btnDone.setOnClickListener(new View.OnClickListener() {
							
				@Override
				public void onClick(View view) {
					Log.d("GetIngredient: ", "in Done onClick");
								
					List<String> list= new ArrayList<String>();
								
					list= adapter.getChecked();
								
					Log.d("inside btnDone: ", list.toString());					
								
					//Toast.makeText(getApplicationContext(), "Your Pantry has been Saved", Toast.LENGTH_SHORT).show();
								
					Intent intent= getIntent();
                    Bundle b = new Bundle();
                    b.putStringArrayList("myarraylist", (ArrayList<String>) list);
                    intent.putExtras(b);
                    setResult(100, intent);
                    finish();
								
								
				}
		});	
				
		// save selected ingredients click event
		btnAdd.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						Log.d("GetIngredient: ", "in Add onClick");
						
						// getting values from selected ListItem
						String ingredientname = "new ingredient";
								
						// Starting new intent
						Intent intent = new Intent(getApplicationContext(), EditIngredientActivity.class);
						// sending ingredientName to next activity
						intent.putExtra(TAG_INGREDIENTNAME, ingredientname);
						intent.putExtra(TAG_ORIGIN, TAG_ADDINGREDIENT);	
						startActivityForResult(intent, 100);
						startActivity(intent);
					}
				});				
		
		
		/*lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//view.setBackgroundColor(Color.CYAN);
				//view.findViewById(R.id.tableRowIngredient).setBackgroundColor(Color.CYAN);
						
				// getting values from selected ListItem
				String ingredientname = ((TextView) view.findViewById(R.id.ingredientName)).getText().toString();
						
				// Starting new intent
				Intent intent = new Intent(getApplicationContext(), EditIngredientActivity.class);
				// sending ingredientName to next activity
				intent.putExtra(TAG_INGREDIENTNAME, ingredientname);
				intent.putExtra(TAG_ORIGIN, TAG_LISTINGREDIENT);
				
				// starting new activity and expecting some response back
				startActivityForResult(intent, 100);
				startActivity(intent);
			}
		});*/

	}
	
	
	/**
	 * Finds which letter was clicked and goes to the first ingredient 
	 * that begins with that letter in the ListView. 
	 * This method is called from inside the XML.
	 * @param view
	 */
	public void letterClicked(View view) {
		
		Log.d("letterClicked: ", "in onClick");
		int id = view.getId();
		
		String letter = ((Button) view.findViewById(id)).getText().toString();
		int position = adapter.getPositionOf(letter);
		
		if (position==-1){
			Toast.makeText(getApplicationContext(), "No Ingredient starting with the letter "+letter, Toast.LENGTH_SHORT).show();
		}else{
			lv.setSelectionFromTop(position, 0);
			Toast.makeText(getApplicationContext(), "Moving to the letter "+letter, Toast.LENGTH_SHORT).show();
		}	
		
	}
	
	/**
	 * Enables user to cancel the AsychTask by hitting the back button
	 */
	OnCancelListener cancelListener=new OnCancelListener(){
	    @Override
	    public void onCancel(DialogInterface arg0){
	        bCancelled=true;
	        finish();
	    }
	};
	
	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted ingredient
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllIngredients extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GetIngredientActivity.this);
			pDialog.setMessage("Loading Ingredients. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			bCancelled=false;
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 	

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(urlGetAllIngredients, "GET", params);
			
			
			//if the asyncTask has not been cancelled then continue
			if(!bCancelled) try {
				Log.d("GetIngredient: ", json.toString());
				
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// Ingredient found
					// Getting Array of Ingredients
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through All Ingredients
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);
						
						String ingredientName = c.getString(TAG_INGREDIENTNAME);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_INGREDIENTNAME, ingredientName);

						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no ingredient found
					// Launch Add new Ingredient Activity
					Intent i = new Intent(getApplicationContext(), EditIngredientActivity.class);
					// Closing all previous activities
					//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
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
			// dismiss the dialog after getting all Ingredients
			pDialog.dismiss();
			
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					
					List<String> list = new ArrayList<String>();
					//list = db.getAllIngredients();
					adapter = new SamsListAdapter(GetIngredientActivity.this, productsList, list, "IngredientList");
					
					
					setListAdapter(adapter);
				}
			});

		}
		

	}

}
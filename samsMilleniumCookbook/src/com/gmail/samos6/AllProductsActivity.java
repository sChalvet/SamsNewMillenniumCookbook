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
import android.widget.TextView;
import android.widget.Toast;

public class AllProductsActivity extends ListActivity {
	
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	SamsListAdapter adapter;
	Button btnSave;
	Button btnAdd;
	Button btnEdit;

	ArrayList<HashMap<String, String>> productsList;
	final DatabaseHandler db = new DatabaseHandler(this);
	// url to get all products list
	private static String url_all_products = "http://10.0.2.2/recipeApp/get_all_products.php";

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

	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_products);
		
		//Instantiating the SQLite database
		//final DatabaseHandler db = new DatabaseHandler(this);
		
		
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllProducts().execute();

		// Get listview
		final ListView lv = getListView();  //added final
		
		
		btnSave = (Button) findViewById(R.id.btnSaveIngredients);
		btnAdd = (Button) findViewById(R.id.btnAddIngredient);
		btnEdit = (Button) findViewById(R.id.btnEditIngredients);
				
				// view products click event
		btnSave.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						Log.d("allIngredient: ", "in Save onClick");
						
						List<String> list= new ArrayList<String>();
						
						list= adapter.getChecked();
						
						Log.d("inside btnSave: ", list.toString());
						
						db.deleteAllIngredient();
						db.addListIngredient(list);
			
						
						Toast.makeText(getApplicationContext(), "Your Pantry has been Saved", Toast.LENGTH_SHORT).show();
						
						// product successfully deleted
						// notify previous activity by sending code 100
						Intent i = getIntent();
						// send result code 100 to notify about product deletion
						setResult(100, i);
						finish();
						
						
					}
				});
		// on seleting single product
		// launching Edit Product Screen
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				view.setBackgroundColor(Color.CYAN);
				view.findViewById(R.id.tableRowIngredient).setBackgroundColor(Color.CYAN);
				
				
				// getting values from selected ListItem
				
				Boolean ingredientName = ((CheckBox) view.findViewById(R.id.ingredientCheckBox)).isChecked();
				
				
				//String ingredientname = ((TextView) view.findViewById(R.id.ingredientCheckBox)).getText().toString();
						
				//Color color=new Color(200,0,0);
				if(ingredientName)Toast.makeText(getBaseContext(), "True", Toast.LENGTH_SHORT).show();
				// Starting new intent
				/*Intent in = new Intent(getApplicationContext(),
						EditProductActivity.class);
				// sending pid to next activity
				//in.putExtra(TAG_PID, pid);
				in.putExtra(TAG_INGREDIENTNAME, ingredientname);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);*/
			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AllProductsActivity.this);
			pDialog.setMessage("Loading Ingredients. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 	

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						//String id = c.getString(TAG_PID);
						//String name = c.getString(TAG_NAME);
						
						String ingredientName = c.getString(TAG_INGREDIENTNAME);
					 /* String calories = c.getString(TAG_CALORIES);
						String protein = c.getString(TAG_PROTEIN);
						String fat = c.getString(TAG_FAT);
						String carbs = c.getString(TAG_CARBS);
						String notes = c.getString(TAG_NOTES);  */
						//String addedBy = c.getString(TAG_ADDEDBY);  
						//String type = c.getString(TAG_TYPE);
					  /*  String dateCreated = c.getString(TAG_DATECREATED);
						String dateUpdated = c.getString(TAG_DATEUPDATED);*/
						

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						//map.put(TAG_PID, id);
						//map.put(TAG_NAME, name);
						map.put(TAG_INGREDIENTNAME, ingredientName);
					/*	map.put(TAG_CALORIES, calories);
						map.put(TAG_PROTEIN, protein);
						map.put(TAG_FAT, fat);
						map.put(TAG_CARBS, carbs);
						map.put(TAG_NOTES, notes);*/
						//map.put(TAG_ADDEDBY, addedBy);
						//map.put(TAG_TYPE, type);
					/*	map.put(TAG_DATECREATED, dateCreated);
						map.put(TAG_DATEUPDATED, dateUpdated);*/

						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no products found
					// Launch Add New product Activity
					Intent i = new Intent(getApplicationContext(),
							NewProductActivity.class);
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					
					List<String> list = new ArrayList<String>();
					list = db.getAllIngredients();
					adapter = new SamsListAdapter(AllProductsActivity.this, productsList, list, "IngredientList");
					
					/*ListAdapter adapter = new SimpleAdapter(AllProductsActivity.this, productsList,
							R.layout.list_item, new String[] { TAG_INGREDIENTNAME, "cbxingred" },
							new int[] { R.id.ingredientName, R.id.ingredientCheckBox});*/
					
					// updating listview
					//Log.d("allproducts: ", "setListAdapter(adapter)");
					
					setListAdapter(adapter);
				}
			});

		}
		

	}

}
package com.gmail.samos6.samscookbook;

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
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListIngredientActivity extends ListActivity {
	
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	//preference access
	SharedPreferences prefs;
	String userName="";

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	SamsListAdapter adapter;
	Button btnSave;
	Button btnAdd;
    ListView lv;

	//used to set font
	Typeface typeFace; 
	
	ArrayList<HashMap<String, String>> productsList;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	String urlGetAllIngredients;
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_INGREDIENTNAME = "ingredientName";
	
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
		setContentView(R.layout.display_ingredients);
		
		//setting user name and password from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		userName =prefs.getString("nickName", "guest");
		
		//getting url from resources
		urlGetAllIngredients = getResources().getString(R.string.urlGetAllIngredients);
	
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllProducts().execute();

		// Get listview
		lv = getListView(); 
		
		
		btnSave = (Button) findViewById(R.id.btnSaveIngredients);
		btnAdd = (Button) findViewById(R.id.btnAddIngredient);
		
		//setting the font type from assets		
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		btnSave.setTypeface(typeFace);
		btnAdd.setTypeface(typeFace);
		
		Toast.makeText(getApplicationContext(), getString(R.string.pAddIng), Toast.LENGTH_SHORT).show();
		
		// save selected ingredients click event
				btnSave.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View view) {
								//log.d("allIngredient: ", "in Save onClick");
								
								List<String> list= new ArrayList<String>();
								
								list= adapter.getChecked();
								
								//log.d("inside btnSave: ", list.toString());
								
								db.deleteAllIngredient();
								db.addListIngredient(list);
					
								
								Toast.makeText(getApplicationContext(), getString(R.string.pantrySaved), Toast.LENGTH_SHORT).show();
								
								// ingredients successfully saved
								// notify previous activity by sending code 100
								Intent i = getIntent();
								// send result code 100 to notify about ingredient save
								setResult(100, i);
								finish();
								
								
							}
						});	
				
				// save selected ingredients click event
				btnAdd.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View view) {
								//log.d("ListIngredient: ", "in Add onClick");
								
								if(!userName.equalsIgnoreCase("guest")){
									// getting values from selected ListItem
									String ingredientname = "new ingredient";
											
									// Starting new intent
									Intent intent = new Intent(getApplicationContext(), EditIngredientActivity.class);
									// sending ingredientName to next activity
									intent.putExtra(TAG_INGREDIENTNAME, ingredientname);
									intent.putExtra(TAG_ORIGIN, TAG_ADDINGREDIENT);	
									startActivityForResult(intent, 100);
								}else{
									Toast.makeText(getApplicationContext(), getString(R.string.pLoginToCreatIngr), Toast.LENGTH_LONG).show();
								}
							}
						});	
		
				
		// on selecting single ingredient
		// launching EditIngredient Screen		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
						
				// getting values from selected ListItem
				String ingredientname = ((TextView) view.findViewById(R.id.ingredientName)).getText().toString();
						
				// Starting new intent
				Intent intent = new Intent(getApplicationContext(), EditIngredientActivity.class);
				// sending ingredientName to next activity
				intent.putExtra(TAG_INGREDIENTNAME, ingredientname);
				intent.putExtra(TAG_ORIGIN, TAG_LISTINGREDIENT);
				
				// starting new activity and expecting some response back
				startActivityForResult(intent, 100);
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
	 * used for browsing the list of ingredients with more ease
	 * @param view
	 */
	public void letterClicked(View view) {
		
		//log.d("letterClicked: ", "in onClick");
		int id = view.getId();
		
		String letter = ((Button) view.findViewById(id)).getText().toString();
		int position = adapter.getPositionOf(letter);
		
		if (position==-1){
			Toast.makeText(getApplicationContext(), getString(R.string.noIngreWithLetter)+" "+letter, Toast.LENGTH_SHORT).show();
		}else{
			lv.setSelectionFromTop(position, 0);
			Toast.makeText(getApplicationContext(), getString(R.string.movingToLetter)+" "+letter, Toast.LENGTH_SHORT).show();
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
			// reload this adapter again
			adapter.clear();
			new LoadAllProducts().execute();
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
			pDialog = new ProgressDialog(ListIngredientActivity.this);
			pDialog.setMessage(getString(R.string.loadingIngredients));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			bCancelled=false;
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 	

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(urlGetAllIngredients, "POST", params);
			
			
			//if the asyncTask has not been cancelled then continue
			if(!bCancelled) try {
				//log.d("All Ingredients: ", json.toString());
				
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing  json item in variable					
						String ingredientName = c.getString(TAG_INGREDIENTNAME);

						

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_INGREDIENTNAME, ingredientName);

						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no Ingredient found
					// Launch Add New product Activity
					/*Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					
					List<String> list = new ArrayList<String>();
					list = db.getAllIngredients();
					adapter = new SamsListAdapter(ListIngredientActivity.this, productsList, list, "IngredientList", typeFace);
					
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
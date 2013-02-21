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

public class ListRecipeActivity  extends ListActivity{
	
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	SamsListAdapter adapter;
	Button btnSave;
	Button btnAdd;

	ArrayList<HashMap<String, String>> productsList;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	private static String urlGetAllRecipes = "http://10.0.2.2/recipeApp/getAllRecipes.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_RATING = "rating";
	private static final String TAG_NUMRATINGS = "numRatings";
	private static final String TAG_PREPTIME = "prepTime";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_TOTALTIME = "totalTime";

	private static final String TAG_ORIGIN = "origin";
	

	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_recipes);
	
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllRecipes().execute();

		// Get listview
		final ListView lv = getListView();  //added final
		
		
				
		// on seleting single recipe
		// launching recipe Screen
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
				// getting values from selected ListItem
				String recipeName = ((TextView) view.findViewById(R.id.txtListRecipeRecipeName)).getText().toString();
				Log.d("ListRecipe: ", recipeName);
						
				// Starting new intent
				Intent intent = new Intent(getApplicationContext(), RecipeViewActivity.class);
				
				// sending recipeName to next activity
				intent.putExtra(TAG_RECIPENAME, recipeName);
				
				// starting new activity and expecting some response back
				startActivityForResult(intent, 100);
				//startActivity(intent);
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
			// means user edited/deleted ingredient
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all Recipe by making HTTP Request
	 * */
	class LoadAllRecipes extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListRecipeActivity.this);
			pDialog.setMessage("Loading Recipes. Please wait...");
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
			JSONObject json = jParser.makeHttpRequest(urlGetAllRecipes, "GET", params);
			
			Log.d("All Recipes: ", json.toString());

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
						String recipeName = c.getString(TAG_RECIPENAME);
						String summery = c.getString(TAG_SUMMERY);
						String rating = c.getString(TAG_RATING);
						String numRatings = c.getString(TAG_NUMRATINGS);
						String prepTime = c.getString(TAG_PREPTIME);
						String cookTime = c.getString(TAG_COOKTIME);
						String author = c.getString(TAG_AUTHOR);	
						
						int cookT = Integer.parseInt(cookTime);
						int prepT = Integer.parseInt(prepTime);

						String totalTime = Integer.toString(cookT+prepT);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_RECIPENAME, recipeName);
						map.put(TAG_SUMMERY, summery);
						map.put(TAG_RATING, rating);
						map.put(TAG_NUMRATINGS, numRatings);
						map.put(TAG_AUTHOR, author);
						map.put(TAG_TOTALTIME, totalTime);
						


						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no recipes found
					//Toast.makeText(getApplicationContext(), "Your no recipes found", Toast.LENGTH_SHORT).show();
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
					
					
					ListAdapter adapter = new SimpleAdapter(ListRecipeActivity.this, productsList,
							R.layout.list_recipes, new String[] { TAG_RECIPENAME, TAG_SUMMERY, TAG_NUMRATINGS, 
																	TAG_AUTHOR, TAG_TOTALTIME},
							new int[] { R.id.txtListRecipeRecipeName, R.id.txtListRecipeSummery, 
										R.id.txtListRecipeNumReviews, R.id.txtListRecipeAuthor, R.id.txtListRecipeTotalCookTime});
					
					// updating listview
					//Log.d("allproducts: ", "setListAdapter(adapter)");
					
					setListAdapter(adapter);
				}
			});

		}
		

	}

}

package com.gmail.samos6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmail.samos6.ListIngredientActivity.LoadAllProducts;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	RecipeLazyAdapter adapter;
	Button btnSave;
	Button btnAdd;
	
	String searchAuthor;
	String searchFoodName;
	String searchRecipeType;
	String searchKeyWord;
	String searchCookTime;
	int position;

	ArrayList<HashMap<String, String>> productsList;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	String urlGetAllRecipes;
	String urlRoot;

	ListView lv;
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_RATING = "rating";
	private static final String TAG_NUMRATINGS = "numRatings";
	private static final String TAG_PREPTIME = "prepTime";
	private static final String TAG_TOTALTIME = "totalTime";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_IMAGEURL = "imageUrl";
	
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_FOODNAME = "foodName";
	private static final String TAG_RECIPETYPE = "recipeType";
	private static final String TAG_KEYWORD = "keyWord";
	private static final String TAG_ORIGIN = "origin";
	

	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_recipes);
	
		//getting url from resources
		urlGetAllRecipes = getResources().getString(R.string.urlGetAllRecipes);
		urlRoot = getResources().getString(R.string.urlRoot);
		
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();
		
		// getting ingredient details from intent
		Intent intent = getIntent();
		// getting data past from intent
		searchAuthor = intent.getStringExtra(TAG_AUTHOR);
		searchFoodName = intent.getStringExtra(TAG_FOODNAME);
		searchRecipeType = intent.getStringExtra(TAG_RECIPETYPE);
		searchKeyWord = intent.getStringExtra(TAG_KEYWORD);
		searchCookTime = intent.getStringExtra(TAG_COOKTIME);
		
		
		// Loading products in Background Thread
		new LoadAllRecipes().execute();

		// Get listview
		lv = getListView();
				
		// on selecting single recipe
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
			}
		});

	}
	
	
	@Override
	protected void onRestart() {
	    super.onRestart();
	    
	    Log.d("list recipe inside", "on restart");
	    position= lv.getFirstVisiblePosition();
		//adapter.clear();
		//new LoadAllRecipes().execute();
		//lv.setSelectionFromTop(position, 0);
		Log.d("list recipe inside", "visible pos="+Integer.toString(position));
	}

	// Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
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
			pDialog.setMessage(getString(R.string.loadingRecipes));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 	
			
			params.add(new BasicNameValuePair(TAG_AUTHOR, searchAuthor));
			params.add(new BasicNameValuePair(TAG_FOODNAME, searchFoodName));
			params.add(new BasicNameValuePair(TAG_RECIPETYPE, searchRecipeType));
			params.add(new BasicNameValuePair(TAG_KEYWORD, searchKeyWord));
			params.add(new BasicNameValuePair(TAG_COOKTIME, searchCookTime));

			Log.d("SearchRecipes params: ", params.toString());
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(urlGetAllRecipes, "POST", params);
			
			//if AsyncTask has Not been cancelled then continue
			if (!bCancelled) try {
				
				Log.d("All Recipes: ", json.toString());
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
						String imageUrl = urlRoot+c.getString(TAG_IMAGEURL); //adding the urlRoot to the url returned by php
						
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
						map.put(TAG_IMAGEURL, imageUrl);

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
					
					
					adapter = new RecipeLazyAdapter(ListRecipeActivity.this, productsList);
					
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

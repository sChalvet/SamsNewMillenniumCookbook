package com.gmail.samos6.samscookbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SearchForRecipeByIngredientActivity  extends ListActivity{
	
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	RecipeLazyAdapter adapter;

	ArrayList<HashMap<String, String>> productsList;
	List<String> ingredientList = new ArrayList<String>();
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;
	
	String index="0";
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	String urlGetAllRecipesByIngredient;
	String urlRoot;
	
	//used to set font
	Typeface typeFace; 
	
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
	private static final String TAG_IMAGEURL = "imageUrl";
	

	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_recipes);
	
		//getting url from resources
		urlGetAllRecipesByIngredient = getResources().getString(R.string.urlGetAllRecipesByIngredient);
		urlRoot = getResources().getString(R.string.urlRoot);
		
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		//setting the font type from assets		
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		
		// Get listview
		final ListView lv = getListView();  //added final
		
		
		if( getIntent().getExtras() != null){
			Bundle extras= getIntent().getExtras();
			ingredientList = extras.getStringArrayList("IngredientList");
			//log.d("SearchForRecipeByIngredient list: ", ingredientList.toString());
			index = getIntent().getStringExtra("index");
			
			// Loading products in Background Thread
			new LoadAllRecipes().execute();
		}




		//on selecting single recipe launching recipe Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
				// getting values from selected ListItem
				String recipeName = ((TextView) view.findViewById(R.id.txtListRecipeRecipeName)).getText().toString();
				//log.d("ListFavoriteRecipe: ", recipeName);
						
				// Starting new intent
				Intent intent = new Intent(getApplicationContext(), RecipeViewActivity.class);
				
				// sending recipeName to next activity
				intent.putExtra(TAG_RECIPENAME, recipeName);
				
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
			pDialog = new ProgressDialog(SearchForRecipeByIngredientActivity.this);
			pDialog.setMessage(getString(R.string.loadingRecipes));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			for(int i=0; i<ingredientList.size(); i++){
				params.add(new BasicNameValuePair("list"+Integer.toString(i), ingredientList.get(i).toString()));
			}
			
			params.add(new BasicNameValuePair("searchType", index));
			
			//log.d("All Favorite Recipes params: ", params.toString());
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(urlGetAllRecipesByIngredient, "POST", params);
			
			
			//if the asyncTask has not been cancelled then continue
			if(!bCancelled) try {
				//log.d("All Favorite Recipes json: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// Recipes found
					// Getting Array of Recipes
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through All Recipes
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
						String imageUrl = urlRoot+c.getString(TAG_IMAGEURL); //adding urlRoot to the image url
						
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
					// no Recipes found
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
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all Recipes
			pDialog.dismiss();
			
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					
					
					adapter = new RecipeLazyAdapter(SearchForRecipeByIngredientActivity.this, productsList, typeFace);
					
					// updating listview
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

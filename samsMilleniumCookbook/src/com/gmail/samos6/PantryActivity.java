package com.gmail.samos6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PantryActivity extends ListActivity {
	
	
	// Progress Dialog
	private ProgressDialog pDialog;

	SamsListAdapter adapter;
	
	Button btnEdit;
	Button btnSearchRecipes;
	Button btnDeleteIngredient;

	ArrayList<HashMap<String, String>> productsList;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);

	private static final String TAG_INGREDIENTNAME = "ingredientName";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pantry_view);
		
		
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllProducts().execute();

		// Get listview
		final ListView lv = getListView();  //added final
		
		
		btnSearchRecipes = (Button) findViewById(R.id.btnSearchRecipes);
		btnEdit = (Button) findViewById(R.id.btnEditPantry);
		btnDeleteIngredient = (Button) findViewById(R.id.btnDeleteIngredients);
				
				// view products click event
		btnEdit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d("Pantry_btnEdit: ", "inside OnClick");
				
				// Starting new intent
				Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
				
			
				// starting new activity and expecting some response back
				startActivityForResult(i, 100);

			}
		});
		
		btnDeleteIngredient.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d("Pantry_btnDeleteIngredient: ", "inside OnClick");
				
				List<String> list = new ArrayList<String>();
				
				list= adapter.getChecked();
				Log.d("Pantry_btnDeleteIngredient list= ", list.toString());
				
				db.deleteListIngredient(list);
				Intent intent = getIntent();
				finish();
				startActivity(intent);

			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//view.setBackgroundColor(Color.CYAN);
				//view.findViewById(R.id.tableRowIngredient).setBackgroundColor(Color.CYAN);
						
				// getting values from selected ListItem
				String ingredientname = ((TextView) view.findViewById(R.id.ingredientName)).getText().toString();
				
				Log.d("Pantry_ItemClick: ", ingredientname);
				// Starting new intent
				Intent intent = new Intent(getApplicationContext(), EditIngredientActivity.class);
				// sending ingredientName to next activity
				intent.putExtra(TAG_INGREDIENTNAME, ingredientname);
				
				// starting new activity and expecting some response back
				startActivityForResult(intent, 100);
			}
		});

	}

	// Response from IngredientList Activity
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
			pDialog = new ProgressDialog(PantryActivity.this);
			pDialog.setMessage("Loading Ingredients. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from SQLite
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<String> ingredientList = new ArrayList<String>(); 	

			ingredientList = db.getAllIngredients();
			
			//if(ingredientList.size()==0)
			//	Toast.makeText(getApplicationContext(), "Your Pantry is empty", Toast.LENGTH_SHORT).show();
			
			// Check your log cat for DB response
			Log.d("Pantry: ", ingredientList.toString());

			// looping through All Ingredients
			for (int i = 0; i < ingredientList.size(); i++) {				
				String ingredientName = ingredientList.get(i);
				
				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key => value
				map.put(TAG_INGREDIENTNAME, ingredientName);
						
				// adding HashList to ArrayList
				productsList.add(map);
			}
			
			Log.d("Pantry_productList: ", productsList.toString());

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
					Log.d("Pantry_callingAdapter with: ", list.toString());
					adapter = new SamsListAdapter(PantryActivity.this, productsList, list, "Pantry");
					
					setListAdapter(adapter);
				}
			});

		}
		

	}

}
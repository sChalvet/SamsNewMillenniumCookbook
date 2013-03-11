package com.gmail.samos6;

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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
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
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class ListRecipeCommentsActivity  extends ListActivity{
	
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ListCommentsAdapter adapter;

	ArrayList<HashMap<String, String>> productsList;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	//preference access
	SharedPreferences prefs;
	String userName="";
	String password="";
	
	String urlGetAllComments;
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCTS = "products";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_POSTTIME = "postTime";
	private static final String TAG_COMMENT = "comment";
	private static final String TAG_RATING = "rating";
	
	String recipeName="";
	Button btnAddComment;
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;
	

	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_comments);
	
		//setting user name and password from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		userName =prefs.getString("nickName", "guest");
		password =prefs.getString("password", "");
		
		urlGetAllComments = getResources().getString(R.string.urlGetAllComments);
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();
		
		// getting recipeName from intent
		Intent intent = getIntent();
		// getting data past from intent
		recipeName = intent.getStringExtra(TAG_RECIPENAME);
		// Loading comments in Background Thread
		new LoadAllComments().execute();

		// Get listview
		final ListView lv = getListView();  //added final
		
		btnAddComment = (Button) findViewById(R.id.btnListRecipeCommentsAdd);
		
		btnAddComment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.d("ListComments: ", "in btnAddComment onClick");
				
				if(!adapter.hasAlredyPosted(userName)){
					Intent intent = new Intent(getApplicationContext(), CreateNewCommentActivity.class);
					
					// sending recipeName to next activity
					intent.putExtra(TAG_RECIPENAME, recipeName);
					
					// starting new activity and expecting some response back
					startActivityForResult(intent, 100);
				}
				else{
					Toast.makeText(getApplicationContext(), "Editing your previous comment.", Toast.LENGTH_LONG).show();
					// Starting new intent
					Intent intent = new Intent(getApplicationContext(), EditCommentActivity.class);				
					// sending recipeName to next activity
					intent.putExtra(TAG_RECIPENAME, recipeName);			
					// starting new activity and expecting some response back
					startActivityForResult(intent, 100);
				}
				
				
			}
		});		
		// on selecting single comment
		// launching edit comment Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
				if(userName.equalsIgnoreCase(adapter.getAuthor(position))){
					// Starting new intent
					Intent intent = new Intent(getApplicationContext(), EditCommentActivity.class);				
					// sending recipeName to next activity
					intent.putExtra(TAG_RECIPENAME, recipeName);			
					// starting new activity and expecting some response back
					startActivityForResult(intent, 100);
				}else{
					Toast.makeText(getApplicationContext(), "You can only edit your own comment.", Toast.LENGTH_LONG).show();
					//Toast.setView(Gravity.CENTER);
				}
				
					

			}
		});

	}

	// Response from Edit Comment Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user added/deleted a comment
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
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllComments extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListRecipeCommentsActivity.this);
			pDialog.setMessage("Loading Comments. Please wait...");
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
			
			//including recipeName for the query
			params.add(new BasicNameValuePair(TAG_RECIPENAME, recipeName));
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(urlGetAllComments, "POST", params);
			
			
			//if AsyncTask was not cancelled then carry on
			if(!bCancelled) try {
				
				Log.d("All Comments: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// comment found
					// Getting Array of Products
					products = json.getJSONArray(TAG_PRODUCTS);

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable						
						String authorName = c.getString(TAG_AUTHOR);
						String rating = c.getString(TAG_RATING);
						String postTime = c.getString(TAG_POSTTIME);
						String comment = c.getString(TAG_COMMENT);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_AUTHOR, authorName);
						map.put(TAG_RATING, rating);
						map.put(TAG_POSTTIME, postTime);
						map.put(TAG_COMMENT, comment);
						
						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no Comments found
					// Launch create new comment
					Intent intent = new Intent(getApplicationContext(),CreateNewCommentActivity.class);
					// Closing all previous activities
					intent.putExtra(TAG_RECIPENAME, recipeName);
					//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 100);
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

					adapter = new ListCommentsAdapter(ListRecipeCommentsActivity.this, productsList);		
							
					
					setListAdapter(adapter);
				}
			});

		}
		

	}

}

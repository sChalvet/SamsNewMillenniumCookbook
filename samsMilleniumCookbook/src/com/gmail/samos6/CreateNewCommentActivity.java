package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateNewCommentActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	
	TextView txtRecipeName;
	EditText txtComment;
	Spinner spnrRating;
	Button btnPost;
	
	String recipeName;
	String userName = "Van das gutter";
	
	//these 2 variables are used to test the results and errors from the server
	Boolean successful =false;
	String message="";

	// url to create new product
	//private static String urlCreateNewRating = "http://10.0.2.2/recipeApp/CreateNewRating.php";
	String urlCreateNewRating ;
	
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_MESSAGE = "message";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_comment);

		//getting url from resources
		urlCreateNewRating = getResources().getString(R.string.urlCreateNewRating);
		// Edit Text
		txtRecipeName = (TextView) findViewById(R.id.txtCreateCommentRecipeName);
		txtComment = (EditText) findViewById(R.id.txtCreateCommentComment);
		spnrRating = (Spinner) findViewById(R.id.CreateRecipeRatingSpinner);
		
		// getting recipeName from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		recipeName = intent.getStringExtra(TAG_RECIPENAME);
		
		txtRecipeName.setText(recipeName);
		// Create button
		btnPost = (Button) findViewById(R.id.btnCreateCommentPost);

		// button click event
		btnPost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// creating new Comment in background thread
				new CreateNewComment().execute();
			}
		});
	}

	/**
	 * Background Async Task to Create new Comment
	 * */
	class CreateNewComment extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateNewCommentActivity.this);
			pDialog.setMessage("Posting Comment...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			
			String comment = txtComment.getText().toString();
			String rating = spnrRating.getSelectedItem().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("recipeName", recipeName));
			params.add(new BasicNameValuePair("comment", comment));
			params.add(new BasicNameValuePair("rating", rating));
			params.add(new BasicNameValuePair("authorName", userName));

			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(urlCreateNewRating, "GET", params);
			
			// check log cat for response
			Log.d("Create Comment", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successful=true;
					// successfully created Comment
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify that the mission was accomplished
					setResult(100, i);
					
					// closing this screen
					finish();
				} else {
					message = json.getString(TAG_MESSAGE);
					// failed to create Comment
					Log.d("Failed to create new comment", "failed");
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
				Toast.makeText(getApplicationContext(), "Comment Posted", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}

	}
}

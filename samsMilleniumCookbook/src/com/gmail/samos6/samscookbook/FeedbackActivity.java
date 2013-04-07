package com.gmail.samos6.samscookbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmail.samos6.samscookbook.EditIngredientActivity.SaveIngredientDetails;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	
	TextView txtFeedback;
	Button btnSend;

	//used to set font
	Typeface typeFace; 
	
	//preference access
	SharedPreferences prefs;
	String userName="";
	String token="";
	
	//these 2 variables are used to test the results and errors from the server
	Boolean successful =false;
	String message="";
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	// url to create new product
	String urlSendFeedback ;
	
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_COMMENT = "comment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_form);

		//setting user name and password from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		userName =prefs.getString("nickName", "guest");
		token =prefs.getString("token", "");
		
		//getting url from resources
		urlSendFeedback = getResources().getString(R.string.urlSendFeedback);
		// Edit Text
		txtFeedback = (TextView) findViewById(R.id.txtFeedback);
		
		// Create button
		btnSend = (Button) findViewById(R.id.btnSendFeedback);

		//setting the font type from assets		
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		txtFeedback.setTypeface(typeFace);
		btnSend.setTypeface(typeFace);
		
		((TextView)findViewById(R.id.textView1)).setTypeface(typeFace);
		((TextView)findViewById(R.id.textView2)).setTypeface(typeFace);
		
		// button click event
		btnSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
				String comment = txtFeedback.getText().toString();	
				
				String msg = "";
				boolean incomplete=false;
				
				
				if(comment.matches("")){
					msg = getString(R.string.pEnterFeedback);
					incomplete=true;
				}else{
					new SendNewFeedback().execute();
				}
				
				if(incomplete)
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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
	 * Background Async Task to Create new Comment
	 * */
	class SendNewFeedback extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FeedbackActivity.this);
			pDialog.setMessage(getString(R.string.sendingFeedback));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * Creating comment
		 * */
		@Override
		protected String doInBackground(String... args) {
			
			String comment = txtFeedback.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_COMMENT, comment));
			params.add(new BasicNameValuePair(TAG_AUTHOR, userName));

			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(urlSendFeedback, "POST", params);
			
			//if asyncTask has not been cancelled then continue
			if(!bCancelled) try {
				
				// check log cat for response
				//log.d("Create Comment", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successful=true;
					// successfully posted feedback
					// notify previous activity by sending code 100
					//Intent i = getIntent();
					// send result code 100 to notify that the mission was accomplished
					//setResult(100, i);
					
					// closing this screen
					finish();
				} else {
					message = json.getString(TAG_MESSAGE);
					// failed to send feedback
					//log.d("Failed to create new comment", "failed");
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
			// dismiss the dialog once done
			pDialog.dismiss();
			
			if(successful)
				Toast.makeText(getApplicationContext(), getString(R.string.feedbackSent), Toast.LENGTH_LONG).show();
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

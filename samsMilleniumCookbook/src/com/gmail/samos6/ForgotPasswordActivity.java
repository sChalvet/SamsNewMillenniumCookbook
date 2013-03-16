package com.gmail.samos6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmail.samos6.AccountCreationActivity.CreateNewAccount;
import com.gmail.samos6.MainScreenActivity.LoginClass;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPasswordActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	
	TextView txtTestQuestion;
	EditText txtTestAnswer;
	Button btnResetPassword;

	String userName="";
	String testQuestion="";
	String testAnswer="";
	
	//these 2 variables are used to test the results and errors from the server
	Boolean successful =false;
	String message="";
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	// url strings
	String urlResetPassword;
	String urlGetTestQuestion;
	
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_PRODUCT = "product";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password);
		
		//getting url from resources
		urlResetPassword = getResources().getString(R.string.urlResetPassword);
		urlGetTestQuestion = getResources().getString(R.string.urlGetTestQuestion);
		
		// Edit Text
		txtTestQuestion = (TextView) findViewById(R.id.resetPassTestQuestion);
		txtTestAnswer = (EditText) findViewById(R.id.resetPassTestAnswer);
		
		// getting userName from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		userName = intent.getStringExtra("userName");
		
		//loading the test question
		new LoadTestQuestion().execute();
		
		// reset button
		btnResetPassword = (Button) findViewById(R.id.btnResetPassword);

		// button click event
		btnResetPassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

Log.d(" inside create account=", "inside onclick");
				

				testAnswer = txtTestAnswer.getText().toString();
				String msg = "";
				boolean incomplete=false;
				
				if(testAnswer.matches("")){
					msg = getString(R.string.pEnterTestQAns);
					incomplete=true;
				}else{
					new ResetPassword().execute();
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
    
	private void addDetails(){
		
		txtTestQuestion.setText(testQuestion);
		
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
	 * Background Async Task to reset password
	 * */
	class ResetPassword extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setMessage(getString(R.string.resetingPass));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * updating comment
		 * */
		protected String doInBackground(String... args) {
			
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
		
			params.add(new BasicNameValuePair("userName", userName));
			params.add(new BasicNameValuePair("testAnswer", testAnswer));

			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(urlResetPassword, "POST", params);
			
			//reseting variable
			successful=false;
			
			//if asyncTask has not been cancelled then continue
			if(!bCancelled) try {
				
				// check log cat for response
				Log.d("ForgotPass testAnswer:", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successful=true;

				} else {
					message = json.getString(TAG_MESSAGE);
					// failed to update Comment
					Log.d("Failed reset password", message);
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
			
			if(successful){
				dialogBox(R.string.resetPasswordMessage, true);
			}else{
				dialogBox(R.string.resetPasswordAnswerDoesNotMatch, false);
			}
		}

	}
	
	/**
	 * Background Async Task to Load test question by making HTTP Request
	 * */
	class LoadTestQuestion extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setMessage(getString(R.string.loadingTestQ));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * getting comment info from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>(); 	
			
			//including recipeName for the query
			params.add(new BasicNameValuePair("userName", userName));
			
			// getting JSON string from URL
			JSONObject json = jsonParser.makeHttpRequest(urlGetTestQuestion, "POST", params);
			
			//reseting variable
			successful=false;
			
			//if AsyncTask was not cancelled then carry on
			if(!bCancelled) try {
				
				Log.d("forgotPassword: ", json.toString());
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successful=true;
					// products found
					// Getting Array of Products
					JSONArray products = json.getJSONArray(TAG_PRODUCT);				
					
					// get first ingredient object from JSON Array
					JSONObject product = products.getJSONObject(0);

					// Storing each json item in variable						
					testQuestion = product.getString("testQuestion");

				} else {
					// no Comments found
					message = json.getString(TAG_MESSAGE);
					Log.d("ForgotPassword responce:", message);
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
			// dismiss the dialog after getting comment		
			pDialog.dismiss();
			if(successful)
				addDetails();
			else{
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				finish();
			}


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
	 

	 
	 /**
		 * Spawns a login dialog box
		 */
	 public void dialogBox(int msgId, final Boolean done){
		 AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
      builder.setCancelable(true);
      builder.setTitle(R.string.Attention);
      builder.setMessage(msgId);
      builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                      if(done)
                    	  finish();
                      
                  }
              });
      AlertDialog alert = builder.create();
      alert.show();
		}
		
		
		
}

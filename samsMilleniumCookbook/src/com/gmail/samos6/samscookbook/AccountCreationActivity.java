package com.gmail.samos6.samscookbook;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.TextView;
import android.widget.Toast;


public class AccountCreationActivity extends Activity{
	
	Button btnCreateAccount;
	EditText txtNickName;
	EditText txtEmail;
	EditText txtFirstName;
	EditText txtLastName;
	EditText txtPassword;
	EditText txtTestQuestion;
	EditText txtTestAnswer;

	// Progress Dialog
	private ProgressDialog pDialog;
	
	boolean successful = false;
	String message = "";
	
	//used to set font
	Typeface typeFace;
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;
	
	String nickName;
	String email;
	String firstName;
	String lastName;
	String password;
	String testQuestion;
	String testAnswer;
	String token;
	
	//Creating the variable that will hold the url when it is pulled from resources
	String urlCreateAccount;
	String urlLogin;
	
	//preference access
	SharedPreferences prefs;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_NICKNAME = "nickName";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_FIRSTNAME = "firstName";
	private static final String TAG_LASTNAME = "lastName";
	private static final String TAG_PASSWORD = "password";
	private static final String TAG_TESTQUESTION = "testQuestion";
	private static final String TAG_TESTANSWER = "testAnswer";
	private static final String TAG_TOKEN = "token";
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		//getting url from resources
		urlCreateAccount = getResources().getString(R.string.urlCreateAccount);
		urlLogin = getResources().getString(R.string.urlLogin);
		
		//setting the btn and txt
		btnCreateAccount = (Button) findViewById(R.id.btnPreferenceSave);
		txtNickName = (EditText) findViewById(R.id.userNickName);
		txtEmail = (EditText) findViewById(R.id.userEmail);
		txtFirstName = (EditText) findViewById(R.id.userFirstName);
		txtLastName = (EditText) findViewById(R.id.userLastName);
		txtPassword = (EditText) findViewById(R.id.userPassword);
		txtTestQuestion = (EditText) findViewById(R.id.userTestQuestion);
		txtTestAnswer = (EditText) findViewById(R.id.userTestQuestionAnswer);
	
		//setting the font type from assets		
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		btnCreateAccount.setTypeface(typeFace);
		txtNickName.setTypeface(typeFace);
		txtEmail.setTypeface(typeFace);
		txtFirstName.setTypeface(typeFace);
		txtLastName.setTypeface(typeFace);
		txtPassword.setTypeface(typeFace);
		txtTestQuestion.setTypeface(typeFace);
		txtTestAnswer.setTypeface(typeFace);
		
		((TextView) findViewById(R.id.pref1)).setTypeface(typeFace);
		((TextView) findViewById(R.id.pref2)).setTypeface(typeFace);
		((TextView) findViewById(R.id.pref3)).setTypeface(typeFace);
		((TextView) findViewById(R.id.pref4)).setTypeface(typeFace);
		((TextView) findViewById(R.id.pref6)).setTypeface(typeFace);
		((TextView) findViewById(R.id.pref7)).setTypeface(typeFace);
		((TextView) findViewById(R.id.pref8)).setTypeface(typeFace);
		
		// Create Account click event
		btnCreateAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {

				
				//log.d(" inside create account=", "inside onclick");
				
				nickName =  txtNickName.getText().toString();
				email = txtEmail.getText().toString();
				firstName = txtFirstName.getText().toString();
				lastName = txtLastName.getText().toString();
				password = txtPassword.getText().toString();
				testQuestion = txtTestQuestion.getText().toString();
				testAnswer = txtTestAnswer.getText().toString();
				String msg = "";
				boolean incomplete=false;
				
				if(nickName.matches("")){
					msg = getString(R.string.needNickName);
					incomplete=true;
				}else if(!isValidEmailAddress(email)){
					msg = getString(R.string.pEnterValidEmail);
					incomplete=true;
				}else if(firstName.matches("")){
					msg = getString(R.string.pEnterYourFname);
					incomplete=true;
				}else if(lastName.matches("")){
					msg = getString(R.string.pEnterYourLname);
					incomplete=true;
				}else if(password.matches("")){
					msg = getString(R.string.pEnterAPass);
					incomplete=true;
				}else if(testQuestion.matches("")){
					msg = getString(R.string.pEnterTestQ);
					incomplete=true;
				}else if(testAnswer.matches("")){
					msg = getString(R.string.pEnterTestQAns);
					incomplete=true;
				}else{
					new CreateNewAccount().execute();
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
	 * Setsthe user info into preferences (keeps him logged in)
	 */
	private void setPreferences(){

		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putString("nickName", nickName);
		editor.putString("email", email);
		editor.putString("firstName", firstName);
		editor.putString("lastName", lastName);
		editor.putString("token", token);
		editor.commit();
		
		// closing this screen
		finish();
		
	};
	/**
	 * Background Async Task to Create new Account
	 * */
	class CreateNewAccount extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AccountCreationActivity.this);
			pDialog.setMessage(getString(R.string.creatingAcc));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
			
		}

		/**
		 * Creating account
		 * */
		@Override
		protected String doInBackground(String... args) {
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			//log.d("Create Account=", "do in background");

			// Building Parameters
			params.add(new BasicNameValuePair(TAG_NICKNAME, nickName));
			params.add(new BasicNameValuePair(TAG_EMAIL, email));
			params.add(new BasicNameValuePair(TAG_FIRSTNAME, firstName));
			params.add(new BasicNameValuePair(TAG_LASTNAME, lastName));
			params.add(new BasicNameValuePair(TAG_PASSWORD, password));
			params.add(new BasicNameValuePair(TAG_TESTQUESTION, testQuestion));
			params.add(new BasicNameValuePair(TAG_TESTANSWER, testAnswer));

			//log.d("CreateAccount params: ", params.toString());
			
			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(urlCreateAccount, "POST", params);

			//if asyncTask has Not been cancelled then continue
			if (!bCancelled) try {
				
				// check log cat for response
				//log.d("Create account Response", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successful=true;
					// successfully created Account
					//log.d("CreateAccount_Background", "Success! account Created");
					
					token = json.getString(TAG_TOKEN);
					
				} else {
					// failed to create Account
					 message = json.getString(TAG_MESSAGE);
					//log.d("CreateAccount_Background", "oops! Failed to create Account "+message);
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
			if(successful){
				setPreferences();
				Toast.makeText(getApplicationContext(), getString(R.string.accountCreated), Toast.LENGTH_LONG).show();
			}
			else
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}

	}
	
	/**
	 * Checks to see if the email address provided is valid
	 * 
	 * @param email
	 * @return true if valid email
	 */
	public static boolean isValidEmailAddress(String email) {
		
		Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/-_.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/-_\"]]+.[a-zA-Z0-9[!#$%&'()*+,/-_\".]]+");

		Matcher m = emailPattern.matcher(email); 
		
		return m.matches();

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
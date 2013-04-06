package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmail.samos6.EditRecipeActivity.CreateNewRecipe;
import com.gmail.samos6.ListRecipeActivity.LoadAllRecipes;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;


public class MainScreenActivity extends Activity{
	
	Button btnSaved;
	Button btnMe;
	Button btnLogin;
	Button btnFavorites;
	Button btnAddRecipe;
	Button btnPantry;
	Button btnToRecipeSearch;
	Button btnLogout;
	
	EditText txtNickName;
	EditText txtPassword;
	EditText txtSearchWords;
	TextView txtLogInName;
	Button btnAlertLogin;
	Button btnAlertCreateAccount;
	Button btnForgotPassword;
	
	String nickName;
	String firstName;
	String lastName;
	String email;
	String password;
	String token;
	String urlLogin;
	String searchWords;
	
	String userName="";
	
	boolean successful = false;
	String message = "";
	
	Typeface typeFace;
	
	// Progress Dialog
	private AlertDialog alert;
	private ProgressDialog pDialog;
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	//preference access
	SharedPreferences prefs;
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_NICKNAME = "nickName";
	private static final String TAG_PASSWORD = "password";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_FIRSTNAME = "firstName";
	private static final String TAG_LASTNAME = "lastName";
	private static final String TAG_TOKEN = "token";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState); 
	setContentView(R.layout.main_screen);	
	
	prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	
	if(prefs.getString("nickName", null)==null){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("nickName", "Guest");
		editor.putString("token", "");
		editor.commit();
	}
	
	//getting user name
	userName =prefs.getString("nickName", "guest");
	token =prefs.getString("token", "");
	
	//getting url from resources
	urlLogin = getResources().getString(R.string.urlLogin);
		
		// Buttons
		btnSaved = (Button) findViewById(R.id.btnMainSavedRecipe);
		btnFavorites = (Button) findViewById(R.id.btnMainFavRecipe);
		btnPantry = (Button) findViewById(R.id.btnMainPantry);
		btnToRecipeSearch = (Button) findViewById(R.id.btnMainToRecipeSearch);
		btnAddRecipe = (Button) findViewById(R.id.btnMainAddRecipe);
		btnLogin = (Button) findViewById(R.id.btnMainLogin);
		btnLogout = (Button) findViewById(R.id.btnMainLogout);
		btnMe = (Button) findViewById(R.id.btnMainMe);
		txtSearchWords= (EditText) findViewById(R.id.autoCompleteTextView1);	
		txtLogInName= (TextView) findViewById(R.id.textView1);	
	
		//setting the font type from assets
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		btnSaved.setTypeface(typeFace);
		btnFavorites.setTypeface(typeFace);
		btnPantry.setTypeface(typeFace);
		btnToRecipeSearch.setTypeface(typeFace);
		btnAddRecipe.setTypeface(typeFace);
		btnLogin.setTypeface(typeFace);
		btnLogout.setTypeface(typeFace);
		btnMe.setTypeface(typeFace);
		txtSearchWords.setTypeface(typeFace);
		txtLogInName.setTypeface(typeFace);
		
		//set login name or guest if none exist
		txtLogInName.setText(userName+" "+getString(R.string.isNowLogedIn));
		
		btnAddRecipe.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				if(!userName.equalsIgnoreCase("Guest")){
				// Launching CreateRecipeActivity
				Intent i = new Intent(getApplicationContext(), CreateRecipeActivity.class);
				startActivity(i);
				}else{
					Toast.makeText(getApplicationContext(), getString(R.string.loginToCreate), Toast.LENGTH_LONG).show();
					loginAlertDialog();
				}
				
			}
		});
		
		btnMe.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String name =prefs.getString("nickName", null);
				String t =prefs.getString("token", null);
				Toast.makeText(getApplicationContext(), "You are "+name+" token is: "+t, Toast.LENGTH_LONG).show();
				
				/*PackageManager pm = getPackageManager();
				Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
				// Verify clock implementation
				String clockImpls[][] = {
				        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
				        {"Standar Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
				        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
				        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
				        {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"}
				};

				boolean foundClockImpl = false;

				for(int i=0; i<clockImpls.length; i++) {
				    String vendor = clockImpls[i][0];
				    String packageName = clockImpls[i][1];
				    String className = clockImpls[i][2];
				    try {
				        ComponentName cn = new ComponentName(packageName, className);
				        ActivityInfo aInfo = pm.getActivityInfo(cn, PackageManager.GET_META_DATA);
				        alarmClockIntent.setComponent(cn);
				        Log.d("uyu","Found " + vendor + " --> " + packageName + "/" + className);
				        foundClockImpl = true;
				    } catch (NameNotFoundException e) {
				    	Log.d("hghjg", vendor + " does not exists");
				    }
				}

				if (foundClockImpl) {
				    PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, alarmClockIntent, 0);
				    startActivity(alarmClockIntent);   
				}*/
				
				
				
			}
		});
		
		btnLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// calling logout();
				logOut();
				Toast.makeText(getApplicationContext(), getString(R.string.youLoggedOut), Toast.LENGTH_LONG).show();
				
			}
		});
		
		
		btnSaved.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching SavedRecipesActivity
				Intent i = new Intent(getApplicationContext(), SavedRecipesActivity.class);
				startActivity(i);
				
			}
		});
		
		btnFavorites.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching FavoriteRecipesActivity
				Intent i = new Intent(getApplicationContext(), FavoriteRecipesActivity.class);
				startActivity(i);
				
			}
		});

		btnToRecipeSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching SearchForRecipeActivity
				Intent i = new Intent(getApplicationContext(), SearchForRecipeActivity.class);
				startActivity(i);
				
			}
		});

		btnPantry.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				// Launching PantryActivity
				Intent i = new Intent(getApplicationContext(), PantryActivity.class);
				startActivity(i);
				
			}
		});
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				// Launching Login alert box
				loginAlertDialog();		
			}
		});
		
	}
	
	@Override
	protected void onRestart() {
	    super.onRestart();
	    
	    Log.d("Main Screen", "on restart");

		//getting user name
		userName =prefs.getString("nickName", "guest");	  
		txtLogInName.setText(userName+" "+getString(R.string.isNowLogedIn));
	}
	
	// Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }
	
	/**
	 * Spawns a login dialog box
	 */
	private void loginAlertDialog(){
	  
	      LayoutInflater factory = LayoutInflater.from(MainScreenActivity.this);            
	        final View textEntryView = factory.inflate(R.layout.login_form, null);
	        
	        
			txtNickName= (EditText) textEntryView.findViewById(R.id.MainNickName);
			txtPassword= (EditText) textEntryView.findViewById(R.id.MainPassword);	
			btnAlertLogin= (Button) textEntryView.findViewById(R.id.btnMainAlertLogin);
			btnAlertCreateAccount= (Button) textEntryView.findViewById(R.id.btnMainAlertCreateAccount);
			btnForgotPassword= (Button) textEntryView.findViewById(R.id.btnMainAlertForgotPass);
	        
			txtNickName.setTypeface(typeFace);
			txtPassword.setTypeface(typeFace);
			btnAlertLogin.setTypeface(typeFace);
			btnAlertCreateAccount.setTypeface(typeFace);
			btnForgotPassword.setTypeface(typeFace);
			
			alert = new AlertDialog.Builder(MainScreenActivity.this).create();
			alert.setTitle(R.string.login);
			alert.setIcon(R.drawable.icon_37_by_37);

			
			alert.setView(textEntryView);

			btnAlertLogin.setOnClickListener(new OnClickListener() {
			    @Override
              public void onClick(View v) {
			    	
			    	nickName=  txtNickName.getText().toString();
			    	password=  txtPassword.getText().toString();	
				
					String msg = "";
					boolean incomplete=false;
					
					Log.d("MainScreen alert", "in onclick");
					
					if(nickName.matches("")){
						msg = getString(R.string.pEnterUser);
						incomplete=true;
					}else if(password.matches("")){
						msg = getString(R.string.pEnterPass);
						incomplete=true;
					}else{
						new LoginClass().execute();
						
					}
					
					if(incomplete)
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				    }
          }); 
			
			btnAlertCreateAccount.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// Launching AccountCreationActivity
					alert.cancel();
					Intent i = new Intent(getApplicationContext(), AccountCreationActivity.class);
					startActivity(i);
					
				}
			});
			
			
			btnForgotPassword.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// Launching AccountCreationActivity
					
					String nickName=  txtNickName.getText().toString();
					
					String msg = "";
					boolean incomplete=false;
					
					Log.d("MainScreen alert", "in onclick");
					
					if(nickName.matches("")){
						msg = getString(R.string.pEnterNameForPass);
						incomplete=true;
					}else{
						
						alert.cancel();
						Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
						intent.putExtra("userName", nickName);	
						startActivityForResult(intent, 100);
						
					}
					
					if(incomplete){
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				    }
					
				}
			});
			
			alert.show();
		
	};
	
	/**
	 * logIn method that sets the users preferences.
	 */
	private void logIn(){
	  
		userName=nickName;
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putString("nickName", nickName);
		editor.putString("email", email);
		editor.putString("firstName", firstName);
		editor.putString("lastName", lastName);
		editor.putString("token", token);
		editor.commit();
		
		txtLogInName.setText(userName+" "+getString(R.string.isNowLogedIn));
		
		//close alert dialogue once logged in
		alert.cancel();
		
	};
	
	/**
	 * Enables user to cancel the AsychTask by hitting the back button
	 */
	private void logOut(){
	  
		userName="Guest";
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putString("nickName", "guest");
		editor.putString("email", "");
		editor.putString("firstName", "");
		editor.putString("lastName", "");
		editor.putString("token", "");
		editor.commit();
		
	};
	
	
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
	 * Background Async Task to Login
	 * */
	class LoginClass extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainScreenActivity.this);
			pDialog.setMessage(getString(R.string.logingIn));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
			
		}

		/**
		 * Loging in
		 * */
		protected String doInBackground(String... args) {
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//reset test variable
			successful=false;
			// Building Parameters
			params.add(new BasicNameValuePair(TAG_NICKNAME, nickName));
			params.add(new BasicNameValuePair(TAG_PASSWORD, password));

			Log.d("Login params: ", params.toString());
			
			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(urlLogin, "POST", params);

			//if asyncTask has Not been cancelled then continue
			if (!bCancelled) try {
				
				// check log cat for response
				Log.d("login Response", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					successful=true;

					// successfully received product details
					JSONArray products = json.getJSONArray(TAG_PRODUCT); // JSON Array
					
					// get first ingredient object from JSON Array
					JSONObject product = products.getJSONObject(0);

					//Getting details from the query
					Log.d("Login_DoinBackGround", "getting user information");
					nickName= product.getString(TAG_NICKNAME);
					email = product.getString(TAG_EMAIL);
					firstName = product.getString(TAG_FIRSTNAME);
					lastName = product.getString(TAG_LASTNAME);
					token = product.getString(TAG_TOKEN);

					
					Log.d("Login_DoinBackGround", "Success! User Loged In");
					// closing this screen
					//finish();
				} else {
					// failed to log in
					 message = json.getString(TAG_MESSAGE);
					Log.d("Login_DoinBackGround", "oops! Failed to log In "+message);
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
				logIn();
				Toast.makeText(getApplicationContext(), nickName+getString(R.string.isNowLogedIn), Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		}

	}
	
	/**
	 * Gets the search words and launches a search.
	 */
	public void searchRecipe(View view){
		
		searchWords=txtSearchWords.getText().toString();
		
		Intent intent = new Intent(getApplicationContext(), ListRecipeActivity.class);
		intent.putExtra("author", "");
		intent.putExtra("foodName", "Any");	
		intent.putExtra("recipeType", "Any");
		intent.putExtra("cookTime", "Any");	
		intent.putExtra("keyWord", searchWords);	
		startActivity(intent);
		
	}
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item){
	 
	        switch (item.getItemId()){
	 
	            
	        case R.id.editAccount:
	        	
	        	if(userName.equalsIgnoreCase("guest")){
	        		
					Toast.makeText(getApplicationContext(), getString(R.string.loginToEditAcc), Toast.LENGTH_LONG).show();
					loginAlertDialog();
	        		
	        	}else{
	        		Intent i = new Intent(getApplicationContext(), EditUserInfoActivity.class);
	        		// Closing all previous activities
	        		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        		startActivity(i);
	        		return true;
	        	}
	        	
				
	 
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    } 

}


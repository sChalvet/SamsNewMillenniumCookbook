package com.gmail.samos6;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressWarnings("deprecation")
public class CreateRecipeActivity extends Activity {
	
	Inflater inflater;

	EditText txtRecipeName;
	EditText txtCookingDirections;
	EditText txtCookTime;
	EditText txtPrepTime;
	EditText txtSummery;
	Spinner spnrRecipeType;
	Spinner spnrServings;
	ImageView imgPicture;
	SlidingDrawer slidingDrawer;
	
	LinearLayout listLayout;
	
	//used to set font
	Typeface typeFace;
	
	Button btnPublish;
	Button btnAddIngredient;
	Button btnTakePhoto;
	
	Bitmap recipeImage=null;
	
	//preference access
	SharedPreferences prefs;
	String userName="";
	String token="";
	
	//used to fill the spinners
	String[] recipeType;
	String[] recipeServings;
	
	boolean successfulRecipe = false;
	boolean successfulPicture = true;
	String message = "";
	
	//will contain the image id from db
	int hasImage=0;
	
	ArrayAdapter<String> spin_adapter;
	
	//these lists store the values of the generated ingredient list
	List<String> ingredientList= new ArrayList<String>();
	List<TextView> listIngredientName= new ArrayList<TextView>();
	List<EditText> listDescription= new ArrayList<EditText>();
	List<Spinner> listSpnrMeasurement= new ArrayList<Spinner>();
	List<Spinner> listSpnrAmount= new ArrayList<Spinner>();
	List<CheckBox> listVital= new ArrayList<CheckBox>();
	List<TableLayout> listTable= new ArrayList<TableLayout>();
	
	//this stores the number of ingredients selected
	int numIngredients=0;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	//Creating the variable that will hold the url when it is pulled from resources
	String urlCreateRecipe;
	String urlUploadImage; 
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_INGREDIENTLIST = "ingredientList";
	private static final String TAG_COOKINGDIRECTIONS = "cookingDirections";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_PREPTIME = "prepTime";
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SERVINGS = "servings";
	private static final String TAG_IMAGE = "image";
	private static final String TAG_HASIMAGE = "hasImage";
	private static final String TAG_TOKEN = "token";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_recipe);
		
		//setting user name and password from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		userName =prefs.getString("nickName", "guest");
		token =prefs.getString("token", "");
		
		//getting url from resources
		urlCreateRecipe = getResources().getString(R.string.urlCreateRecipe);
		urlUploadImage = getResources().getString(R.string.urlUploadImage);
		
		recipeType = getResources().getStringArray(R.array.recipeType);
		recipeServings = getResources().getStringArray(R.array.spinnerServings);
		
		
		//setting the recipe type spinner
		spnrRecipeType = (Spinner) findViewById(R.id.spnrCreateRecipeType);
		spin_adapter = new ArrayAdapter<String>(CreateRecipeActivity.this, android.R.layout.simple_spinner_item, recipeType);
		spnrRecipeType.setAdapter(spin_adapter);
		
		//setting the servings spinner
		spnrServings = (Spinner) findViewById(R.id.spnrCreateRecipeServings);
		spin_adapter = new ArrayAdapter<String>(CreateRecipeActivity.this, android.R.layout.simple_spinner_item, recipeServings);
		spnrServings.setAdapter(spin_adapter);
			
		// Initializing all of the text fields and buttons
		txtRecipeName = (EditText) findViewById(R.id.inputCreateRecipeName);
		txtCookingDirections= (EditText) findViewById(R.id.inputRecipeCreateDirections);
		txtCookTime= (EditText) findViewById(R.id.inputRecipeCreateCooktime);
		txtPrepTime= (EditText) findViewById(R.id.inputRecipeCreatePreptime);
		txtSummery= (EditText) findViewById(R.id.inputRecipeCreateSummery);
		imgPicture= (ImageView) findViewById(R.id.recipeCreateImage);	
		slidingDrawer= (SlidingDrawer) findViewById(R.id.recipeCreateSlidingDrawer);
		
		//used to put the ingredient intering data into
		listLayout =  (LinearLayout) findViewById(R.id.inputRecipeCreateLinearLayout);
		
		btnPublish= (Button) findViewById(R.id.btnCreateRecipeSubmit);
		btnTakePhoto= (Button) findViewById(R.id.btnCreateRecipeTakePhoto);
		btnAddIngredient= (Button) findViewById(R.id.btnCreateRecipeAddIngredient);
		
		//setting the font type from assets		
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		txtRecipeName.setTypeface(typeFace);
		txtCookingDirections.setTypeface(typeFace);
		txtCookTime.setTypeface(typeFace);
		txtPrepTime.setTypeface(typeFace);
		txtSummery.setTypeface(typeFace);
		btnPublish.setTypeface(typeFace);		
		btnTakePhoto.setTypeface(typeFace);
		btnAddIngredient.setTypeface(typeFace);
		
        ((TextView)findViewById(R.id.handle)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.tv1)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.tv2)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.tv3)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.tv4)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.tv5)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.tv6)).setTypeface(typeFace);
        ((TextView)findViewById(R.id.tv7)).setTypeface(typeFace);

        
			
		Log.d("CreateRecipe_just in", "Inside");
	
		//send user to pick ingredients first
		Toast.makeText(getApplicationContext(), getString(R.string.chooseIngredients), Toast.LENGTH_LONG).show();
		Intent intent = new Intent(getApplicationContext(), GetIngredientActivity.class);
		startActivityForResult(intent,100);
		
		// publish to DB button click event
		btnPublish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Log.d("CreateRecipe_btnPublish onclick", "inside");
				
				String recipeName= txtRecipeName.getText().toString();
				String cookingDirections= txtCookingDirections.getText().toString();
				String cookTime= txtCookTime.getText().toString();
				String prepTime= txtPrepTime.getText().toString();
				String summery= txtSummery.getText().toString();
				String msg = "";
				boolean incomplete=false;
				
				Log.d("CreateRecipe_btnPublish recipename=", recipeName);
				
				if(recipeName.matches("")){
					msg = getString(R.string.pEnterRecipeName);
					incomplete=true;
				}else if(cookingDirections.matches("")){
					msg = getString(R.string.pEnterCookingDir);
					incomplete=true;
				}else if(cookTime.matches("") || prepTime.matches("")){
					msg = getString(R.string.pEnterCookTime);
					incomplete=true;
				}else if(summery.matches("")){
					msg = getString(R.string.pEnterSumery);
					incomplete=true;
				}else if(numIngredients==0){
					msg = getString(R.string.pEnterIngredient);
					incomplete=true;
				}else{
					new CreateNewRecipe().execute();
				}
				
				if(incomplete)
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

			}
		});
		
		// take pic button click event
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				Log.d("CreateRecipe_btnTakePhoto onclick", "inside");
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);	

			}
		});
		
		// Add Ingredient button click event
				btnAddIngredient.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						Log.d("CreateRecipe_btnAddIngredient onclick", "inside");
						
						Intent intent = new Intent(getApplicationContext(), GetIngredientActivity.class);
						Bundle b = new Bundle();
	                    b.putStringArrayList("IngredientList", (ArrayList<String>) ingredientList);
	                    intent.putExtras(b);
						startActivityForResult(intent,100);
						

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
	
	
	// Response from Get Ingredient Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("inside ActivityResults", "...");	
		
		//result from taking a pic of recipe
		if(requestCode == 0){
			Log.d("inside ActivityResults", "pic result");	
			recipeImage = (Bitmap) data.getExtras().get("data");
			imgPicture.setImageBitmap(recipeImage);	
		}
		
		// if result code 100, coming from GetIngredientActivity
		if (resultCode == 100) {
			
			Log.d("inside ActivityResults", "result from GetIngredientActivity");	
			
			List<String> receivedList= new ArrayList<String>();
			List<String> addList= new ArrayList<String>();
			List<String> cutList= new ArrayList<String>();
			Bundle extras= data.getExtras();
			
			receivedList = extras.getStringArrayList("IngredientList");
			Log.d("inside ActivityResults got this list: ", receivedList.toString());
			
			if(ingredientList.isEmpty()){
				ingredientList=receivedList;
				createIngredientListView(ingredientList);
			}else{
				//here we figure out what has been added and what has been dropped
				//when user changed his ingredient selection after using AddIngredient button
				addList= cloneList(receivedList);
				addList.removeAll(ingredientList);
				cutList= cloneList(ingredientList);
				cutList.removeAll(receivedList);
				
				//dropping any fields that were dropped
				dropFromList(cutList);
				
				//ingredientList is kept up to date one what ingredients the recipe contains
				ingredientList.addAll(addList);
				ingredientList.removeAll(cutList);
				
				Log.d("inside ActivityResults list final: ", ingredientList.toString());
				Log.d("inside ActivityResults list cut: ", cutList.toString());
				Log.d("inside ActivityResults list add: ", addList.toString());
				
				//adding the needed fields for the added ingredients
				createIngredientListView(addList);
			}
			
			
			numIngredients=ingredientList.size();
			
		}

	}
	
private void createIngredientListView(List<String> list){
	
	//http://prasans.info/2011/03/add-edittexts-dynamically-and-retrieve-values-android/
	listLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	
	
	for(int index=0; index<list.size(); index++){
		
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_ingredient_gradient));
		
		TextView txtIngredientName = new TextView(this);
		txtIngredientName.setTextSize(21f);
		txtIngredientName.setText(list.get(index));
		txtIngredientName.setPadding(0, 15, 0, 0);
		txtIngredientName.setTypeface(typeFace);
		txtIngredientName.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		listIngredientName.add(txtIngredientName);
		
		//packing everything into a tableLayout
		tableLayout.addView(txtIngredientName);
		tableLayout.addView(ingredientRow1(index));
		tableLayout.addView(ingredientRow2(index));
		
		//this tableLayout now contains all of the layout for one ingredient
		//storing it into listTable in case we need to drop an ingredient
		listTable.add(tableLayout);
		
		//adding one ingredient view to the list
		listLayout.addView(tableLayout);
	}
	
}

/**
 * creates a table layout with the vital checkbox and text
 * @param index
 * @return TableLayout
 */
private TableLayout ingredientRow1(int index) {
	TableLayout tableLayout = new TableLayout(this);
	tableLayout.setStretchAllColumns(true);
	
	TableRow tableRow = new TableRow(this);
	tableRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	
	TextView txtVital = new TextView(this);
	txtVital.setTextSize(13f);
	txtVital.setText(getString(R.string.ingredientVital));
	txtVital.setTypeface(typeFace);
	txtVital.setPadding(30, 0, 0, 22);
	txtVital.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 20f));
	txtVital.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
	
	CheckBox ckBox = new CheckBox(this);
	ckBox.setPadding(0, 0, 0, 0);
	ckBox.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0f));
	ckBox.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
	ckBox.setSelected(true);
	listVital.add(ckBox);
	
	
    //adding all of the created widgets into the tableRow
	//then adding that tableRow into the tableLayout
	tableRow.addView(txtVital);
	tableRow.addView(ckBox);
    tableLayout.addView(tableRow);
    
    return tableLayout;
}

/**
 * Creates a TableLayout with with 2 spinners (amount and unit)
 * and one text field for the ingredient description (diced, chopped, etc...)
 * 
 * @param index
 * @return TableLayout
 */
private TableLayout ingredientRow2(int index) {
	
	//amount.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.1f));
	
	String[] measurement = getResources().getStringArray(R.array.measurement);
	String[] Unitamount = getResources().getStringArray(R.array.amount);

	
	TableLayout tableLayout = new TableLayout(this);
	tableLayout.setStretchAllColumns(true);
	
	TableRow tableRow = new TableRow(this);
	tableRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	
	Spinner amount = new Spinner(this);
	//setting the food type spinner
	spin_adapter = new ArrayAdapter<String>(CreateRecipeActivity.this, android.R.layout.simple_spinner_item, Unitamount);
	amount.setAdapter(spin_adapter);
	amount.setPadding(3, 0, 0, 0);
	amount.setLayoutParams(new TableRow.LayoutParams(52, LayoutParams.MATCH_PARENT, 0.5f));
	listSpnrAmount.add(amount);
   
	
	
	Spinner unit = new Spinner(this);
	//setting the food type spinner
	spin_adapter = new ArrayAdapter<String>(CreateRecipeActivity.this, android.R.layout.simple_spinner_item, measurement);
	unit.setAdapter(spin_adapter);
	unit.setPadding(5, 0, 40, 0);
	unit.setLayoutParams(new TableRow.LayoutParams(120, LayoutParams.MATCH_PARENT, 0.5f));
	listSpnrMeasurement.add(unit);
	
	

	EditText txtDiscription = new EditText(this);
	txtDiscription.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
	//this limits the text inside EditText to maxLength
	int maxLength = 30;    
	InputFilter[] FilterArray = new InputFilter[1];
	FilterArray[0] = new InputFilter.LengthFilter(maxLength);
	txtDiscription.setFilters(FilterArray);
	txtDiscription.setHint(getString(R.string.dicedChoped));
	txtDiscription.setTypeface(typeFace);
	txtDiscription.setTextSize(13f);
	txtDiscription.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
	listDescription.add(txtDiscription);
	
    //adding all of the created widgets into the tableRow
	//then adding that tableRow into the tableLayout
	tableRow.addView(amount);
	tableRow.addView(unit);
	tableRow.addView(txtDiscription);
    tableLayout.addView(tableRow);
    
    return tableLayout;
}

/**
 * Clones an ArrayList
 * @param list
 * @return clonedList
 */
private List<String> cloneList(List<String> list) {
    List<String> clone = new ArrayList<String>(list.size());
    for(int i=0; i<list.size();i++){
    	clone.add(list.get(i));
    }
    return clone;
}

/**
 * 	drops any ingredient table that is no longer needed
 * @param list
 */
private void dropFromList(List<String> list) {
    
	Log.d("inside dropList: ", list.toString());
	
	for(int i=0; i<list.size() ; i++){
		
		//adding ':' to the end of the list because the text field has one as well
		//list.set(i, list.get(i).toString()+":");
		
		for(int y=0; y<listIngredientName.size(); y++){
			Log.d("inside loop: ", list.get(i).toString()+"=?="+listIngredientName.get(y).getText().toString());
			if(list.get(i).toString().equalsIgnoreCase(listIngredientName.get(y).getText().toString())){
				
				Log.d("inside if: ", list.get(i).toString());
				
				listTable.get(y).setVisibility(View.GONE);
				listIngredientName.remove(y);
				listDescription.remove(y);
				listSpnrMeasurement.remove(y);
				listSpnrAmount.remove(y);
				listVital.remove(y);
				listTable.remove(y);
			}
		}	
	}	
	
	Log.d("inside dropList: ", list.toString());
}


	/**
	 * Background Async Task to Create new Recipe
	 * */
	class CreateNewRecipe extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateRecipeActivity.this);
			pDialog.setMessage(getString(R.string.creatingRecipe));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
			
		}

		/**
		 * Creating recipe
		 * */
		protected String doInBackground(String... args) {
			
			String ingredientList="";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			
			//this loop gets the data from the ingredient list
			for(int i=0; i<numIngredients; i++){
				//if(listIngredientName.get(i))
				String name = listIngredientName.get(i).getText().toString(); 
				String amount = listSpnrAmount.get(i).getSelectedItem().toString(); 
				String measurement = listSpnrMeasurement.get(i).getSelectedItem().toString(); 
				String description = listDescription.get(i).getText().toString(); 
				Boolean vital = listVital.get(i).isChecked(); 
				
				int val = vital? 1 : 0;	
				
				//needed to remove the : from the end of the string
				//name = name.substring(0, name.length()-1);
				
				params.add(new BasicNameValuePair("ingredientName"+Integer.toString(i), name));
				params.add(new BasicNameValuePair("amount"+Integer.toString(i), amount));
				params.add(new BasicNameValuePair("measurement"+Integer.toString(i), measurement));
				params.add(new BasicNameValuePair("discription"+Integer.toString(i), description));
				params.add(new BasicNameValuePair("important"+Integer.toString(i), Integer.toString(val)));
				
				
				//adds the plural's' to the end of measurement
				if(listSpnrAmount.get(i).getSelectedItemPosition()>3)
					measurement+="s";
				
				//screens the possibility that no unit is needed
				if(measurement.equalsIgnoreCase("no unit")||measurement.equalsIgnoreCase("no units"))
					measurement="";
				else
					measurement+=" of";
				
				ingredientList +="> "+amount+" "+measurement+" "+description+" "+name+"\n";

			}
			
			Log.d("CreateRecipe ingredientList=", ingredientList);
			
			String recipeName= txtRecipeName.getText().toString();
			String cookingDirections= txtCookingDirections.getText().toString();
			String cookTime= txtCookTime.getText().toString();
			String prepTime= txtPrepTime.getText().toString();
			String summery= txtSummery.getText().toString();
			String recipeType = spnrRecipeType.getSelectedItem().toString();
			String servings = spnrServings.getSelectedItem().toString();

			// Building Parameters
			params.add(new BasicNameValuePair(TAG_RECIPENAME, recipeName));
			params.add(new BasicNameValuePair(TAG_INGREDIENTLIST, ingredientList));
			params.add(new BasicNameValuePair(TAG_COOKINGDIRECTIONS, cookingDirections));
			params.add(new BasicNameValuePair(TAG_COOKTIME, cookTime));
			params.add(new BasicNameValuePair(TAG_PREPTIME, prepTime));
			params.add(new BasicNameValuePair(TAG_SUMMERY, summery));
			params.add(new BasicNameValuePair(TAG_TYPE, recipeType));
			params.add(new BasicNameValuePair(TAG_SERVINGS, servings));
			params.add(new BasicNameValuePair(TAG_AUTHOR, userName));
			params.add(new BasicNameValuePair(TAG_TOKEN, token));

			Log.d("CreateRecipes params: ", params.toString());
			
			//checks to see if user took a picture
			if(recipeImage!=null){
				//compress' the image and then puts it in a base64 string to send
				//over to uploadImage.php
		        ByteArrayOutputStream stream = new ByteArrayOutputStream();
		        recipeImage.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
		        byte [] byte_arr = stream.toByteArray();
		        String image_str = Base64.encodeToString(byte_arr, 1);
		        ArrayList<NameValuePair> picParams = new  ArrayList<NameValuePair>();
	
		        picParams.add(new BasicNameValuePair(TAG_IMAGE,image_str));
		        picParams.add(new BasicNameValuePair(TAG_RECIPENAME,recipeName));
				
		        JSONObject json2 = jsonParser.makeHttpRequest(urlUploadImage, "POST", picParams);
		        
		        //resetting variable just in case
		        successfulPicture=false;
		        
				//if asyncTask has Not been cancelled then continue
				if (!bCancelled) try {
					
					// check log cat for response
					Log.d("Create Response", json2.toString());
					
					int success = json2.getInt(TAG_SUCCESS);
	
					if (success == 1) {
						
						successfulPicture=true;
						
						hasImage= json2.getInt(TAG_HASIMAGE);
						
						Log.d("CreateRecipe_Background picUpload", json2.getString(TAG_MESSAGE));
						
						// closing this screen
						//finish();
					} else {
						// failed to upload pic
						 message = json2.getString(TAG_MESSAGE);
						Log.d("CreateRecipe_Background", "oops! Failed to upload pic:  "+message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}//end if recipeImage!= null
			
			//if user has not taken a picture then it will be 0
			params.add(new BasicNameValuePair(TAG_HASIMAGE, Integer.toString(hasImage)));
						
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(urlCreateRecipe, "POST", params);

	        //resetting variable just in case
	        successfulRecipe=false;
	        
			//if asyncTask has Not been cancelled then continue
			if (!bCancelled) try {
				
				// check log cat for response
				Log.d("Create Response", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successfulRecipe=true;
					// successfully created Recipe
					Log.d("CreateRecipe_Background", "Success! Recipe Created");
				} else {
					// failed to create Recipe
					 message += json.getString(TAG_MESSAGE);
					Log.d("CreateRecipe_Background", "oops! Failed to create recipe "+message);
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
			if(successfulRecipe && successfulPicture){
				Toast.makeText(getApplicationContext(), getString(R.string.recipeCreated), Toast.LENGTH_LONG).show();
				// closing this screen
				finish();
			}
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


/***************************************************************************************************************/


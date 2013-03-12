package com.gmail.samos6;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import org.apache.http.NameValuePair;
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
public class EditRecipeActivity extends Activity {
	
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
	
	Button btnPublish;
	Button btnAddIngredient;
	Button btnTakePhoto;
	
	//used to fill the spinners
	String[] recipeType;
	String[] recipeServings;
	String[] measurement;
	String[] Unitamount;
	
	//oldRecipeName is used in case the recipe name
	//is changed (needed for UPDATE on the server-side)
	String oldRecipeName;
	String recipeName;
	
	//preference access
	SharedPreferences prefs;
	String userName="";
	String password="";
	
	String cookingDirections= "";
	String cookTime= "";
	String prepTime= "";
	String rawImage= "";
	String summery= "";
	String type= "";
	String servings= "";
	String imageUrl="";
	
	Bitmap recipeImage=null;
	boolean successfulRecipe = false;
	boolean successfulPicture = false;
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
	
	List<String> arrayDescription= new ArrayList<String>();
	List<String> arrayMeasurement= new ArrayList<String>();
	List<String> arrayAmount= new ArrayList<String>();
	List<String> arrayVital= new ArrayList<String>();

	Images_ImageLoader imgLoader;
	
	//this stores the number of ingredients selected
	int numIngredients=0;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	//Creating the variable that will hold the url when it is pulled from resources
	String urlEditRecipe;
	String urlUpdateRecipe; 
	String urlUploadImage;
	String urlRoot;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_OLDRECIPENAME = "oldRecipeName";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_INGREDIENTLIST = "ingredientList";
	private static final String TAG_COOKINGDIRECTIONS = "cookingDirections";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_PREPTIME = "prepTime";
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SERVINGS = "servings";
	private static final String TAG_INGREDIENTNAME = "ingredientName";
	private static final String TAG_AMOUNT = "amount";
	private static final String TAG_MEASUREMENT = "measurement";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_IMPORTANT = "important";
	private static final String TAG_NUMINGREDIENTS = "numIngredients";
	private static final String TAG_IMAGEURL = "imageUrl";
	private static final String TAG_IMAGE = "image";
	private static final String TAG_HASIMAGE = "hasImage";

	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_recipe);
		
		imgLoader = new Images_ImageLoader(getApplicationContext());
		
		//setting user name and password from preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		userName =prefs.getString("nickName", "guest");
		password =prefs.getString("password", "");
		
		
		//getting url from resources
		urlEditRecipe = getResources().getString(R.string.urlEditRecipe);
		urlUpdateRecipe = getResources().getString(R.string.urlUpdateRecipe);
		urlUploadImage = getResources().getString(R.string.urlUploadImage);
		urlRoot = getResources().getString(R.string.urlRoot);
		
		//loading up strings to be used with spinners
		recipeType = getResources().getStringArray(R.array.recipeType);
		recipeServings = getResources().getStringArray(R.array.spinnerServings);
		measurement = getResources().getStringArray(R.array.measurement);
		Unitamount = getResources().getStringArray(R.array.amount);
		
		
		//setting the recipe type spinner
		spnrRecipeType = (Spinner) findViewById(R.id.spnrCreateRecipeType);
		spin_adapter = new ArrayAdapter<String>(EditRecipeActivity.this, android.R.layout.simple_spinner_item, recipeType);
		spnrRecipeType.setAdapter(spin_adapter);
		
		//setting the servings spinner
		spnrServings = (Spinner) findViewById(R.id.spnrCreateRecipeServings);
		spin_adapter = new ArrayAdapter<String>(EditRecipeActivity.this, android.R.layout.simple_spinner_item, recipeServings);
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
			
		Log.d("EditRecipe_just in", "Inside");
		
		if(numIngredients==0){
			// getting ingredient details from intent
			Intent intent = getIntent();		
			// getting data past from intent
			oldRecipeName = intent.getStringExtra(TAG_RECIPENAME);
			new GetRecipeDetails().execute();
		}
			
		
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
					msg = "You need a recipe name.";
					incomplete=true;
				}else if(cookingDirections.matches("")){
					msg = "Please add some cooking directions.";
					incomplete=true;
				}else if(cookingDirections.matches("")){
					msg = "Please add some cooking directions.";
					incomplete=true;
				}else if(cookTime.matches("") || prepTime.matches("")){
					msg = "Please enter a time for cook time and prep time or 0.";
					incomplete=true;
				}else if(summery.matches("")){
					msg = "Please enter a short summery.";
					incomplete=true;
				}else if(numIngredients==0){
					msg = "Please add at least one ingredient.";
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
	
	
	/**
	 * This handles any changes in ingredients coming from getIngredientActivity (request code 100)
	 * OR gets the picture taken (request code 0) 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("inside ActivityResults", "...");	
		
		//result from taking a pic of recipe
		if(requestCode == 0){
			recipeImage = (Bitmap) data.getExtras().get("data");
			imgPicture.setImageBitmap(recipeImage);
			
		}
		
		// if result code 100, coming from GetIngredientActivity
		if (resultCode == 100) {
			
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
	
	/***************************************************************************************************
	 *  									void addDetails()
	 *  
	 *  			This method adds all of the recipe details from the query. 
	 * 
	 ****************************************************************************************************/
	public void addDetails(){
		
		createIngredientListView(ingredientList);
		int measurementPosition=0;
		int amountPosition=0;
		int typePosition=0;
		int servingsPosition=0;
		
		for(int i=0; i<ingredientList.size(); i++){
			
			//finding the position of measurement spinner
			for(int n=0; n<measurement.length; n++){
				if(arrayMeasurement.get(i).equalsIgnoreCase(measurement[n]))
					measurementPosition=n;
			}
			
			//finding the position of amount spinner
			for(int n=0; n<Unitamount.length; n++){
				if(arrayAmount.get(i).equalsIgnoreCase(Unitamount[n]))
					amountPosition=n;
			}
			
			listSpnrAmount.get(i).setSelection(amountPosition); 
			listSpnrMeasurement.get(i).setSelection(measurementPosition); 
			listDescription.get(i).setText(arrayDescription.get(i)); 
			Boolean val = Integer.parseInt(arrayVital.get(i).toString()) == 1? true : false;
			listVital.get(i).setChecked(val); 
		}
		
		txtRecipeName.setText(oldRecipeName);
		txtCookingDirections.setText(cookingDirections);
		txtCookTime.setText(cookTime);
		txtPrepTime.setText(prepTime);
		txtSummery.setText(summery);
		
		//finding the position of recipeType spinner
		for(int n=0; n<recipeType.length; n++){
			if(type.equalsIgnoreCase(recipeType[n]))
				typePosition=n;
		}
		
		//finding the position of servings spinner
		for(int n=0; n<recipeServings.length; n++){
			if(servings.equalsIgnoreCase(recipeServings[n]))
				servingsPosition=n;
		}
		
		spnrRecipeType.setSelection(typePosition);
		spnrServings.setSelection(servingsPosition);
		
		imgLoader.DisplayImage(urlRoot+imageUrl, imgPicture);
		
	}
	
private void createIngredientListView(List<String> list){
	
	//http://prasans.info/2011/03/add-edittexts-dynamically-and-retrieve-values-android/
	listLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	
	
	for(int index=0; index<list.size(); index++){
		
		TableLayout tableLayout = new TableLayout(this);
		tableLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.create_recipe_ingredient_list));
		
		TextView txtIngredientName = new TextView(this);
		txtIngredientName.setTextSize(21f);
		txtIngredientName.setText(list.get(index));
		txtIngredientName.setPadding(0, 30, 0, 0);
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
	txtVital.setText("Is this ingredient vital?");
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

	
	TableLayout tableLayout = new TableLayout(this);
	tableLayout.setStretchAllColumns(true);
	
	TableRow tableRow = new TableRow(this);
	tableRow.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	
	Spinner amount = new Spinner(this);
	//setting the food type spinner
	spin_adapter = new ArrayAdapter<String>(EditRecipeActivity.this, android.R.layout.simple_spinner_item, Unitamount);
	amount.setAdapter(spin_adapter);
	amount.setPadding(3, 0, 0, 0);
	amount.setLayoutParams(new TableRow.LayoutParams(52, LayoutParams.MATCH_PARENT, 0.5f));
	listSpnrAmount.add(amount);
   
	
	
	Spinner unit = new Spinner(this);
	//setting the food type spinner
	spin_adapter = new ArrayAdapter<String>(EditRecipeActivity.this, android.R.layout.simple_spinner_item, measurement);
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
	txtDiscription.setHint("diced, chopped, etc..");
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
			pDialog = new ProgressDialog(EditRecipeActivity.this);
			pDialog.setMessage("Updating Recipe..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
			
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			
			String ingredientList="";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			
			//this loop gets the data from the ingredient list
			for(int i=0; i<numIngredients; i++){

				String name = listIngredientName.get(i).getText().toString(); 
				String amount = listSpnrAmount.get(i).getSelectedItem().toString(); 
				String measurement = listSpnrMeasurement.get(i).getSelectedItem().toString(); 
				String description = listDescription.get(i).getText().toString(); 
				Boolean vital = listVital.get(i).isChecked(); 
				
				int val = vital? 1 : 0;	
				
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
			
			Log.d("EditRecipe ingredientList=", ingredientList);
			
			recipeName= txtRecipeName.getText().toString();
			String cookingDirections= txtCookingDirections.getText().toString();
			String cookTime= txtCookTime.getText().toString();
			String prepTime= txtPrepTime.getText().toString();
			String summery= txtSummery.getText().toString();
			String recipeType = spnrRecipeType.getSelectedItem().toString();
			String servings = spnrServings.getSelectedItem().toString();

			// Building Parameters
			params.add(new BasicNameValuePair(TAG_RECIPENAME, recipeName));
			params.add(new BasicNameValuePair(TAG_OLDRECIPENAME, oldRecipeName));
			params.add(new BasicNameValuePair(TAG_INGREDIENTLIST, ingredientList));
			params.add(new BasicNameValuePair(TAG_COOKINGDIRECTIONS, cookingDirections));
			params.add(new BasicNameValuePair(TAG_COOKTIME, cookTime));
			params.add(new BasicNameValuePair(TAG_PREPTIME, prepTime));
			params.add(new BasicNameValuePair(TAG_SUMMERY, summery));
			params.add(new BasicNameValuePair(TAG_TYPE, recipeType));
			params.add(new BasicNameValuePair(TAG_SERVINGS, servings));
			params.add(new BasicNameValuePair(TAG_AUTHOR, userName));

			Log.d("EditRecipe params: ", params.toString());
			
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
		        picParams.add(new BasicNameValuePair(TAG_RECIPENAME, recipeName));
				
		        JSONObject json2 = jsonParser.makeHttpRequest(urlUploadImage, "POST", picParams);
		        
		        //resetting variable just in case
		        successfulPicture=false;
		        
				//if asyncTask has Not been cancelled then continue
				if (!bCancelled) try {
					
					// check log cat for response
					Log.d("Create Response for pic", json2.toString());
					
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
			JSONObject json = jsonParser.makeHttpRequest(urlUpdateRecipe, "POST", params);

			//if asyncTask has Not been cancelled then continue
			if (!bCancelled) try {
				
				// check log cat for response
				Log.d("Create Response", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successfulRecipe=true;
					// successfully created Recipe
					Log.d("EditRecipe_Background", "Success! Recipe Updated");
					// closing this screen
					Intent intent= getIntent();
					intent.putExtra(TAG_RECIPENAME, recipeName);
                    setResult(100, intent);
					finish();
				} else {
					// failed to create Recipe
					 message += json.getString(TAG_MESSAGE);
					Log.d("EditRecipe_Background", "oops! Failed to update recipe "+message);
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
			if(successfulRecipe && successfulPicture)
				Toast.makeText(getApplicationContext(), "Recipe Updated", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}

	}
	
	
	/**
	 * Background Async Task to Get complete recipe details
	 * */
	class GetRecipeDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditRecipeActivity.this);
			pDialog.setMessage("Loading Recipe details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * Getting recipe details in background thread
		 * */
		protected String doInBackground(String... args) {

					// Check for success tag
					int success;
					//if the AsyncTask has Not been cancelled then continue
					if(!bCancelled) try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("recipeName", oldRecipeName));

						// getting Ingredient details by making HTTP request
						JSONObject json = jsonParser.makeHttpRequest( urlEditRecipe, "POST", params);

						// check your log for json response
						Log.d("Single recipe Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray products = json.getJSONArray(TAG_PRODUCT); // JSON Array
							
							// get first ingredient object from JSON Array
							JSONObject product = products.getJSONObject(0);

							//Getting details from the query
							Log.d("RecipeView_DoinBackGround", "setting all of the details");
							summery = product.getString(TAG_SUMMERY);
							cookingDirections = product.getString(TAG_COOKINGDIRECTIONS);
							prepTime = product.getString(TAG_PREPTIME);
							cookTime = product.getString(TAG_COOKTIME);
							type = product.getString(TAG_TYPE);
							servings = product.getString(TAG_SERVINGS);
							numIngredients = Integer.parseInt(product.getString(TAG_NUMINGREDIENTS));
							imageUrl = product.getString(TAG_IMAGEURL);
							hasImage = Integer.parseInt(product.getString(TAG_HASIMAGE));
							
							
							for(int i=0; i<numIngredients; i++){
								
								ingredientList.add(product.getString(TAG_INGREDIENTNAME+""+Integer.toString(i)));
								arrayDescription.add(product.getString(TAG_DESCRIPTION+Integer.toString(i)));
								arrayMeasurement.add(product.getString(TAG_MEASUREMENT+Integer.toString(i)));
								arrayAmount.add(product.getString(TAG_AMOUNT+Integer.toString(i)));
								arrayVital.add(product.getString(TAG_IMPORTANT+Integer.toString(i)));
								
							}

						}else{	
							// recipe with that name not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					Log.d("EditRecipe_DoinBackGround", "before return");
			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			Log.d("EditRecipe_PostExecute", "in post execute");
			//is called to add all of the details to the fields
			addDetails();
			// dismiss the dialog once got all details
			pDialog.dismiss();
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



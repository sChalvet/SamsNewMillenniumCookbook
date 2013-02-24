package com.gmail.samos6;

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
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	EditText txtIngredientList;
	EditText txtCookingDirections;
	EditText txtCookTime;
	EditText txtPrepTime;
	EditText txtSummery;
	Spinner spnrRecipeType;
	ImageView imgPicture;
	SlidingDrawer slidingDrawer;
	
	LinearLayout listLayout;
	
	Button btnPublish;
	Button btnTakePhoto;
	
	String recipeName= "";
	String ingredientList= "";
	String cookingDirections= "";
	String cookTime= "";
	String prepTime= "";
	String summery= "";
	String author= "Van Keizer";
	String[] recipeType;
	
	boolean successful = false;
	String message = "";
	
	ArrayAdapter<String> spin_adapter;
	
	//these lists store the values of the generated ingredient list
	List<String> list= new ArrayList<String>();
	List<TextView> listIngredientName= new ArrayList<TextView>();
	List<EditText> listDescription= new ArrayList<EditText>();
	List<Spinner> listSpnrMeasurement= new ArrayList<Spinner>();
	List<Spinner> listSpnrAmount= new ArrayList<Spinner>();
	
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
	String urlUpdateRecipe; 
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_PRODUCT = "product";
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_NUMREVIEWS = "numReviews";
	private static final String TAG_INGREDIENTLIST = "ingredientList";
	private static final String TAG_COOKINGDIRECTIONS = "cookingDirections";
	private static final String TAG_RATINGS = "rating";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_PREPTIME = "prepTime";
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_TYPE = "type";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_recipe);
		
		//getting url from resources
		urlCreateRecipe = getResources().getString(R.string.urlCreateRecipe);
		urlUpdateRecipe = getResources().getString(R.string.urlUpdateRecipe);
		
		recipeType = getResources().getStringArray(R.array.recipeType);
		
		
		//setting the recipe type spinner
		spnrRecipeType = (Spinner) findViewById(R.id.spnrCreateRecipeType);
		spin_adapter = new ArrayAdapter<String>(CreateRecipeActivity.this, android.R.layout.simple_spinner_item, recipeType);
		spnrRecipeType.setAdapter(spin_adapter);
			
		// Initializing all of the text fields and buttons
		txtRecipeName = (EditText) findViewById(R.id.inputCreateRecipeName);
		txtIngredientList= (EditText) findViewById(R.id.inputRecipeCreateIngredientList);
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
			
		Log.d("CreateRecipe_just in", "Inside");
	
		
		
		// publish to DB button click event
		btnPublish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Log.d("CreateRecipe_btnPost onclick", "inside");
				
				for(int i=0; i<numIngredients; i++){
					String name = listIngredientName.get(i).getText().toString(); 
					String am = listSpnrAmount.get(i).getSelectedItem().toString(); 
					String mea = listSpnrMeasurement.get(i).getSelectedItem().toString(); 
					String des = listDescription.get(i).getText().toString(); 
					Log.d("CreateRecipe_btnPost onclick s=", am+" "+mea+" of "+des+" "+name);
				}
				
				//new CreateNewRecipe().execute();
				

			}
		});
		
		// take pic button click event
		btnTakePhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), GetIngredientActivity.class);

				startActivityForResult(intent,100);
				Log.d("CreateRecipe_btnTakePhoto onclick", "inside");
				
				//Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				//startActivityForResult(intent, 0);
				

			}
		});

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
			Bitmap theImage = (Bitmap) data.getExtras().get("data");
			imgPicture.setImageBitmap(theImage);
			
		}
		
		// if result code 100, coming from GetIngredientActivity
		if (resultCode == 100) {
			
			Bundle extras= data.getExtras();
			
			list = extras.getStringArrayList("myarraylist");
			Log.d("inside ActivityResults got this list: ", list.toString());
			
			//use the recieved ingredients and get them ready for user
			createIngredientListView(list);
		}

	}
	
private void createIngredientListView(List<String> list){
	
	//http://prasans.info/2011/03/add-edittexts-dynamically-and-retrieve-values-android/
	listLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	
	numIngredients=list.size();
	for(int index=0; index<list.size(); index++){
		
		TextView txtIngredientName = new TextView(this);
		txtIngredientName.setId(index);
		txtIngredientName.setText(list.get(index)+":");
		listLayout.addView(txtIngredientName);
		listIngredientName.add(txtIngredientName);
		
		listLayout.addView(ingredientRow(index));
	}
	//View view =  (View) findViewById(R.id.ingredientRowLayout);
	//listLayout.addView((View) findViewById(R.id.ingredientRowLayout));
	
	
}

private TableLayout ingredientRow(int index) {
	
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
	txtDiscription.setHint("diced, chopped, etc..");
	txtDiscription.setTextSize(13f);
	txtDiscription.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
	listDescription.add(txtDiscription);
	
    
	tableRow.addView(amount);
	tableRow.addView(unit);
	tableRow.addView(txtDiscription);
    tableLayout.addView(tableRow);
    return tableLayout;
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
			pDialog.setMessage("Creating Recipe..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
			
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {
			
			String recipeName= txtRecipeName.getText().toString();
			String ingredientList= txtIngredientList.getText().toString();
			String cookingDirections= txtCookingDirections.getText().toString();
			String cookTime= txtCookTime.getText().toString();
			String prepTime= txtPrepTime.getText().toString();
			String summery= txtSummery.getText().toString();
			String recipeType = spnrRecipeType.getSelectedItem().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_RECIPENAME, recipeName));
			params.add(new BasicNameValuePair(TAG_INGREDIENTLIST, ingredientList));
			params.add(new BasicNameValuePair(TAG_COOKINGDIRECTIONS, cookingDirections));
			params.add(new BasicNameValuePair(TAG_COOKTIME, cookTime));
			params.add(new BasicNameValuePair(TAG_PREPTIME, prepTime));
			params.add(new BasicNameValuePair(TAG_SUMMERY, summery));
			params.add(new BasicNameValuePair(TAG_TYPE, recipeType));
			params.add(new BasicNameValuePair(TAG_AUTHOR, author));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(urlCreateRecipe, "GET", params);

			//if asyncTask has Not been cancelled then continue
			if (!bCancelled) try {
				
				// check log cat for response
				Log.d("Create Response", json.toString());
				
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					successful=true;
					// successfully created Recipe
					//Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
					//startActivity(i);
					Log.d("CreateRecipe_Background", "Success! Recipe Created");
					// closing this screen
					finish();
				} else {
					// failed to create Recipe
					 message = json.getString(TAG_MESSAGE);
					Log.d("CreateRecipe_Background", "oops! Failed to create recipe"+message);
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
				Toast.makeText(getApplicationContext(), "Recipe Created", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		}

	}

}


/***************************************************************************************************************/


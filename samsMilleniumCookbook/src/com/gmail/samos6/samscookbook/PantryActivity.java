package com.gmail.samos6.samscookbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PantryActivity extends ListActivity {
	
	
	// Progress Dialog
	private ProgressDialog pDialog;

	SamsListAdapter adapter;
	
	Button btnEdit;
	Button btnSearch;
	Button btnDeleteIngredient;
	Button btnSelectAll;
	RadioGroup searchType;

	//used to set font
	Typeface typeFace; 
	
	//used to see if user canceled the AsyncTask
	Boolean bCancelled=false;
	
	Boolean isEmpty=false;

	ArrayList<HashMap<String, String>> productsList;

	private AlertDialog alert;
	
	//Instantiating the SQLite database
	final DatabaseHandler db = new DatabaseHandler(this);

	private static final String TAG_INGREDIENTNAME = "ingredientName";
	private static final String TAG_PANTRY = "pantry";
	private static final String TAG_ORIGIN = "origin";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pantry_view);
		
		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadingPantry().execute();

		// Get listview
		final ListView lv = getListView();  //added final
		
		
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnEdit = (Button) findViewById(R.id.btnEditPantry);
		btnDeleteIngredient = (Button) findViewById(R.id.btnDeleteIngredients);
		btnSelectAll= (Button) findViewById(R.id.btnSelectAll);	
		
		//setting the font type from assets		
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		btnSearch.setTypeface(typeFace);
		btnEdit.setTypeface(typeFace);
		btnDeleteIngredient.setTypeface(typeFace);
		btnSelectAll.setTypeface(typeFace);
		
		// Search by ingredient on click Event
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				//Log.d("Pantry_Search onclick", "inside");
				
				if(db.getIngredientCount()>0){
					List<String> ingredientList = adapter.getChecked();
				
					//Log.d("Pantry_Search onclick list=", ingredientList.toString());
					
					if(ingredientList.isEmpty()){
						Toast.makeText(getApplicationContext(), getString(R.string.selectIngredients), Toast.LENGTH_SHORT).show();
					}else{
						
						//launches dialog for user so the search type
						choseSearchAlertDialog(ingredientList);
						
					}
				}else{	//no ingredients to be selected
					// Starting new intent
					Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
					
				
					// starting new activity and expecting some response back
					startActivityForResult(i, 100);
				}

				

			}
		});
		
		
		// Add to pantry click event
		btnEdit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//Log.d("Pantry_btnEdit: ", "inside OnClick");
				
				// Starting new intent
				Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
				
			
				// starting new activity and expecting some response back
				startActivityForResult(i, 100);

			}
		});
		
		btnDeleteIngredient.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//Log.d("Pantry_btnDeleteIngredient: ", "inside OnClick");
				
				List<String> list = new ArrayList<String>();
				
				list= adapter.getChecked();
				//Log.d("Pantry_btnDeleteIngredient list= ", list.toString());
				
				db.deleteListIngredient(list);
				Intent intent = getIntent();
				finish();
				startActivity(intent);

			}
		});
		
		btnSelectAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
			//	Log.d("Pantry_selectAll: ", "inside OnClick "+ adapter.getCount()+", select= "+adapter.numSelected()+",  Total num="+lv.getChildCount());
				
				
				
				if(adapter.getCount()== adapter.numSelected()){ //deselects all
					for(int index=0; index<adapter.getCount(); index++){
						try{
							((CheckBox) lv.getChildAt(index).findViewById(R.id.ingredientCheckBox)).setChecked(false);
						} catch (Exception e){
							
						}
					}
				}else{	//selects all
					
					for(int index=0; index<adapter.getCount(); index++){
						try{
							if(!((CheckBox) lv.getChildAt(index).findViewById(R.id.ingredientCheckBox)).isChecked())
								((CheckBox) lv.getChildAt(index).findViewById(R.id.ingredientCheckBox)).setChecked(true);	
						} catch (Exception e){
							
						}
						
					}
				}
				
				adapter.selectAll();
				
				List<String> list = new ArrayList<String>();
				
				list= adapter.getChecked();
				//Log.d("Pantry_selectAll list= ", list.toString());

			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
						
				// getting values from selected ListItem
				String ingredientname = ((TextView) view.findViewById(R.id.ingredientName)).getText().toString();
				
				//Log.d("Pantry_ItemClick: ", ingredientname);
				// Starting new intent
				Intent intent = new Intent(getApplicationContext(), EditIngredientActivity.class);
				// sending ingredientName to next activity
				intent.putExtra(TAG_INGREDIENTNAME, ingredientname);
				intent.putExtra(TAG_ORIGIN, TAG_PANTRY);
				
				// starting new activity and expecting some response back
				startActivityForResult(intent, 100);

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

	// Response from IngredientList Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted ingredient
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
	class LoadingPantry extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PantryActivity.this);
			pDialog.setMessage(getString(R.string.loadingIngredients));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.setOnCancelListener(cancelListener);
			pDialog.show();
		}

		/**
		 * getting All products from SQLite
		 * */
		@Override
		protected String doInBackground(String... args) {
			
			//if AsyncTask has Not been cancelled continue
			if(!bCancelled){
				
				// Building Parameters
				
				List<String> ingredientList = new ArrayList<String>(); 	
	
				ingredientList = db.getAllIngredients();
				
				//checking if pantry database is empty
				if(ingredientList.size()>0){			
					// Check your log cat for DB response
					//log.d("Pantry: ", ingredientList.toString());
		
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
					
					////log.d("Pantry_productList: ", productsList.toString());
				}else{
					//pantry DB is empty
					isEmpty=true;
				}
			}//end if

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			
			if(isEmpty){
				Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.yourPantryEmpty), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				
				// Starting new intent
				Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
				// starting new activity and expecting some response back
				startActivityForResult(i, 100);		
			}else{
				// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						/**
						 * Updating parsed JSON data into ListView
						 * */
						
						List<String> list = new ArrayList<String>();
						list = db.getAllIngredients();
						//Log.d("Pantry_callingAdapter with: ", list.toString());
						adapter = new SamsListAdapter(PantryActivity.this, productsList, list, "Pantry", typeFace);
						
						setListAdapter(adapter);
					}
				});
			}

		}
		

	}
	
	/**
	 * Spawns a login dialog box
	 */
	private void choseSearchAlertDialog(final List<String> ingredientList){
	  
	      LayoutInflater factory = LayoutInflater.from(PantryActivity.this);            
	        final View textEntryView = factory.inflate(R.layout.search_type, null);
	        
	        
	    	searchType= (RadioGroup) textEntryView.findViewById(R.id.radioGroup);

	    	((Button) textEntryView.findViewById(R.id.dialogPantrySearch)).setTypeface(typeFace);
	    	((RadioButton) textEntryView.findViewById(R.id.radioFindAll)).setTypeface(typeFace);
	    	((RadioButton) textEntryView.findViewById(R.id.radioFindAny)).setTypeface(typeFace);
	    	
			alert = new AlertDialog.Builder(PantryActivity.this).create();
			alert.setTitle(R.string.chooseSearchType);
			alert.setIcon(R.drawable.icon_37_by_37);
			
			alert.setView(textEntryView);

			textEntryView.findViewById(R.id.dialogPantrySearch).setOnClickListener(new OnClickListener() {
			    @Override
              public void onClick(View v) {
			    
			    	int index = searchType.indexOfChild(searchType.findViewById(searchType.getCheckedRadioButtonId()));
					
					//log.d("inside pantry dialog", "index= "+Integer.toString(index) );
					
					Intent intent = new Intent(getApplicationContext(), SearchForRecipeByIngredientActivity.class);
					Bundle b = new Bundle();
	                b.putStringArrayList("IngredientList", (ArrayList<String>) ingredientList);
	                intent.putExtras(b);
	                intent.putExtra("index", Integer.toString(index));
					startActivityForResult(intent,100);
			    }
          }); 
			
			
			alert.show();
		
	};
	
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
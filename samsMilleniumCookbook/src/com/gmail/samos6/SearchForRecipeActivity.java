package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class SearchForRecipeActivity extends Activity{
	
	Button btnSearchRecipes;
	Spinner spnrFoodType;
	Spinner spnrRecipeType;
	Spinner spnrCooktime;
	EditText txtAuthorName;
	EditText txtSearchKeyWords;
	
	ArrayAdapter<String> spin_adapter;
	
	//needed to add the "any" option to the food type
	String[] foodTypeTemp;
	String[] foodType;
	String[] recipeType;
	String[] cookTime;
	
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_FOODNAME = "foodName";
	private static final String TAG_RECIPETYPE = "recipeType";
	private static final String TAG_COOKTIME = "cookTime";
	private static final String TAG_KEYWORD = "keyWord";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_for_recipe);
		
		//getting arrays from from the values folder
		foodTypeTemp = getResources().getStringArray(R.array.foodType);
		recipeType = getResources().getStringArray(R.array.recipeType);
		cookTime = getResources().getStringArray(R.array.cookTime);
		
		//this appends 'any' to the begining of foodType (used only for this search)
		foodType = new String[foodTypeTemp.length+1];
		foodType[0]="Any";
		for(int i=1; i<(foodTypeTemp.length+1); i++){
			foodType[i]=foodTypeTemp[i-1];
		}
		
		//setting the btn and txt
		btnSearchRecipes = (Button) findViewById(R.id.btnSearchRecipes);
		txtAuthorName = (EditText) findViewById(R.id.txtAuthorName);
		txtSearchKeyWords = (EditText) findViewById(R.id.txtSearchKeyWords);
		
		//setting the food type spinner
		spnrFoodType = (Spinner) findViewById(R.id.spnrFoodType);
		spin_adapter = new ArrayAdapter<String>(SearchForRecipeActivity.this, android.R.layout.simple_spinner_item, foodType);
		spnrFoodType.setAdapter(spin_adapter);
		
		//setting the recipe type spinner
		spnrRecipeType = (Spinner) findViewById(R.id.spnrRecipeType);
		spin_adapter = new ArrayAdapter<String>(SearchForRecipeActivity.this, android.R.layout.simple_spinner_item, recipeType);
		spnrRecipeType.setAdapter(spin_adapter);
		
		//setting the cook time spinner
		spnrCooktime = (Spinner) findViewById(R.id.spnrCooktime);
		spin_adapter = new ArrayAdapter<String>(SearchForRecipeActivity.this, android.R.layout.simple_spinner_item, cookTime);
		spnrCooktime.setAdapter(spin_adapter);
		
		
		// search for recipes click event
		btnSearchRecipes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {

				String author =  txtAuthorName.getText().toString();
				String foodName = spnrFoodType.getSelectedItem().toString();
				String recipeType = spnrRecipeType.getSelectedItem().toString();
				String cook = spnrCooktime.getSelectedItem().toString();
				String keyword = txtSearchKeyWords.getText().toString();
				
				Log.d("SearchActivity_params=", author+" "+foodName+" "+recipeType+" "+cook+" "+keyword);
				
				Intent intent = new Intent(getApplicationContext(), ListRecipeActivity.class);
				intent.putExtra(TAG_AUTHOR, author);
				intent.putExtra(TAG_FOODNAME, foodName);	
				intent.putExtra(TAG_RECIPETYPE, recipeType);
				intent.putExtra(TAG_COOKTIME, cook);	
				intent.putExtra(TAG_KEYWORD, keyword);	
				startActivity(intent);
				
				
				
				
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

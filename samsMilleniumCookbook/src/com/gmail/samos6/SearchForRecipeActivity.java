package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	
	String[] foodType = { "Any", "Beef", "Poultry", "Egg", "Pork", "Seafood", "Lamb", "Pasta"};
	String[] recipeType = { "Any", "Breackfast", "Main Dish", "Salad", "Soup", "Side Dish", "Bread", "Dessert", "Appetizer"};
	String[] cookTime = { "Any", "10 min or Less", "20 min", "30 min", "40 min", "50 min", "1 hour", "1 hour 30 min", "2 hours or Longer", "Over Night"};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_for_recipe);
		
		btnSearchRecipes = (Button) findViewById(R.id.btnSearchRecipes);
		txtAuthorName = (EditText) findViewById(R.id.txtAuthorName);
		txtSearchKeyWords = (EditText) findViewById(R.id.txtSearchKeyWords);
		
		spnrFoodType = (Spinner) findViewById(R.id.spnrFoodType);
		spin_adapter = new ArrayAdapter<String>(SearchForRecipeActivity.this, android.R.layout.simple_spinner_item, foodType);
		spnrFoodType.setAdapter(spin_adapter);
		
		
		spnrRecipeType = (Spinner) findViewById(R.id.spnrRecipeType);
		spin_adapter = new ArrayAdapter<String>(SearchForRecipeActivity.this, android.R.layout.simple_spinner_item, recipeType);
		spnrRecipeType.setAdapter(spin_adapter);
		
		spnrCooktime = (Spinner) findViewById(R.id.spnrCooktime);
		spin_adapter = new ArrayAdapter<String>(SearchForRecipeActivity.this, android.R.layout.simple_spinner_item, cookTime);
		spnrCooktime.setAdapter(spin_adapter);
		
		
		// search for recipes click event
		btnSearchRecipes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {

				String name =  txtAuthorName.getText().toString();
				String foodName = spnrFoodType.getSelectedItem().toString();
				String recipeName = spnrRecipeType.getSelectedItem().toString();
				String cook = spnrCooktime.getSelectedItem().toString();
				String keyword = txtSearchKeyWords.getText().toString();
				
				Log.d("SearchActivity_params=", name+" "+foodName+" "+recipeName+" "+cook+" "+keyword);
				//Log.d("SearchActivity_params=", "inside btnsearch listener");
				
				
				
			}
		});
		
		


	}
}

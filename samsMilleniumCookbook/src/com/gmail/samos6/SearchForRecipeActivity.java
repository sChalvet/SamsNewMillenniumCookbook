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
	
	String[] foodType;
	String[] recipeType;
	String[] cookTime;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_for_recipe);
		
		//getting arrays from from the values folder
		foodType = getResources().getStringArray(R.array.foodType);
		recipeType = getResources().getStringArray(R.array.recipeType);
		cookTime = getResources().getStringArray(R.array.cookTime);
		
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

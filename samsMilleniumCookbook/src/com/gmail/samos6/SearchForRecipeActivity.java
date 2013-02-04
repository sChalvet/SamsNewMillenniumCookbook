package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


public class SearchForRecipeActivity extends Activity{
	
	Button btnSearch;
	Spinner spnrFoodType;
	//Spinner spnrFoodType;
	//Spinner spnrFoodType;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_for_recipe);
		
		final DatabaseHandler db = new DatabaseHandler(this);
		
		// Buttons
		//btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
		//btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
		//btnPantry = (Button) findViewById(R.id.btnPantry);
		
		// view products click event
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
				startActivity(i);
				
			}
		});


	}
}

package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainScreenActivity extends Activity{
	
	Button btnViewProducts;
	Button btnNewProduct;
	Button btnPantry;
	Button btnToRecipeSearch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		final DatabaseHandler db = new DatabaseHandler(this);
		
		// Buttons
		btnViewProducts = (Button) findViewById(R.id.btnViewProducts);
		btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
		btnPantry = (Button) findViewById(R.id.btnPantry);
		btnToRecipeSearch = (Button) findViewById(R.id.btnToRecipeSearch);
		

		btnViewProducts.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						// Launching All products Activity
						Intent i = new Intent(getApplicationContext(), ListIngredientActivity.class);
						startActivity(i);
						
					}
				});

		btnToRecipeSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), SearchForRecipeActivity.class);
				startActivity(i);
				
			}
		});

		btnPantry.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				// Launching create new product activity
				Intent i = new Intent(getApplicationContext(), PantryActivity.class);
				startActivity(i);
				
				//List<String> list= new ArrayList<String>();
				
				//list = db.getAllIngredients();
				
				//Log.d("Inside main pantry", list.toString());
				
			}
		});
		
		// view products click event
		btnNewProduct.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching create new product activity
				Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
				startActivity(i);
				
			}
		});
	}
}

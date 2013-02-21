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
	
	Button btnSaved;
	Button btnFavorites;
	Button btnAddRecipe;
	Button btnNewProduct;
	Button btnPantry;
	Button btnToRecipeSearch;
	
	String[] userInfo={"123", "samos6@gmail.com", "Sam", "Chalvet", "1987-11-15", "test123"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState); 
	setContentView(R.layout.main_screen);
		
	final DatabaseHandler db = new DatabaseHandler(this);
	
		
		// Buttons
		btnSaved = (Button) findViewById(R.id.btnMainSavedRecipe);
		btnFavorites = (Button) findViewById(R.id.btnMainFavRecipe);
		btnNewProduct = (Button) findViewById(R.id.btnCreateProduct);
		btnPantry = (Button) findViewById(R.id.btnMainPantry);
		btnToRecipeSearch = (Button) findViewById(R.id.btnMainToRecipeSearch);
		btnAddRecipe = (Button) findViewById(R.id.btnMainAddRecipe);
		

		btnAddRecipe.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), CreateRecipeActivity.class);
				startActivity(i);
				
			}
		});
		
		btnSaved.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), SavedRecipesActivity.class);
				startActivity(i);
				
			}
		});
		
		btnFavorites.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), FavoriteRecipesActivity.class);
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
		/*btnNewProduct.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching create new product activity
				Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
				startActivity(i);
				
			}
		});*/
	}
}

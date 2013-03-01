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


public class PreferencesActivity extends Activity{
	
	Button btnSave;
	EditText txtNickName;
	EditText txtEmail;
	EditText txtFirstName;
	EditText txtLastName;
	EditText txtPassword;
	
	
	String[] foodType;
	String[] recipeType;
	String[] cookTime;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout);
		
		//getting arrays from from the values folder
		foodType = getResources().getStringArray(R.array.foodType);
		recipeType = getResources().getStringArray(R.array.recipeType);
		cookTime = getResources().getStringArray(R.array.cookTime);
		
		//setting the btn and txt
		btnSave = (Button) findViewById(R.id.btnPreferenceSave);
		txtNickName = (EditText) findViewById(R.id.userNickName);
		txtEmail = (EditText) findViewById(R.id.userEmail);
		txtFirstName = (EditText) findViewById(R.id.userFirstName);
		txtLastName = (EditText) findViewById(R.id.userLastName);
		txtPassword = (EditText) findViewById(R.id.userPassword);
		
	
		
		// search for recipes click event
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {

				//String name =  txtAuthorName.getText().toString();
				//String foodName = spnrFoodType.getSelectedItem().toString();
				//String recipeName = spnrRecipeType.getSelectedItem().toString();
				//String cook = spnrCooktime.getSelectedItem().toString();
				//String keyword = txtSearchKeyWords.getText().toString();
				
				Log.d(" inside preferences=", "inside onclick");
				
				Intent i = new Intent(getApplicationContext(), ListRecipeActivity.class);
				startActivity(i);
				//Log.d("SearchActivity_params=", "inside btnsearch listener");
				
				
				
			}
		});
		
		


	}
}

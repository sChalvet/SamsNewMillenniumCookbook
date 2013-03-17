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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;


public class NutritionInfoActivity extends Activity implements OnSeekBarChangeListener{
	
	TextView txtCalories;
	TextView txtProtein;
	TextView txtFat;
	TextView txtCarbs;
	TextView txtServings;
	SeekBar sbrServings;
	
	String calories;
	String protein;
	String fat;
	String carbs;
	String servings;
	
	private static final String TAG_CALORIES = "calories";
	private static final String TAG_FAT = "fat";
	private static final String TAG_PROTEIN = "protein";
	private static final String TAG_SERVINGS = "servings";
	private static final String TAG_CARBS = "carbs";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipe_nutrition);
		
		// getting ingredient details from intent
		Intent intent = getIntent();
		
		// getting data past from intent
		calories = intent.getStringExtra(TAG_CALORIES);
		protein = intent.getStringExtra(TAG_FAT);
		fat = intent.getStringExtra(TAG_PROTEIN);
		servings = intent.getStringExtra(TAG_SERVINGS);
		carbs = intent.getStringExtra(TAG_CARBS);
		
		//setting the btn and txt
		txtCalories = (TextView) findViewById(R.id.txtNutritionCalories);
		txtProtein = (TextView) findViewById(R.id.txtNutritionProtein);
		txtFat = (TextView) findViewById(R.id.txtNutritionFat);
		txtCarbs = (TextView) findViewById(R.id.txtNutritionCarbs);
		txtServings = (TextView) findViewById(R.id.txtNutritionServings);
		sbrServings = (SeekBar) findViewById(R.id.seekBarServings);
		
		//set max of seek bar to twice the serving amount
		sbrServings.setMax(Integer.parseInt(servings)*2);
		sbrServings.setProgress(Integer.parseInt(servings));
		
		setNutritionText(calories, protein, fat, carbs, servings);
		
		sbrServings.setOnSeekBarChangeListener(this);
	}
	
	private void setNutritionText(String cal, String pro, String fat, String carb, String serv){
		
		txtCalories.setText(cal);
		txtProtein.setText(pro);
		txtFat.setText(fat);
		txtCarbs.setText(carb);
		txtServings.setText(serv);		
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

	@Override
	public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
		txtServings.setText(Integer.toString(progress));	
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	} 
}

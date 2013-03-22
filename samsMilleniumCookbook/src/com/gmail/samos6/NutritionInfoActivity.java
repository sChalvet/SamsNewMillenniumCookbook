package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
	
	float calories;
	float protein;
	float fat;
	float carbs;
	String servings;
	
	//used to set font
	Typeface typeFace;
	
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
		calories = intent.getFloatExtra(TAG_CALORIES, 0.0f);
		protein = intent.getFloatExtra(TAG_FAT, 0.0f);
		fat = intent.getFloatExtra(TAG_PROTEIN, 0.0f);
		servings = intent.getStringExtra(TAG_SERVINGS);
		carbs = intent.getFloatExtra(TAG_CARBS, 0.0f);
		
		Log.d("inside nutrition facts", "cal: "+calories+"prot: "+protein+"fat: "+fat+"carb: "+carbs+"serving: "+servings);
		//setting the btn and txt
		txtCalories = (TextView) findViewById(R.id.txtNutritionCalories);
		txtProtein = (TextView) findViewById(R.id.txtNutritionProtein);
		txtFat = (TextView) findViewById(R.id.txtNutritionFat);
		txtCarbs = (TextView) findViewById(R.id.txtNutritionCarbs);
		txtServings = (TextView) findViewById(R.id.txtNutritionServings);
		sbrServings = (SeekBar) findViewById(R.id.seekBarServings);
		
		//setting the font type from assets		
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/KELMSCOT.ttf");
		txtCalories.setTypeface(typeFace);
		txtProtein.setTypeface(typeFace);
		txtFat.setTypeface(typeFace);
		txtCarbs.setTypeface(typeFace);
		txtServings.setTypeface(typeFace);
		
		//set max of seek bar to twice the serving amount, 
		//adding -1 because the min needs to be 1 it is then added later
		sbrServings.setMax((Integer.parseInt(servings)*2)-1);
		sbrServings.setProgress(Integer.parseInt(servings)-1);
		
		setNutritionText(calories, protein, fat, carbs, servings);
		
		sbrServings.setOnSeekBarChangeListener(this);
	}
	
	private void setNutritionText(float fCalorie, float fProtein, float fFat, float fCarb, String serv){
		
		int fServing = Integer.parseInt(serv);
		
		
		txtCalories.setText(Float.toString(Math.round(fCalorie/fServing * 10) / 10.0f));
		txtProtein.setText(Float.toString(Math.round(fProtein/fServing * 10) / 10.0f));
		txtFat.setText(Float.toString(Math.round(fFat/fServing * 10) / 10.0f));
		txtCarbs.setText(Float.toString(Math.round(fCarb/fServing * 10) / 10.0f));
		txtServings.setText(serv);		
	}

	@Override
	public void onProgressChanged(SeekBar v, int progress, boolean isUser) {
		setNutritionText(calories, protein, fat, carbs, Integer.toString(progress+1));	
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
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

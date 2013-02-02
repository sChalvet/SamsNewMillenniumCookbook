package com.gmail.samos6;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "recipeApp";
 
    // recipeApp table name
    private static final String TABLE_INGREDIENTSATHAND = "ingredientsAtHand";
 
    // ingredientsAtHand Table Columns names
    private static final String KEY_INGREDIENTNAME = "ingredientName";
   // private static final String KEY_NAME = "name";
   // private static final String KEY_PH_NO = "phone_number";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INGREDIENTSATHAND_TABLE = "CREATE TABLE " + TABLE_INGREDIENTSATHAND + "("
                + KEY_INGREDIENTNAME + " TEXT PRIMARY KEY)";
        db.execSQL(CREATE_INGREDIENTSATHAND_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTSATHAND);
 
        // Create tables again
        onCreate(db);
    }
    
 // Adding new ingredient
    public void addIngredient(String ingredient) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        values.put(KEY_INGREDIENTNAME, ingredient); // ingredient Name
     
        // Inserting Row
        db.insert(TABLE_INGREDIENTSATHAND, null, values);
        db.close(); // Closing database connection
        
    }
    
 // Adding many ingredients
    public void addListIngredient(List<String> ingredient) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        
        for(int i=0; i<ingredient.size(); i++){
        	
        	Log.d("DB_addIngredients inserting:", ingredient.get(i).toString());
        	values.put(KEY_INGREDIENTNAME, ingredient.get(i)); // ingredient Name	
        	
            // Inserting Row
            db.insert(TABLE_INGREDIENTSATHAND, null, values);
        	
        }
        
        
     
        //db.close(); // Closing database connection
        
    }
     
     
    // Getting All ingredients
    public List<String> getAllIngredients() {
    	
    	List<String> list= new ArrayList<String>();
    	String query = "SELECT  * FROM " + TABLE_INGREDIENTSATHAND;
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
        Cursor cursor = db.rawQuery(query, null);
        
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	list.add(cursor.getString(0));
            	Log.d("DB_getAll adding:", list.toString());
            } while (cursor.moveToNext());
        }
     
      //  db.close(); // Closing database connection
    	return list;
    }
     
    // Getting ingredients Count
    public int getIngredientCount() {
    	 	
        String query = "SELECT  * FROM " + TABLE_INGREDIENTSATHAND;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
    	
        db.close(); // Closing database connection
        
    	return cursor.getCount();
    	
    }
    
    // Updating single ingredient
    public int updateIngredient(String ingredient) {
    	
    	return 0;
    }
     
 // Deleting single ingredient
    public void deleteIngredient(String ingredient) {
    	
    	 SQLiteDatabase db = this.getWritableDatabase();
    	    db.delete(TABLE_INGREDIENTSATHAND, KEY_INGREDIENTNAME + " = ?", new String[] { ingredient });
    	    db.close();
    	
    }
    
 // Deleting All ingredient
    public void deleteAllIngredient() {
    	
    		SQLiteDatabase db = this.getReadableDatabase();
    		db.delete(TABLE_INGREDIENTSATHAND, null,null);
    	    db.close();
    	    Log.d("DB_delete all in :", TABLE_INGREDIENTSATHAND);
    	
    }
 // Deleting List ingredient
    public void deleteListIngredient(List<String> ingredient) {
    	   	
    	SQLiteDatabase db = this.getReadableDatabase();

    	for(int index=0; index<ingredient.size(); index++){
    		db.delete(TABLE_INGREDIENTSATHAND, KEY_INGREDIENTNAME+" = '"+ingredient.get(index).toString()+"'", null);
    	}
                	
         db.close(); // Closing database connection
         Log.d("DB_delete:", ingredient.toString());
         
    	
    }
    
    
    
}


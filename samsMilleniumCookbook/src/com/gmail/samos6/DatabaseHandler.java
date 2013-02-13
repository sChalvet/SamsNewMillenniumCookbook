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
    private static final String TABLE_USERINFORMATION = "userInformation";
 
    // ingredientsAtHand Table Column names
    private static final String KEY_INGREDIENTNAME = "ingredientName";

    // userInformation Table column names
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_JOINDATE = "joinDate";
    private static final String KEY_FIRSTNAME = "firstName";
    private static final String KEY_LASTNAME = "lastName";
    private static final String KEY_DOB = "dob";
    private static final String KEY_PASSWORD = "password";
 
    public DatabaseHandler(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INGREDIENTSATHAND_TABLE = "CREATE TABLE " + TABLE_INGREDIENTSATHAND + " ("
                + KEY_INGREDIENTNAME + " TEXT PRIMARY KEY)";
        
        String CREATE_USERINFORMATION_TABLE = "CREATE TABLE " + TABLE_USERINFORMATION + " ("
        		+ KEY_USERNAME + " TEXT PRIMARY KEY, "
        		+ KEY_EMAIL + " TEXT, "
        		+ KEY_JOINDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
        		+ KEY_FIRSTNAME + " TEXT, "
        		+ KEY_LASTNAME + " TEXT, "
        		+ KEY_DOB + " TEXT, "
        		+ KEY_PASSWORD + " TEXT )";
        
        Log.d("DB_onCreate db string=", CREATE_USERINFORMATION_TABLE);
        db.execSQL(CREATE_INGREDIENTSATHAND_TABLE);
        db.execSQL(CREATE_USERINFORMATION_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTSATHAND);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERINFORMATION);
 
    	Log.d("!!!!!!!!!!!!!!!!!!!!!!", "Tables have been droped");
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
        
        
     
        db.close(); // Closing database connection
        
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
     
        db.close(); // Closing database connection
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
    	    Log.d("DB_delete all in table:", TABLE_INGREDIENTSATHAND);
    	
    }
    
    
 // Deleting List of ingredients
    public void deleteListIngredient(List<String> ingredient) {
    	   	
    	SQLiteDatabase db = this.getReadableDatabase();

    	for(int index=0; index<ingredient.size(); index++){
    		db.delete(TABLE_INGREDIENTSATHAND, KEY_INGREDIENTNAME+" = '"+ingredient.get(index).toString()+"'", null);
    	}
                	
         db.close(); // Closing database connection
         Log.d("DB_delete:", ingredient.toString());	
    }
    
    /***************************************************************************************************************
     * 											Methods for Table UserInformation
     ***************************************************************************************************************/
    
    // Deleting All userInformation
    public void deleteUserInformation() {
    	
    		SQLiteDatabase db = this.getReadableDatabase();
    		db.delete(TABLE_USERINFORMATION, null,null);
    	    db.close();
    	    Log.d("DB_delete all in table:", TABLE_USERINFORMATION);
    	
    }
    
    // Adding many ingredients
    public void addUserInformation(String[] userInfo) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
 	
        Log.d("DB_addUserInformation receiving:", userInfo.toString());

        String sql = "INSERT INTO "+TABLE_USERINFORMATION+" ("+ KEY_USERNAME+", "+KEY_EMAIL+", "
        		+KEY_FIRSTNAME+", "+KEY_LASTNAME+", "+KEY_DOB+", "+KEY_PASSWORD+") "
        	    +"VALUES ('" + userInfo[0] + "', '" + userInfo[1] + "', '" +userInfo[2] + "', '" +userInfo[3] + "', '"
        	   + userInfo[4] + "', '" +userInfo[5] + "')";
        
        Log.d("DB_addUserInformation inserting:", sql);
        
        db.rawQuery(sql, null);
        db.close();
        
        
        
    }
    
    public void checkTables(){
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	 String CREATE_USERINFORMATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERINFORMATION + "  ("
         		+ KEY_USERNAME + " TEXT PRIMARY KEY, "
         		+ KEY_EMAIL + " TEXT, "
         		+ KEY_JOINDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
         		+ KEY_FIRSTNAME + " TEXT, "
         		+ KEY_LASTNAME + " TEXT, "
         		+ KEY_DOB + " TEXT, "
         		+ KEY_PASSWORD + " TEXT )";
         
         Log.d("DB_checktables db string=", CREATE_USERINFORMATION_TABLE);
         db.execSQL(CREATE_USERINFORMATION_TABLE);
         
         db.close();
    }
    
 // get username
    public String getUserName() {
    	
    	String query = "SELECT "+KEY_USERNAME+" FROM " + TABLE_USERINFORMATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        
    	Log.d("DB_getUserName returning:", cursor.toString());
    	
        db.close(); // Closing database connection
    
    	return cursor.toString();
    	    
    	
    }
    
 // get username
    public String[] getUserInfo(String userName) {
    	
    	String[] userInfo= new String[6];
    	//String query = "SELECT * FROM " + TABLE_USERINFORMATION +" WHERE "+ KEY_USERNAME +" = "+ "'"+userName+"'";
    	String query = "SELECT * FROM " + TABLE_USERINFORMATION ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        
        int index =0;
        if (cursor.moveToFirst()) {
            do {
            	userInfo[index]=cursor.getString(index);
            	index++;
            } while (cursor.moveToNext());
            Log.d("DB_getUserInfo returning:", userInfo[2]);
        }
        else{
        	Log.d("DB_getUserInfo nothing to return:", "nothing i tell you");
        }
        
    	
    	
    	cursor.close();
        db.close(); // Closing database connection
    
    	return userInfo;
    	    
    	
    }
    
}


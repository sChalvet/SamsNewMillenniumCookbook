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
    private static final int DATABASE_VERSION = 2;
 
    // Database Name
    private static final String DATABASE_NAME = "NewMillenniumDB";
 
    // recipeApp table name
    private static final String TABLE_INGREDIENTSATHAND = "ingredientsAtHand";
    private static final String TABLE_FAVORITERECIPES = "favoriteRecipes";
    private static final String TABLE_SAVEDRECIPES = "savedRecipes";
 
    // ingredientsAtHand Table Column name
    private static final String KEY_INGREDIENTNAME = "ingredientName";

    
    // favoriteRecipes and savedRecipes Table column name
    private static final String KEY_RECIPEId = "recipeId";

 
    public DatabaseHandler(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	String CREATE_INGREDIENTSATHAND_TABLE = "CREATE TABLE " + TABLE_INGREDIENTSATHAND + " ("
                + KEY_INGREDIENTNAME + " TEXT PRIMARY KEY)";
    	
    	String CREATE_FAVORITERECIPES_TABLE = "CREATE TABLE " + TABLE_FAVORITERECIPES + " ("
                + KEY_RECIPEId + " TEXT PRIMARY KEY)";
    	
    	String CREATE_SAVEDRECIPES_TABLE = "CREATE TABLE " + TABLE_SAVEDRECIPES + " ("
                + KEY_RECIPEId + " TEXT PRIMARY KEY)";
        
        
        Log.d("DB_onCreate db string=", CREATE_SAVEDRECIPES_TABLE);
        db.execSQL(CREATE_INGREDIENTSATHAND_TABLE);
        db.execSQL(CREATE_FAVORITERECIPES_TABLE);
        db.execSQL(CREATE_SAVEDRECIPES_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTSATHAND);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITERECIPES);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVEDRECIPES);
 
    	Log.d("!!!!!!!!!!!!!!!!!!!!!!", "Tables have been droped");
        // Create tables again
        onCreate(db);
    }
    
    /**
     * <b>name</b>: addIngredient
     * <br/><b>purpose</b>: adds a single ingredient to DB
     * <br/><b>returns</b>: void
     */
    public void addIngredient(String ingredient) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        values.put(KEY_INGREDIENTNAME, ingredient); // ingredient Name
     
        // Inserting Row
        db.insert(TABLE_INGREDIENTSATHAND, null, values);
        db.close(); // Closing database connection
        
    }
    
    /**
     * <b>name</b>: addListIngredient
     * <br/><b>purpose</b>: adds a List<String> of ingredients to DB
     * <br/><b>returns</b>: void
     */
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
     
     
    /**
     * <b>name</b>: getAllIngredients
     * <br/><b>purpose</b>: retrieves all of the Ingredients from DB
     * <br/><b>returns</b>: List<String>
     */
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
     
    /**
     * <b>name</b>: getIngredientCount
     *<br/> <b>purpose</b>: gets the number of ingredients in the DB
     * <br/><b>returns</b>: int
     */
    public int getIngredientCount() {
    	 	
        String query = "SELECT  * FROM " + TABLE_INGREDIENTSATHAND;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
    	
        db.close(); // Closing database connection
        
    	return cursor.getCount();
    	
    }
    
     
    /**
     * <b>name</b>: deleteIngredient
     * <br/><b>purpose</b>: deletes a single ingredient from DB
     * <br/><b>returns</b>: void
     */
    public void deleteIngredient(String ingredient) {
    	
    	 SQLiteDatabase db = this.getWritableDatabase();
    	    db.delete(TABLE_INGREDIENTSATHAND, KEY_INGREDIENTNAME + " = ?", new String[] { ingredient });
    	    db.close();
    	
    }
    
    /**
     * <b>name</b>: hasIngredient
     * <br/><b>purpose</b>: checks to see if this ingredient is in the DB
     * <br/><b>returns</b>: true if it is, false if it is not
     */
    public boolean hasIngredient(String ingredient) {
    	
    	String query = "SELECT  '"+ingredient+"' FROM " + TABLE_INGREDIENTSATHAND;
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
        Cursor cursor = db.rawQuery(query, null);
        
        
        // if it contains the ingredient
        if (cursor.moveToFirst()) {
        	db.close(); // Closing database connection
        	return true;
        }else{
        	db.close(); // Closing database connection
        	return false;
        }

    	
    }
    
    /**
     * <b>name</b>: deleteAllIngredient
     * <br/><b>purpose</b>: deletes all ingredients from DB
     * <br/><b>returns</b>: void
     */
    public void deleteAllIngredient() {
    	
    		SQLiteDatabase db = this.getReadableDatabase();
    		db.delete(TABLE_INGREDIENTSATHAND, null,null);
    	    db.close();
    	    Log.d("DB_delete all in table:", TABLE_INGREDIENTSATHAND);
    	
    }
    
    
    /**
     * <b>name</b>: deleteListIngredient
     * <br/><b>purpose</b>: deletes a List<String> of ingredients from DB
     * <br/><b>returns</b>: void
     */
    public void deleteListIngredient(List<String> ingredient) {
    	   	
    	SQLiteDatabase db = this.getReadableDatabase();

    	for(int index=0; index<ingredient.size(); index++){
    		db.delete(TABLE_INGREDIENTSATHAND, KEY_INGREDIENTNAME+" = '"+ingredient.get(index).toString()+"'", null);
    	}
                	
         db.close(); // Closing database connection
         Log.d("DB_delete:", ingredient.toString());	
    }

    /***************************************************************************************************************
     * 											Methods for Table favoriteRecipes
     ***************************************************************************************************************/
    
    /**
     * <b>name</b>: addFavoriteRecipe
     * <br/><b>purpose</b>: adds a new Recipe to Favorites
     * <br/><b>returns</b>: void
     */
    public void addFavoriteRecipe(String recipeName) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        values.put(KEY_RECIPEId, recipeName); 
     
        // Inserting Row
        db.insert(TABLE_FAVORITERECIPES, null, values);
        db.close(); // Closing database connection
        
    }
    
    /**
     * <b>name</b>: getAllFavRecipes
     * <br/><b>purpose</b>: retrieves all of the users favorite recipes
     * <br/><b>returns</b>: List<String> recipes
     */
    public List<String> getAllFavRecipes() {
    	
    	List<String> list= new ArrayList<String>();
    	String query = "SELECT  * FROM " + TABLE_FAVORITERECIPES;
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
        Cursor cursor = db.rawQuery(query, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	list.add(cursor.getString(0));
            	Log.d("DB_getAllFavRecipes adding:", list.toString());
            } while (cursor.moveToNext());
        }
     
        db.close(); // Closing database connection
    	return list;
    }
    
    /**
     * <b>name</b>: deleteListFavRecipes
     * <br/><b>purpose</b>: to delete a list of recipes that the user no longer wishes to remember in his favoriteRecipe DB
     * <br/><b>returns</b>: Void
     */
    public void deleteListFavRecipes(List<String> recipeName) {
    	   	
    	SQLiteDatabase db = this.getReadableDatabase();

    	for(int index=0; index<recipeName.size(); index++){
    		db.delete(TABLE_FAVORITERECIPES, KEY_RECIPEId+" = '"+recipeName.get(index).toString()+"'", null);
    	}
                	
         db.close(); // Closing database connection
         Log.d("DB_delete:", recipeName.toString());	
    }
    
    /**
     * <b>name</b>: deleteAllFavRecipes
     * <br/><b>purpose</b>: to delete all of the favorite recipes
     * <br/><b>returns</b>: Void
     */
    public void deleteAllFavRecipes() {
    	
    		SQLiteDatabase db = this.getReadableDatabase();
    		db.delete(TABLE_FAVORITERECIPES, null,null);
    	    db.close();
    	    Log.d("DB_delete all in table:", TABLE_FAVORITERECIPES);
    	
    }

    /***************************************************************************************************************
     * 											Methods for Table savedRecipes
     ***************************************************************************************************************/
    
    /**
     * <b>name</b>: addSavedRecipe
     * <br/><b>purpose</b>: adds a new Recipe to Saved recipes
     * <br/><b>returns</b>: void
     */
    public void addSavedRecipe(String recipeId) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();
        values.put(KEY_RECIPEId, recipeId); 
     
        // Inserting Row
        db.insert(TABLE_SAVEDRECIPES, null, values);
        db.close(); // Closing database connection
        
    }
    
    /**
     * <b>name</b>: getAllSavedRecipes
     * <br/><b>purpose</b>: retrieves all of the users saved recipes
     * <br/><b>returns</b>: List<String> recipes
     */
    public List<String> getAllSavedRecipes() {
    	
    	List<String> list= new ArrayList<String>();
    	String query = "SELECT  * FROM " + TABLE_SAVEDRECIPES;
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
        Cursor cursor = db.rawQuery(query, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	list.add(cursor.getString(0));
            	Log.d("DB_getAllSavedRecipes adding:", list.toString());
            } while (cursor.moveToNext());
        }
     
        db.close(); // Closing database connection
    	return list;
    }
    
    /**
     * <b>name</b>: deleteListSavedRecipes
     * <br/><b>purpose</b>: to delete a list of recipes that the user no longer wishes to remember in his savedRecipe DB
     * <br/><b>returns</b>: Void
     */
    public void deleteListSavedRecipes(List<String> recipeName) {
    	   	
    	SQLiteDatabase db = this.getReadableDatabase();

    	for(int index=0; index<recipeName.size(); index++){
    		db.delete(TABLE_SAVEDRECIPES, KEY_RECIPEId+" = '"+recipeName.get(index).toString()+"'", null);
    	}
                	
         db.close(); // Closing database connection
         Log.d("DB_delete:", recipeName.toString());	
    }
    
    /**
     * <b>name</b>: deleteAllSavedRecipes
     * <br/><b>purpose</b>: to delete all of the Saved recipes
     * <br/><b>returns</b>: Void
     */
    public void deleteAllSavedRecipes() {
    	
    		SQLiteDatabase db = this.getReadableDatabase();
    		db.delete(TABLE_SAVEDRECIPES, null,null);
    	    db.close();
    	    Log.d("DB_delete all in table:", TABLE_SAVEDRECIPES);
    	
    }
    
    
    
    /***************************************************************************************************************
     * 											Methods for Table UserInformation
     ***************************************************************************************************************/
  
/*    
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
*/
    
}


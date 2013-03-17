package com.gmail.samos6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ListFavoriteAdapter extends BaseAdapter {
	
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_RATING = "rating";
	private static final String TAG_NUMRATINGS = "numRatings";
	private static final String TAG_TOTALTIME = "totalTime";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_IMAGEURL = "imageUrl";
	private static final String TAG_RECIPEID = "recipeId";
	
	
	private LayoutInflater mInflater;
	public ArrayList<HashMap<String, String>> data;
	ViewHolder viewHolder;
	public static HashMap<Integer,String> myList=new HashMap<Integer,String>();
    public Images_ImageLoader imageLoader; 

	

public ListFavoriteAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
}
/**
 * ListFavoriteAdapter is used to remember checked boxes
 */
public ListFavoriteAdapter(Activity activity, ArrayList<HashMap<String, String>> productsList) {
    super();
    
    imageLoader=new Images_ImageLoader(activity.getApplicationContext());
    
    mInflater = activity.getLayoutInflater();
    this.data = productsList;

    Log.d("ListFavAdapter_data", data.toString());
}

public void clear() {
    this.data.clear();
    viewHolder = null;
}

public void setData(ArrayList<HashMap<String, String>> data) {
    this.data = data;
}

public int getCount() {
    return data.size();
}

public Object getItem(int item) {
    return data.get(item);
}

/**
 * gets all of the checked items in the list
 * @return
 */
public List<String> getChecked() {
	
	List<String> list= new ArrayList<String>();

	for(int index=0; index<data.size(); index++){
		if(data.get(index).get("reminder") == "True" )
			list.add(data.get(index).get(TAG_RECIPEID));		
		}		
		
	Log.d("FavoriteAdapter_getChecked= ", list.toString() );
    return list;
}

public long getItemId(int position) {
    return position;
}

public View getView(final int position, View convertView, ViewGroup parent) {
	

    if (convertView == null) {
    	convertView = mInflater.inflate(R.layout.list_fav_recipes, null);
    		
        viewHolder = new ViewHolder();
        
        viewHolder.recipeName = (TextView) convertView.findViewById(R.id.txtListFavRecipeRecipeName);
        viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.chkboxListFavRecipeCheckBox);
        viewHolder.author = (TextView) convertView.findViewById(R.id.txtListFavRecipeAuthor);
        viewHolder.summery = (TextView) convertView.findViewById(R.id.txtListFavRecipeSummery);
        viewHolder.numRatings = (TextView) convertView.findViewById(R.id.txtListFavRecipeNumReviews);
        viewHolder.totalTime = (TextView) convertView.findViewById(R.id.txtListFavRecipeTotalCookTime);
        viewHolder.rtbRating = (RatingBar)convertView.findViewById(R.id.listFavRecipesRatingBar);
        viewHolder.recipeImage =(ImageView)convertView.findViewById(R.id.listFavRecipeImage);
        convertView.setTag(viewHolder);

    } else {
        viewHolder = (ViewHolder) convertView.getTag();
    }

   
    String recipeName = data.get(position).get(TAG_RECIPENAME);
    String checkBox = data.get(position).get("reminder");
    String author = data.get(position).get(TAG_AUTHOR);
    String summery = data.get(position).get(TAG_SUMMERY);
    String numRatings = data.get(position).get(TAG_NUMRATINGS);
    String rating = data.get(position).get(TAG_RATING);
    String imageUrl = data.get(position).get(TAG_IMAGEURL);
    String recipeId = data.get(position).get(TAG_RECIPEID);
     Log.d("Adapter loading recipe: ", recipeName+" at pos:"+Integer.toString(position));
    
     //calculating minutes and hours
     int t=Integer.parseInt(data.get(position).get(TAG_TOTALTIME));
     int hours = t / 60; 
     int minutes = t % 60;
     String time="";
     	if(hours==0)
     		time= minutes+" min";
     	else
     		time= hours+" h, "+minutes+" min";
 
     viewHolder.recipeId= recipeId;
     viewHolder.recipeName.setText(recipeName);
     viewHolder.author.setText(author);
     viewHolder.numRatings.setText(numRatings);
     viewHolder.totalTime.setText(time);
     viewHolder.summery.setText(summery);
     viewHolder.recipeImage =(ImageView)convertView.findViewById(R.id.listFavRecipeImage);
     viewHolder.rtbRating.setRating(Float.valueOf(rating)/2);
     imageLoader.DisplayImage(imageUrl, viewHolder.recipeImage);
    
    try {
    if (checkBox.equalsIgnoreCase("true")) {
        viewHolder.checkBox.setChecked(true);
    } else {
        viewHolder.checkBox.setChecked(false);
    }
    } catch (Exception e) {
        viewHolder.checkBox.setChecked(false);
    }
    viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
        	Log.d("ListFavRecipesAdapter: ", "in onClick");
            if (((CheckBox) v).isChecked()) {
                data.get(position).put("reminder", "True");
            } else {
            	
                data.get(position).put("reminder", "False");
            }
            
        }
    });
    

    Log.d("ListFavRecipesAdapter: ", "return convertView");
    return convertView;
}

static class ViewHolder {

	CheckBox checkBox;
	ImageView recipeImage;
	RatingBar rtbRating;
	TextView recipeName;
	TextView summery;
	TextView numRatings;
	TextView author;
	TextView totalTime;
	String recipeId;
	}
}


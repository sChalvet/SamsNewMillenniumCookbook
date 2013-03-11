package com.gmail.samos6;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;



public class RecipeLazyAdapter extends BaseAdapter {
	
	private static final String TAG_RECIPENAME = "recipeName";
	private static final String TAG_SUMMERY = "summery";
	private static final String TAG_RATING = "rating";
	private static final String TAG_NUMRATINGS = "numRatings";
	private static final String TAG_TOTALTIME = "totalTime";
	private static final String TAG_AUTHOR = "author";
	private static final String TAG_IMAGEURL = "imageUrl";

    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public Images_ImageLoader imageLoader; 
    
    public RecipeLazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new Images_ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }
    
    public void clear() {
        this.data.clear();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_recipes, null);

        TextView txtRecipeName = (TextView)vi.findViewById(R.id.txtListRecipeRecipeName);
        TextView txtSummery = (TextView)vi.findViewById(R.id.txtListRecipeSummery);
        RatingBar rtbRating = (RatingBar)vi.findViewById(R.id.ratingBarListRecipe);
        TextView txtNumReviews = (TextView)vi.findViewById(R.id.txtListRecipeNumReviews);
        TextView txtAuthor = (TextView)vi.findViewById(R.id.txtListRecipeAuthor);
        TextView txtTotalCookTime = (TextView)vi.findViewById(R.id.txtListRecipeTotalCookTime);
        ImageView recipeImage=(ImageView)vi.findViewById(R.id.recipeImage);
        
        
        // Setting all values in listview
        txtRecipeName.setText(data.get(position).get(TAG_RECIPENAME));
        txtSummery.setText(data.get(position).get(TAG_SUMMERY));
        txtNumReviews.setText(data.get(position).get(TAG_NUMRATINGS));
        txtAuthor.setText(data.get(position).get(TAG_AUTHOR));
        txtTotalCookTime.setText(data.get(position).get(TAG_TOTALTIME));
        rtbRating.setRating(Float.valueOf(data.get(position).get(TAG_RATING)));
        imageLoader.DisplayImage(data.get(position).get(TAG_IMAGEURL), recipeImage);
        
        return vi;
    }
}
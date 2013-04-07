package com.gmail.samos6.samscookbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

public class ListCommentsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public ArrayList<HashMap<String, String>> data;
	ViewHolder viewHolder;
	public static HashMap<Integer,String> myList=new HashMap<Integer,String>();
	
	String origin;
	
	public List<String> pantry = new ArrayList<String>();
	

public ListCommentsAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
}

static class ViewHolder {

	RatingBar ratingBar;
	TextView comment;
	TextView author;
	TextView date;
	
	}

/*
 * ListCommentsAdapter is used to personalize the comments list
 */
public ListCommentsAdapter(Activity activity, ArrayList<HashMap<String, String>> commentList) {
    super();
    mInflater = activity.getLayoutInflater();
    //this.activity = activity;
    this.data = commentList;
    //log.d("CommentAdapter_data", data.toString());
}

public void clear() {
    this.data.clear();
    viewHolder = null;
}

public void setData(ArrayList<HashMap<String, String>> data) {
    this.data = data;
}

@Override
public int getCount() {
    return data.size();
}

@Override
public Object getItem(int item) {
    return data.get(item);
}


@Override
public long getItemId(int position) {
    return position;
}

public String getAuthor(int position){
	
	return data.get(position).get("author");
}

public boolean hasAlredyPosted(String author){
	
	for(int i=0; i<data.size();i++){
		if(author.equalsIgnoreCase(data.get(i).get("author")))
			return true;
	}
	
	
	return false;
}


@Override
public View getView(final int position, View convertView, ViewGroup parent) {
	

    if (convertView == null) {
    	convertView = mInflater.inflate(R.layout.list_recipe_comments, null);
    		
        viewHolder = new ViewHolder();

        viewHolder.author = (TextView) convertView.findViewById(R.id.txtListRecipeCommentsAuthorName);
        viewHolder.date = (TextView) convertView.findViewById(R.id.txtListRecipeCommentsPostDate);
        viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.listRecipeCommentsRatingBar);
        viewHolder.comment = (TextView) convertView.findViewById(R.id.txtListRecipeCommentsComment);
        
        

        convertView.setTag(viewHolder);

    } else {
        viewHolder = (ViewHolder) convertView.getTag();
    }

	
    String author = data.get(position).get("author");
    String date = data.get(position).get("postTime");
    String comment = data.get(position).get("comment");
    String rating = data.get(position).get("rating");
    
    
    viewHolder.author.setText(author);
    viewHolder.date.setText(date);
    viewHolder.comment.setText(comment);
    viewHolder.ratingBar.setRating(Float.valueOf(rating)/2);
    
    

    //log.d("SamsListAdapter: ", "return convertView");
    return convertView;
}

}


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
import android.widget.TextView;
import android.widget.Toast;

public class ListFavoriteAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public ArrayList<HashMap<String, String>> data;
	ViewHolder viewHolder;
	public static HashMap<Integer,String> myList=new HashMap<Integer,String>();

	

public ListFavoriteAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
}
/**
 * ListFavoriteAdapter is used to remember checked boxes
 */
public ListFavoriteAdapter(Activity activity, ArrayList<HashMap<String, String>> productsList) {
    super();
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
			list.add(data.get(index).get("recipeName"));		
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

        convertView.setTag(viewHolder);

    } else {
        viewHolder = (ViewHolder) convertView.getTag();
    }

   
    String recipeName = data.get(position).get("recipeName");
    String checkBox = data.get(position).get("reminder");
    String author = data.get(position).get("author");
    String summery = data.get(position).get("summery");
    String numRatings = data.get(position).get("numRatings");
    String totalTime = data.get(position).get("totalTime");
     Log.d("Adapter loading recipe: ", recipeName+" at pos:"+Integer.toString(position));
    
 
     viewHolder.recipeName.setText(recipeName);
     viewHolder.author.setText(author);
     viewHolder.numRatings.setText(numRatings);
     viewHolder.totalTime.setText(totalTime);
     viewHolder.summery.setText(summery);
    
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
	TextView recipeName;
	TextView summery;
	TextView numRatings;
	TextView author;
	TextView totalTime;
	}
}


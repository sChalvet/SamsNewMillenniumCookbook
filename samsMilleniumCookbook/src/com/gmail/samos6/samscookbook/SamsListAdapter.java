package com.gmail.samos6.samscookbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SamsListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public ArrayList<HashMap<String, String>> data;
	ViewHolder viewHolder;
	public static HashMap<Integer,String> myList=new HashMap<Integer,String>();
	
	//used to set font
	Typeface typeFace; 
	
	String origin;
	
	public List<String> pantry = new ArrayList<String>();
	

public SamsListAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
}
/**
 * SamsListAdapter is used to personalize the screen output and calculation
 * List<String> pantry ---> holds a record of what is in the pantry, it is not really used if this class is called by the pantry it self
 * String origine      ---> holds the name of the originator of the call because the output is treated slightly differently
 */
public SamsListAdapter(Activity activity, ArrayList<HashMap<String, String>> productsList, List<String> pantry, String origin, Typeface t) {
    super();
    mInflater = activity.getLayoutInflater();
    //this.activity = activity;
    this.data = productsList;
    this.pantry = pantry;
    this.origin = origin;
    //log.d("Adapter_pantry =", this.pantry.toString());
    typeFace=t;
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

public int getPositionOf(String letter){
	int position=-1;
	String name=null;
	for(int index=0; index<data.size(); index++){
		name=data.get(index).get("ingredientName");
		
		if(name.startsWith(letter)){
			position=index;
			return position;
		}
	}

	return position;
}

public List<String> getChecked() {
	
	List<String> s= new ArrayList<String>();

	//if call comes from the right class then return the checked ingredients
	//and return what is already in the pantry
	if(origin.equalsIgnoreCase("IngredientList") || origin.equalsIgnoreCase("getIngredient")){	
		for(int index=0; index<data.size(); index++){
			if(data.get(index).get("reminder") == "True" || pantry.contains(data.get(index).get("ingredientName")))
				s.add(data.get(index).get("ingredientName"));
			
		}
	}
	else{
		for(int index=0; index<data.size(); index++){
			if(data.get(index).get("reminder") == "True")
				s.add(data.get(index).get("ingredientName"));
			
		}
		
	}
		
		
		
	//log.d("Adapter_getChecked= ", s.toString() );
    return s;
}

@Override
public long getItemId(int position) {
    return position;
}

public boolean toogleSelection(int index, View view) {
	
	//log.d("SamsAdapter_toogleSelection", "inside");

	if(data.get(index).get("reminder") == "True")
		data.get(index).put("reminder", "False");		
	else
		data.get(index).put("reminder", "True");
		
	return true;
}

/**
 * Selects all unselected views.
 * if every view is already selected
 * then it deselects all 
 * 
 */
public void selectAll() {
	
	//log.d("SamsAdapter_selectAll", "inside");
	
	if(data.size()== numSelected()){ //deselects all
		for(int index=0; index<data.size(); index++){
			data.get(index).put("reminder", "False");		
		}
	}else{	//selects all
		for(int index=0; index<data.size(); index++){
			if(data.get(index).get("reminder") != "True")
				data.get(index).put("reminder", "True");		
		}
	}
		
}

/**
 * gets the number of selected views
 * @return int numSelected
 */
public int numSelected(){
	
	int numSelected=0;
	
	if(origin.equalsIgnoreCase("IngredientList") || origin.equalsIgnoreCase("getIngredient")){	
		for(int index=0; index<data.size(); index++){
			if(data.get(index).get("reminder") == "True" || pantry.contains(data.get(index).get("ingredientName")))
				numSelected++;
			
		}
	}
	else{
		for(int index=0; index<data.size(); index++){
			if(data.get(index).get("reminder") == "True")
				numSelected++;
			
		}
		
	}
	
	return numSelected;
}

@Override
public View getView(final int position, View convertView, ViewGroup parent) {
	

    if (convertView == null) {
    	convertView = mInflater.inflate(R.layout.list_ingredients, null);
    		
        viewHolder = new ViewHolder();

        viewHolder.ingredientName = (TextView) convertView.findViewById(R.id.ingredientName);
        viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.ingredientCheckBox);
        
        viewHolder.ingredientName.setTypeface(typeFace);
        

        convertView.setTag(viewHolder);

    } else {
        viewHolder = (ViewHolder) convertView.getTag();
    }

   
    String ingredient = data.get(position).get("ingredientName");
    String checkBox = data.get(position).get("reminder");
     //log.d("Adapter loading ingredient: ", ingredient+" at pos:"+Integer.toString(position));
    
    
     //if 'pantry' contains data already then set a check mark on every ingredient that is listed.
     if(origin.equalsIgnoreCase("IngredientList") || origin.equalsIgnoreCase("getIngredient")){
	    //checks to see if ingredient is already in pantry
	    for(int index=0; index<pantry.size(); index++){
	    	
	    	if(ingredient.equalsIgnoreCase(pantry.get(index).toString()) ){
	    		//log.d("Adapter Pantry adding: ", ingredient);
	    		checkBox="True";
	    	}		
	    }
    }
 
    viewHolder.ingredientName.setText(ingredient);
    
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

        @Override
		public void onClick(View v) {
        	//log.d("SamsListAdapter: ", "in onClick");
            if (((CheckBox) v).isChecked()) {
                data.get(position).put("reminder", "True");
            } else {
            	
            	pantry.remove(data.get(position).get("ingredientName"));	// remove from the list of ingredients that one already has
	
                data.get(position).put("reminder", "False");
            }
            
            
            //log.d("Adapter_onClick= ", pantry.toString() );
        }
    });

    //log.d("SamsListAdapter: ", "return convertView");
    return convertView;
}

static class ViewHolder {

	CheckBox checkBox;
	TextView ingredientName;
	}
}


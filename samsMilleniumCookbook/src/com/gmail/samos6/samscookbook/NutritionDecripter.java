package com.gmail.samos6.samscookbook;

import java.util.HashMap;
import java.util.Map;

public class NutritionDecripter {
	private Map<String, Float> info;
	float calories;
	float fat;
	float carbs;
	float protein;
	
	private static final String TAG_PROTEIN = "protein";
	private static final String TAG_CALORIES = "calories";
	private static final String TAG_CARBS = "carbs";
	private static final String TAG_FAT = "fat";


	public NutritionDecripter() {
		super();
	}
	
	public Map<String, Float> getIngredientFacts(String measurement, String type, String amount, String calories, String fat, String carbs, String protein){
		
		float fAmount= amountToFloat(amount);
		
		return getIngredientFacts(measurement, type, fAmount, calories, fat, carbs, protein);
	}
	
	public Map<String, Float> getIngredientFacts(String measurement, String type, float amount, String cal, String fatt, String carb, String prot){
		
		float cup=-1.0f;
		float weight= -1.0f;
		this.calories = Float.parseFloat(cal);
		this.carbs = Float.parseFloat(carb);
		this.fat = Float.parseFloat(fatt);
		this.protein = Float.parseFloat(prot);
		
		//log.d("nutrition decripter", "type: "+type+", meas: "+measurement+", amount:"+amount+", cal:"+calories+", fat:"+fat+", prot"+protein+", carbs:"+carbs);
		
		cup = getWeightPerCup(type);
		weight= getWeight(cup, measurement);
		
		info = new HashMap<String, Float>(); 
		info.put(TAG_CALORIES, Math.round(amount* weight*(calories/100)*10.0f)/10.0f);
		info.put(TAG_FAT, Math.round(amount* weight*(fat/100)*10.0f)/10.0f);
		info.put(TAG_CARBS, Math.round(amount* weight*(carbs/100)*10.0f)/10.0f);
		info.put(TAG_PROTEIN, Math.round(amount* weight*(protein/100)*10.0f)/10.0f);
		
		//Log.d("nutrition decripter", info.get("carbs").toString());
		
		return info;
	}
	
	public float getWeight(float cup, String measurement){
		
		double weight=-1;
		
		if(measurement.equalsIgnoreCase("cup")){
			weight= cup;
		}else if(measurement.equalsIgnoreCase("dash")){
			weight= cup/384;
		}else if(measurement.equalsIgnoreCase("drop")){
			weight= cup/3840;
		}else if(measurement.equalsIgnoreCase("gallon")){
			weight= cup*16;
		}else if(measurement.equalsIgnoreCase("gram")){
			weight= 1.0;
		}else if(measurement.equalsIgnoreCase("kilo")){
			weight= 1000;
		}else if(measurement.equalsIgnoreCase("liter")){
			weight= cup*4.07;
		}else if(measurement.equalsIgnoreCase("mlLiter")){
			weight= cup/236;
		}else if(measurement.equalsIgnoreCase("ounce")){
			weight= 29.0;
		}else if(measurement.equalsIgnoreCase("pinch")){
			weight= cup/768;
		}else if(measurement.equalsIgnoreCase("pint")){
			weight= cup*2;
		}else if(measurement.equalsIgnoreCase("pound")){
			weight= 454.0;
		}else if(measurement.equalsIgnoreCase("quart")){
			weight= cup*4;
		}else if(measurement.equalsIgnoreCase("shot")){
			weight= cup/5.3;
		}else if(measurement.equalsIgnoreCase("teaspoon")){
			weight= cup/48;
		}else if(measurement.equalsIgnoreCase("tablespoon")){
			weight= cup/16;
		}else if(measurement.equalsIgnoreCase("no unit")){
			weight= -1;
		}
		
		return (float) weight;
	}
	
	public float getWeightPerCup(String type){
		
		float cup=0.0f;
		
		if(type.equalsIgnoreCase("Beef") 
				|| type.equalsIgnoreCase("Lamb") 
				|| type.equalsIgnoreCase("Pork") 
				|| type.equalsIgnoreCase("Rabbit")
				|| type.equalsIgnoreCase("Poultry")
				|| type.equalsIgnoreCase("Seafood")
				|| type.equalsIgnoreCase("Soy")){
			cup = 170.0f;	
			
		}else if(type.equalsIgnoreCase("Dairy") 
				|| type.equalsIgnoreCase("Egg")
				|| type.equalsIgnoreCase("Oil")
				|| type.equalsIgnoreCase("Other")){
			cup = 228.0f;
			
		}else if(type.equalsIgnoreCase("Fruit")
				|| type.equalsIgnoreCase("Mushroom")
				|| type.equalsIgnoreCase("Vegetables")){
			cup = 113.0f;
			
		}else if(type.equalsIgnoreCase("Candy")){
			cup = 208.0f;	
			
		}else if(type.equalsIgnoreCase("Cereal") 
				|| type.equalsIgnoreCase("Nuts")
				|| type.equalsIgnoreCase("Pasta")){
			cup = 228.0f;
		}else if (type.equalsIgnoreCase("Spice/Herbs/Condiments")){
			cup = 70.0f;
		}else{
			cup = 1.0f;
		}
		
		return cup;
	}
	
	public String amountToString(float amount){
		
		//log.d("amountToString begining", "amount= "+amount);
		String sAmount = null;
		String sDecimal = "";
			
		int wholeValue = (int) amount;
		float decimalValue = amount - wholeValue;
		
		//log.d("amountToString", "wholeValue="+Integer.toString(wholeValue)+", decimalValue="+Float.toString(decimalValue)
		//			+", original="+amount);
		
		if(wholeValue==0 || decimalValue>.09){
		
			if(decimalValue<=.18){
				sDecimal="1/8 ";
			}else if(decimalValue<=.29){
				sDecimal="1/4 ";
			}else if(decimalValue<=.40){
				sDecimal="1/3 ";
			}else if(decimalValue<=.60){
				sDecimal="1/2 ";
			}else if(decimalValue<=.70){
				sDecimal="2/3 ";
			}else if(decimalValue<=.90){
				sDecimal="3/4 ";
			}else{
				wholeValue++;
			}
		}
		
		sAmount= wholeValue==0 ? sDecimal : Integer.toString(wholeValue)+" "+sDecimal;
		
		//log.d("amountToString", Float.toString(amount)+"f= "+sAmount);
		
		return sAmount;
	}
	
	public float amountToFloat(String amount){
		
		Float fAmount = 0.0f;
		String[] array= amount.split(" ");
		
		if(array[0].contains("/")){
			fAmount = fractionToFloat(array[0]);
		}else if(array.length==2){
			fAmount = Float.valueOf(array[0]) + fractionToFloat(array[1]);
		}else{
			fAmount = Float.valueOf(array[0]);
		}
		
		//log.d("amountToFloat", amount+"= "+Float.toString(fAmount));
		
		return fAmount;
	}
	
	public float fractionToFloat(String frac){
		
		float dec=0.0f;
		
		if(frac.equalsIgnoreCase("1/8")){
			dec=.13f;
		}else if(frac.equalsIgnoreCase("1/4")){
			dec=.25f;
		}else if(frac.equalsIgnoreCase("1/3")){
			dec=.33f;
		}else if(frac.equalsIgnoreCase("1/2")){
			dec=.50f;
		}else if(frac.equalsIgnoreCase("2/3")){
			dec=.66f;
		}else if(frac.equalsIgnoreCase("3/4")){
			dec=.75f;
		}
		
		return dec;
	}

}

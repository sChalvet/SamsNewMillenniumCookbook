public class memberlistadapter extends BaseAdapter {
private LayoutInflater mInflater;
public ArrayList<Hashtable<String, String>> data;
ViewHolder viewHolder;
public static HashMap<Integer,String> myList=new HashMap<Integer,String>();

public memberlistadapter(Context context) {
    mInflater = LayoutInflater.from(context);
}

public memberlistadapter(Activity activity, ArrayList<Hashtable<String, String>> objects) {
    super();
    mInflater = activity.getLayoutInflater();
    //this.activity = activity;
    this.data = objects;
    Log.d("data", data.toString());
}

public void clear() {
    this.data.clear();
    viewHolder = null;
}

public void setData(ArrayList<Hashtable<String, String>> data) {
    this.data = data;
}

public int getCount() {
    return data.size();
}

public Object getItem(int item) {
    return data.get(item);
}

public long getItemId(int position) {
    return position;
}

public View getView(final int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
        convertView = mInflater.inflate(R.layout.member_amount_child, null);
        viewHolder = new ViewHolder();

        viewHolder.nameText = (TextView) convertView.findViewById(R.id.grp_amt_child);
        viewHolder.amountText = (EditText) convertView.findViewById(R.id.groupAmount);
        viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.memberCheckNotif);

        convertView.setTag(viewHolder);

    } else {
        viewHolder = (ViewHolder) convertView.getTag();
    }
    //Log.d("data at "+position, data.get(position).toString());
    String amt = data.get(position).get("dueAmount");
    String name = data.get(position).get("name");
    String check = data.get(position).get("reminder");

    viewHolder.nameText.setText(name);
    try {
    if (amt.length() > 0 && !amt.equalsIgnoreCase("0")) {
        viewHolder.amountText.setText(amt);
    } else {
        viewHolder.amountText.setText("");
    }
    } catch (Exception e) {
        viewHolder.amountText.setText("");
    }

    viewHolder.amountText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                EditText et = (EditText) v.findViewById(R.id.groupAmount);
                data.get(position).put("dueAmount", et.getText().toString().trim());
            }
        }
    });

    try {
    if (check.equalsIgnoreCase("true")) {
        viewHolder.checkBox.setChecked(true);
    } else {
        viewHolder.checkBox.setChecked(false);
    }
    } catch (Exception e) {
        viewHolder.checkBox.setChecked(false);
    }
    viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
            if (((CheckBox) v).isChecked()) {
                data.get(position).put("reminder", "True");
            } else {
                data.get(position).put("reminder", "False");
            }
        }
    });
    if (position == 0) {
        viewHolder.checkBox.setVisibility(View.GONE);
        viewHolder.amountText.setVisibility(View.GONE);
        viewHolder.nameText.setVisibility(View.GONE);
    } else {
        viewHolder.checkBox.setVisibility(View.VISIBLE);
        viewHolder.amountText.setVisibility(View.VISIBLE);
        viewHolder.nameText.setVisibility(View.VISIBLE);
    }

    return convertView;
}

static class ViewHolder {
    TextView nameText;
    EditText amountText;
    CheckBox checkBox;
}}

/*
 <!--
	android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17sp"
        android:textStyle="bold" />  

    
    <TextView
        android:id="@+id/protein"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17sp"
        android:textStyle="bold" />
        
               <TextView
        android:id="@+id/calories"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17sp"
        android:textStyle="bold" />
    
    <TextView
        android:id="@+id/fat"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17sp"
        android:textStyle="bold" />
    
    <TextView
        android:id="@+id/carbs"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17sp"
        android:textStyle="bold" />
    
    
    <TextView
        android:id="@+id/notes"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17dip"
        android:textStyle="bold" />		
        
     <TextView
        android:id="@+id/addedBy"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17dip"
        android:textStyle="bold" />	

    <TextView
        android:id="@+id/type"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingLeft="6dip"
        android:paddingTop="6dip"
        android:textSize="17dip"
        android:textStyle="bold" />	
    

    <TextView
        android:id="@+id/dateCreated"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17dip"
        android:textStyle="bold" />
    
    <TextView
        android:id="@+id/dateUpdated"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:paddingTop="6dip"
        android:paddingLeft="6dip"
        android:textSize="17dip"
        android:textStyle="bold" />

    -->
	
	
	public class Activity extends Activity implements View.OnClickListener
{
    private Spinner spinner0, spinner1, spinner2, spinner3;
    private Button submit, cancel;

    private String country[], state[], city[], area[];

    Australia aus = new Australia();

    Object object;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        spinner0 = (Spinner)findViewById(R.id.spinnerCountry);
        spinner1 = (Spinner)findViewById(R.id.spinnerQ1);
        spinner2 = (Spinner)findViewById(R.id.spinnerQ2);
        spinner3 = (Spinner)findViewById(R.id.spinnerQ3);

        submit = (Button)findViewById(R.id.btnSubmit);
        cancel = (Button)findViewById(R.id.btnCancel);

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);

        country = new String[] {"Select Country", "Australia", "USA", "UK", "New Zealand", "EU", "Europe", "China", "Hong Kong",
                                "India", "Malaysia", "Canada", "International", "Asia", "Africa"};


        ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(Activity.this, android.R.layout.simple_spinner_item, country);
        adapter0.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner0.setAdapter(adapter0);
        Log.i("AAA","spinner0");

        spinner0.setOnItemSelectedListener(new OnItemSelectedListener()
        {           
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view1, int pos, long id)
            {
                Log.i("AAA","OnItemSelected");
                int loc;
                loc = pos;

                switch (loc)
                {
                case 1:

                    state = aus.getState();
                    object = aus;

                    Log.i("AAA","ArrayAdapter1");                   
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Activity.this, android.R.layout.simple_spinner_item, state);
                    adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spinner1.setAdapter(adapter1);          Log.i("AAA","spinner1");
                    break;

                default:
                    Log.i("AAA","default 0");
                    break;
                }                               
            }       

            @Override
            public void onNothingSelected(AdapterView<?> arg1)
            {
                Log.i("AAA","Nothing S0");

            }
        });

       spinner1.setOnItemSelectedListener(new OnItemSelectedListener()
       {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view1, int pos, long id) 
            {
                Log.i("AAA","OnItemSelected S1");
                int loc = pos;
                switch(loc)
                {
                    case 1:
                        Log.i("AAA","Australia");
                        if(object.equals(aus))
                        {
                            city = aus.getType(loc);
                        }
                        else
                        {
                            break;
                        }


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity.this, android.R.layout.simple_spinner_item, city);
                        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        spinner2.setAdapter(adapter);           Log.i("AAA","spinner2");
                        break;

                    default:
                            Log.i("AAA", "default 1");
                        break;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                Log.i("AAA","Nothing S1");              
            }

        }); 

       spinner2.setOnItemSelectedListener(new OnItemSelectedListener()
       {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long id)
        {
            int loc = pos;
            switch (loc)
            {
            case 1:
                if(object.equals(aus))
                {
                    area = aus.getTitle(loc);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity.this, android.R.layout.simple_spinner_item, area);
                adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                spinner3.setAdapter(adapter);           Log.i("","spinner3");               
                break;

            default:
                break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) 
        {
            // TODO Auto-generated method stub          
        }
       });

    }// on-create


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btnSubmit:

            break;

        case R.id.btnCancel:
            finish();
            break;

        default:
            break;
        }       
    }   
}

/*************************************************************************************************************************************************/


import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {

    Spinner spin;
    String spin_val;
    String[] gender = { "Male", "Female" };//array of strings used to populate the spinner
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_main);//setting layout
		
        spin = (Spinner) findViewById(R.id.spinner_id);//fetching view's id
		
        //Register a callback to be invoked when an item in this AdapterView has been selected
        spin.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int position, long id) { 
                // TODO Auto-generated method stub
                spin_val = gender[position];//saving the value selected


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
		
      //setting array adaptors to spinners 
      //ArrayAdapter is a BaseAdapter that is backed by an array of arbitrary objects
	  
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, gender);

        // setting adapteers to spinners
        spin.setAdapter(spin_adapter);
    }


}
	*/
	
	
	
	
	INSERT INTO `androidrecipeapp`.`recipe` (`recipeName`, `summery`, `creationDate`, `modifyDate`, `rating`, `userName`, `prepTime`, `cookTime`, `numRatings`, `servings`, `picId`, `directions`, `ingredientDiscription`, `ingredientName`, `type`) VALUES ('Butter Ball Turkey', 'roasted turkeyfdg  gfgdg sd gfd ef bg sgs sfhs ', '2013-02-11 12:20:28', '0000-00-00 00:00:00', '4', 'Van Keizer', '10', '90', '1', '7', '', 'ghj jghj ftyf h gd hjhgj fghjg fghj fghjf fhj fhgj hg', 'gj fghjg dyjdty jdjhdgghd jg hjfghj  jgfjf fgfj', 'gj fhfjgfhj gj ffghjg gfhjg fjf gfhgj dfyjhgjf', 'Main Dish')
	
	
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
 
import java.util.ArrayList;
import java.util.List;
 
import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.VERTICAL;
 
public class Sample extends Activity {
    private List<EditText> editTextList = new ArrayList<EditText>();
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        LinearLayout linearLayout = new LinearLayout(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(FILL_PARENT, WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(VERTICAL);
 
        int count = 10;
        linearLayout.addView(tableLayout(count));
        linearLayout.addView(submitButton());
        setContentView(linearLayout);
    }
 
    private Button submitButton() {
        Button button = new Button(this);
        button.setHeight(WRAP_CONTENT);
        button.setText("Submit");
        button.setOnClickListener(submitListener);
        return button;
    }
 
    // Access the value of the EditText
 
    private View.OnClickListener submitListener = new View.OnClickListener() {
        public void onClick(View view) {
            StringBuilder stringBuilder = new StringBuilder();
            for (EditText editText : editTextList) {
                stringBuilder.append(editText.getText().toString());
            }
        }
    };
 
    // Using a TableLayout as it provides you with a neat ordering structure
 
    private TableLayout tableLayout(int count) {
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setStretchAllColumns(true);
        int noOfRows = count / 5;
        for (int i = 0; i < noOfRows; i++) {
            int rowId = 5 * i;
            tableLayout.addView(createOneFullRow(rowId));
        }
        int individualCells = count % 5;
        tableLayout.addView(createLeftOverCells(individualCells, count));
        return tableLayout;
    }
 
    private TableRow createLeftOverCells(int individualCells, int count) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        int rowId = count - individualCells;
        for (int i = 1; i <= individualCells; i++) {
            tableRow.addView(editText(String.valueOf(rowId + i)));
        }
        return tableRow;
    }
 
    private TableRow createOneFullRow(int rowId) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        for (int i = 1; i <= 5; i++) {
            tableRow.addView(editText(String.valueOf(rowId + i)));
        }
        return tableRow;
    }
 
    private EditText editText(String hint) {
        EditText editText = new EditText(this);
        editText.setId(Integer.valueOf(hint));
        editText.setHint(hint);
        editTextList.add(editText);
        return editText;
    }
}
package com.example.hotelmenu.Admin;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import com.example.hotelmenu.Constants;
import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.R;

public class AddFood extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner category;
    EditText name, price, editTextImage;
    Button add_item, clear, update;
    String strCat, strName, strImage;
    double strPrice;
    ProjectDatabase projectDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        projectDatabase = new ProjectDatabase(AddFood.this);
        category = findViewById(R.id.category);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        editTextImage = findViewById(R.id.foodImage);
        add_item = findViewById(R.id.add_item);
        update = findViewById(R.id.update_item);
        clear = findViewById(R.id.clear);

        populateSpinner();
        updateValues();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValues();
            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = name.getText().toString();
                strPrice = Double.parseDouble(price.getText().toString());
                strImage = editTextImage.getText().toString().toLowerCase();
                if (strImage.equals("")){
                    strImage = "ic_launcher";
                }
                if (!strName.isEmpty() && strPrice > 0) {
                    try {
                        long val = projectDatabase.addFood(strName, strCat, strPrice, strImage);
                        if (val > 0) {
                            Toast.makeText(AddFood.this, "Items Added", Toast.LENGTH_SHORT).show();
                            clearValues();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddFood.this);
                            builder.setTitle("Error");
                            builder.setMessage("Some Error Found");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }


                    } catch (Exception e) {
                        Log.e("Register Exception", e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddFood.this);
                        builder.setTitle("Error");
                        builder.setMessage(e.getMessage());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                } else
                    Toast.makeText(AddFood.this, "Enter Some Values", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateValues() {
        final int getId = getIntent().getIntExtra("food_id", -1);
        final String getFName = getIntent().getStringExtra("food_name");
        final String getFCat = getIntent().getStringExtra("food_category");
        final double getFPrice = getIntent().getDoubleExtra("food_price", -1);
        final String getFImage = getIntent().getStringExtra("food_image");
        Log.e("Receive Data", getId + "-" + getFName + "-" + getFCat + "-" + getFPrice);

        if (getId == -1 && getFName == null && getFCat == null && getFPrice == -1) {

            return;
        } else {
            update.setVisibility(View.VISIBLE);
            add_item.setVisibility(View.GONE);
            name.setText(getFName);
            price.setText(String.valueOf(getFPrice));
            editTextImage.setText(getFImage);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        strName = name.getText().toString();
                        strPrice = Double.parseDouble(price.getText().toString());
                        strImage = editTextImage.getText().toString();
                        if (strImage.equals("")){
                            strImage = "ic_launcher";
                        }
                        SQLiteDatabase db = projectDatabase.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(Constants.food_col_id, getId);
                        values.put(Constants.food_col_foodName, strName);
                        values.put(Constants.food_col_category, strCat);
                        values.put(Constants.food_col_price, strPrice);
                        values.put(Constants.food_col_image, strImage);

                        db.update(Constants.food_tableName, values, "id = ?", new String[]{String.valueOf(getId)});
                        db.close();
                        Toast.makeText(AddFood.this, "Values Updated", Toast.LENGTH_SHORT).show();
                        clearValues();
                    } catch (Exception ex) {
                        Log.e("Update Exception", ex.toString());
                    }
                }
            });
        }
    }

    private void clearValues() {
        name.setText("");
        price.setText("");
        editTextImage.setText("");
        name.setHint("Enter Name");
        price.setHint("Enter Price");
        editTextImage.setHint("Enter Image Name");


    }

    private void populateSpinner() {
        // Spinner click listener
        category.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Starter");
       categories.add("Soup");
       categories.add("rice");
       categories.add("pakistani chicken");
       categories.add("BAR B.Q");
       categories.add("Mutton");
       categories.add("Tandoor");
       categories.add("Salad");
       categories.add("Beverages");
       categories.add("Pakistani rice");
       categories.add("Chinese Gravery");
       categories.add("SEA FOOD");
       categories.add("Noodles");
       categories.add("chopesy");
       categories.add("Plater");




        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        category.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strCat = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

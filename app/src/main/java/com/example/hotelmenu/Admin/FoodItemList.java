package com.example.hotelmenu.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.Models.FoodModel;
import com.example.hotelmenu.R;

public class FoodItemList extends AppCompatActivity {
    RecyclerView recyclerView;
    ProjectDatabase projectDatabase;
    private ArrayList<FoodModel> foodList;
    FoodModel foodModel;
    public static FoodListAdapter foodListAdapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        projectDatabase = new ProjectDatabase(this);
        foodList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setAdapter();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodItemList.this, AddFood.class));
            }
        });
    }

    public void setAdapter() {
        foodList.clear();
        foodListAdapter = new FoodListAdapter(this, readAllData());
        recyclerView.setAdapter(foodListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    private ArrayList<FoodModel> readAllData() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select id, FoodName, Category, Price, Image From FoodItems", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String FoodName = cursor.getString(1);
                String Category = cursor.getString(2);
                double Price = cursor.getDouble(3);
                String Image = cursor.getString(4);

                foodModel = new FoodModel(this);
                foodModel.setId(id);
                foodModel.setFoodName(FoodName);
                foodModel.setCategory(Category);
                foodModel.setPrice((int) Price);
                foodModel.setImage(Image);
                foodList.add(foodModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return foodList;

    }

    private class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
        Context context;
        private List<FoodModel> foodList;

        public FoodListAdapter(Context context, ArrayList<FoodModel> foodModel) {
            this.context = context;
            this.foodList = foodModel;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.food_listitems, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final int id = foodList.get(position).getId();
            final String name = foodList.get(position).getFoodName();
            final String category = foodList.get(position).getCategory();
            final double price = foodList.get(position).getPrice();
            final String imageFood = foodList.get(position).getImage();
            Log.e("Data", imageFood + "-" + name + price);

            holder.foodName.setText(name);
            holder.foodCategory.setText(category);
            holder.foodPrice.setText(String.valueOf(price));

            if (imageFood.length() > 0) {
                String uri = "@drawable/" + imageFood;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.icon.setImageDrawable(res);
            }
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FoodItemList.this, AddFood.class);
                    intent.putExtra("food_id", id);
                    intent.putExtra("food_name", name);
                    intent.putExtra("food_category", category);
                    intent.putExtra("food_price", price);
                    startActivity(intent);
                    setAdapter();

                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemList.this);
                    builder.setTitle("Delete Item");
                    builder.setMessage("Are you sure to Delete this Item");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db = projectDatabase.getWritableDatabase();
                            db.delete("FoodItems", "id = ?", new String[]{String.valueOf(id)});
                            Toast.makeText(context, "Item has been deleted", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            setAdapter();

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }
            });
        }


        @Override
        public int getItemCount() {
            return foodList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon, delete, edit;
            TextView foodName, foodPrice, foodCategory;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.imageFood);
                foodName = itemView.findViewById(R.id.foodName);
                foodPrice = itemView.findViewById(R.id.fPrice);
                foodCategory = itemView.findViewById(R.id.food_Category);
                delete = itemView.findViewById(R.id.delItem);
                edit = itemView.findViewById(R.id.editItem);

            }
        }
    }
}

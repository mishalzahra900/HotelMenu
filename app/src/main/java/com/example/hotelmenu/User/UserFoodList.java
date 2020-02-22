package com.example.hotelmenu.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelmenu.Constants;
import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.Models.Cart;
import com.example.hotelmenu.Models.FoodModel;
import com.example.hotelmenu.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.hotelmenu.Constants.food_tableName;

public class UserFoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    UserFoodListAdapter userFoodListAdapter;
    TextView textView;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<FoodModel> foodList;
    String getCat;
    FoodModel foodModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getCat = getIntent().getStringExtra("Category");

        recyclerView = findViewById(R.id.recyvlerView);

        projectDatabase = new ProjectDatabase(UserFoodList.this);
        foodList = new ArrayList<>();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        userFoodListAdapter = new UserFoodListAdapter(UserFoodList.this, readFoodList());
        recyclerView.setAdapter(userFoodListAdapter);
    }

    private ArrayList<FoodModel> readFoodList() {
        boolean distinct = true;
        db = projectDatabase.getReadableDatabase();
        String selection = Constants.food_col_category + " = ?";
        String[] column = {Constants.food_col_foodName, Constants.food_col_price, Constants.food_col_image};
        String[] args = {getCat};
        Cursor cursor = db.query(distinct, Constants.food_tableName, column, selection, args, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String FoodName = cursor.getString(0);
                double Price = cursor.getDouble(1);
                String Image = cursor.getString(2);
                Log.e("Food List", FoodName + "- " + Price);

                foodModel = new FoodModel(this);
                foodModel.setFoodName(FoodName);
                foodModel.setPrice((int) Price);
                foodModel.setImage(Image);
                foodList.add(foodModel);
                Log.e("List", foodList.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return foodList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.checkout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.checkout:
                startActivity(new Intent(this, Checkout.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class UserFoodListAdapter extends RecyclerView.Adapter<UserFoodListAdapter.ViewHolder> {
        private List<FoodModel> foodList;
        Context context;

        public UserFoodListAdapter(Context context, ArrayList<FoodModel> foodList) {
            this.context = context;
            this.foodList = foodList;
        }


        @NonNull
        @Override
        public UserFoodListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_user_food_list, parent, false);
            return new UserFoodListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserFoodListAdapter.ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
//            Log.e("Read Data", foodList.toString());
            final String FoodName = foodList.get(position).getFoodName();
            final double price = foodList.get(position).getPrice();
            final String image = foodList.get(position).getImage();

            holder.foodName.setText(FoodName);
            holder.price.setText(String.valueOf(price));

            if (image.length() > 0) {
                String uri = "@drawable/" + foodList.get(position).getImage();
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.imageView.setImageDrawable(res);
            } else {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);

            }

            holder.cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final Cart cart = new Cart();
                        cart.setName(FoodName);
                        cart.setCategory(getCat);
                        cart.setPrice(price);
                        cart.setQty(1);
                        cart.setImg(image);

                        long res = projectDatabase.insertCart(cart.getName(), cart.getCategory(), cart.getPrice(), cart.getImg(), cart.getQty());
                        if (res > 0) {
                            Toast.makeText(UserFoodList.this, "Food Item Added to your Cart", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserFoodList.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView, cart;
            TextView foodName, price;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageFood);
                foodName = itemView.findViewById(R.id.foodName);
                price = itemView.findViewById(R.id.fPrice);
                cart = itemView.findViewById(R.id.cart);
            }
        }
    }
}

package com.example.hotelmenu.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelmenu.Admin.AddFood;
import com.example.hotelmenu.Admin.FoodItemList;
import com.example.hotelmenu.Constants;
import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.Models.Cart;
import com.example.hotelmenu.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Checkout extends AppCompatActivity {
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<Cart> cartList;
    Cart cart;
    TextView totalPrice;
    Button checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerView = findViewById(R.id.recyvlerView);
        projectDatabase = new ProjectDatabase(Checkout.this);
        cartList = new ArrayList<>();

        db = projectDatabase.getReadableDatabase();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setAdapter();
        totalPrice = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.checkout);

       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = db.rawQuery("SELECT SUM(" + Constants.cart_col_price + ") as Total FROM " + Constants.cart_tableName, null);
                if (cursor.moveToFirst()) {
                    int total = cursor.getInt(cursor.getColumnIndex("Total"));
                    Log.e("Total", String.valueOf(total));
                    totalPrice.setText(total);
                }
                cursor.close();
                db.close();
            }
        });*/


    }

    private ArrayList<Cart> readcartList() {
        Cursor cursor = db.rawQuery("Select id, Name, Category, Price, Quantity, Image From Cart", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String Name = cursor.getString(1);
                String Category = cursor.getString(2);
                double Price = cursor.getDouble(3);
                double Quantity = cursor.getDouble(4);
                String Image = cursor.getString(5);
                Log.e("Cart List", Image + "--" + Name + "- " + Price);

                cart = new Cart();
                cart.setId(id);
                cart.setName(Name);
                cart.setCategory(Category);
                cart.setPrice(Price);
                cart.setQty(Quantity);
                cart.setImg(Image);
                cartList.add(cart);
//                Log.e("List", String.valueOf(cartList));

            } while (cursor.moveToNext());
        }

        cursor.close();
        // db.close();

        return cartList;
    }
    public void setAdapter(){
        cartList.clear();
        cartAdapter = new CartAdapter(Checkout.this, readcartList());
        recyclerView.setAdapter(cartAdapter);
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        Context context;
        private List<Cart> cartList;
        double strQty;

        public CartAdapter(Context context, ArrayList<Cart> cartList) {
            this.context = context;
            this.cartList = cartList;
        }

        @NonNull
        @Override
        public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_checkout_list, parent, false);

            return new CartAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final int id = cartList.get(position).getId();
            final String name = cartList.get(position).getName();
            final String category = cartList.get(position).getCategory();
            final double price = cartList.get(position).getPrice();
            final double qty = cartList.get(position).getQty();
            final String imageItem = cartList.get(position).getImg();
            Log.e("Cart Data", imageItem + "-" + name + price);

            holder.foodName.setText(name);
            holder.foodCategory.setText(category);
            holder.foodPrice.setText(String.valueOf(price));
            holder.qty.setText("1");
            strQty = Double.parseDouble(holder.qty.getText().toString());
            Log.e("Image", imageItem);
            if (imageItem.length() > 0) {
                String uri = "@drawable/" + imageItem;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.icon.setImageDrawable(res);
            } else {
                holder.icon.setImageResource(R.mipmap.ic_launcher);

            }

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = projectDatabase.getWritableDatabase();
                    db.delete("Cart", "id = ?", new String[]{String.valueOf(id)});
                    setAdapter();

                }
            });
        }


        @Override
        public int getItemCount() {
            return cartList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon, delete;
            TextView foodName, foodPrice, foodCategory;
            EditText qty;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.imageFood);
                foodName = itemView.findViewById(R.id.foodName);
                foodPrice = itemView.findViewById(R.id.fPrice);
                foodCategory = itemView.findViewById(R.id.food_Category);
                delete = itemView.findViewById(R.id.delItem);
                qty = itemView.findViewById(R.id.qty);

            }
        }
    }

}
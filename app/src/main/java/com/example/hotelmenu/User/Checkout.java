package com.example.hotelmenu.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    int SubTotal, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        projectDatabase = new ProjectDatabase(Checkout.this);
        cartList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cartAdapter = new CartAdapter(Checkout.this, readcartList());
        recyclerView.setAdapter(cartAdapter);

        totalPrice = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.confirm);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this);
                builder.setTitle("Confirm Order");
                builder.setMessage("Click OK to confirm Order");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Checkout.this, UserDashboardActivity.class));
                    }
                });
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Checkout.this, UserDashboardActivity.class));
                        //Truncate the cart Data
                    }
                });
                builder.show();

            }
        });
    }
    public void setAdapter() {
       /* cartList.clear();
        cartAdapter = new CartAdapter(Checkout.this, readcartList());
        recyclerView.setAdapter(cartAdapter);*/
    }

    private ArrayList<Cart> readcartList() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select id, Name, Category, Price, Quantity, Image From Cart", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String Name = cursor.getString(1);
                String Category = cursor.getString(2);
                double Price = cursor.getDouble(3);
                int Quantity = cursor.getInt(4);
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




    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        Context context;
        private List<Cart> cartList;
        private int id, qty;
        double price;
        String name, category, imageItem;


        public CartAdapter(Context context, ArrayList<Cart> cartList) {
            this.context = context;
            this.cartList = cartList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_checkout_list, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            id = cartList.get(position).getId();
            name = cartList.get(position).getName();
            category = cartList.get(position).getCategory();
            price = cartList.get(position).getPrice();
            qty = cartList.get(position).getQty();
            imageItem = cartList.get(position).getImg();
            Log.e("Cart Data", imageItem + "-" + name + price);

            holder.foodName.setText(name);
            holder.foodCategory.setText(category);
            holder.foodPrice.setText(String.valueOf(price));

            Log.e("cartQty", String.valueOf(qty));
            holder.itemQtyText.setText(String.valueOf(qty));

            if (imageItem.length() > 0) {
                String uri = "@drawable/" + imageItem;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.icon.setImageDrawable(res);
            } else {
                holder.icon.setImageResource(R.mipmap.ic_launcher);

            }
            holder.pos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qty++;
                    cart.setQty(qty);
                    holder.itemQtyText.setText(String.valueOf(cart.getQty()));

                    updateCart();
                    Log.e("Update Qty", String.valueOf(qty));

                }
            });
            holder.neg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qty--;
                    cart.setQty(qty);
                    holder.itemQtyText.setText(String.valueOf(cart.getQty()));
                    updateCart();

                }
            });


            updateCart();

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = projectDatabase.getWritableDatabase();
                    db.delete(Constants.cart_tableName, "id = ?", new String[]{String.valueOf(id)});
                    calculatePrice();
                    calculateTotal();
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
            TextView foodName, foodPrice, foodCategory, itemQtyText;
            ImageView pos, neg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.imageFood);
                foodName = itemView.findViewById(R.id.foodName);
                foodPrice = itemView.findViewById(R.id.fPrice);
                foodCategory = itemView.findViewById(R.id.food_Category);
                delete = itemView.findViewById(R.id.delItem);
                pos = itemView.findViewById(R.id.posQty);
                neg = itemView.findViewById(R.id.negQty);
                itemQtyText = itemView.findViewById(R.id.textQty);

            }
        }

        private void calculateTotal() {
            db = projectDatabase.getReadableDatabase();
            Cursor cursorTotal = db.rawQuery("SELECT SUM(" + Constants.cart_col_subTotal + ") as Total FROM " + Constants.cart_tableName, null);
            if (cursorTotal.moveToFirst()) {
                total = cursorTotal.getInt(cursorTotal.getColumnIndex("Total"));
                Log.e("Total", String.valueOf(total));
                totalPrice.setText("Rs. " + String.valueOf(total));
            }
            cursorTotal.close();
            db.close();
        }

        private void calculatePrice() {
            db = projectDatabase.getReadableDatabase();
            try {
                Cursor curSubTotal = db.rawQuery("Select " + Constants.cart_col_qty + " * " + Constants.cart_col_price + " as SubTotal From " + Constants.cart_tableName, null);
                if (curSubTotal.moveToFirst()) {
                    SubTotal = curSubTotal.getInt(curSubTotal.getColumnIndex("SubTotal"));
                    Log.e("SubTotal", String.valueOf(SubTotal));
                    cart.setSubTotal(SubTotal);
                    db = projectDatabase.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Constants.cart_col_subTotal, SubTotal);
                    db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(id)});
                    db.close();
                }
                curSubTotal.close();

                //db.close();
            } catch (Exception e) {
                Log.e("Excep", e.toString());
            }
        }

        private void updateCart() {
            db = projectDatabase.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.cart_col_id, cart.getId());
            values.put(Constants.cart_col_fName, cart.getName());
            values.put(Constants.cart_col_category, cart.getCategory());
            values.put(Constants.cart_col_price, cart.getPrice());
            values.put(Constants.cart_col_qty, cart.getQty());
            values.put(Constants.cart_col_subTotal, cart.getSubTotal());

            db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(cart.getId())});
            db.close();
            calculatePrice();
            calculateTotal();
            //setAdapter();

        }


    }


}
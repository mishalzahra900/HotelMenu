package com.example.hotelmenu.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.hotelmenu.Admin.AddFood;
import com.example.hotelmenu.Admin.FoodItemList;
import com.example.hotelmenu.Constants;
import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.MainActivity;
import com.example.hotelmenu.Models.Cart;
import com.example.hotelmenu.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Checkout extends Fragment {
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<Cart> cartList;
    Cart cart;
    TextView totalPrice;
    Button checkout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_checkout, container, false);
        projectDatabase = new ProjectDatabase(getContext());
        cartList = new ArrayList<>();

        recyclerView = root.findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cartAdapter = new CartAdapter(getContext(), readcartList());
        recyclerView.setAdapter(cartAdapter);

        totalPrice = root.findViewById(R.id.totalPrice);
        checkout = root.findViewById(R.id.confirm);

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm Order");
                builder.setMessage("Click OK to confirm Order");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final Dialog customDialog = new Dialog(getContext());
                        customDialog.setContentView(R.layout.activity_confirm_order);
                        customDialog.setCancelable(true);
                        customDialog.setTitle("Confirm Order...");

                        Button confirm = (Button) customDialog.findViewById(R.id.confirmOrder);
                        Button cancel = (Button) customDialog.findViewById(R.id.cancel);
                        final EditText name = customDialog.findViewById(R.id.custName);
                        final EditText tableNo = customDialog.findViewById(R.id.tableNo);
                        // if button is clicked, close the custom dialog
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                customDialog.cancel();
                            }
                        });
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String nameStr = name.getText().toString();
                                int tableNoStr = Integer.parseInt(tableNo.getText().toString());
                                db = projectDatabase.getReadableDatabase();
                                Cursor cursor = db.rawQuery("Select id, Name, Category, Price, Image, Quantity From Cart", new String[]{});

                                if (cursor.moveToFirst()) {
                                    do {
                                        int id = cursor.getInt(0);
                                        String Name = cursor.getString(1);
                                        String Category = cursor.getString(2);
                                        double Price = cursor.getDouble(3);
                                        String Image = cursor.getString(4);
                                        int Quantity = cursor.getInt(5);

                                        long res = projectDatabase.confirmOrder(Name, Category, Price, Image, Quantity, nameStr, tableNoStr);
                                        Log.e("App Confirm", String.valueOf(res));
                                        //Log.e("App Name", Name);

                                        db = projectDatabase.getWritableDatabase();
                                        db.execSQL("delete from " + Constants.cart_tableName);
                                        db.close();
                                        Toast.makeText(getContext(), "Your Order is Confirmed", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getContext(), UserDashboardActivity.class));
                                        getActivity().finish();
                                        customDialog.cancel();

                                    } while (cursor.moveToNext());
                                }

                                cursor.close();
                            }
                        });

                        customDialog.show();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), UserDashboardActivity.class));
                        db = projectDatabase.getWritableDatabase();
                        db.execSQL("delete from " + Constants.cart_tableName);
                        db.close();

                    }
                });
                builder.show();

            }
        });
        return root;
    }

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


    }
*/
    public void setAdapter() {
        cartList.clear();
        cartAdapter = new CartAdapter(getContext(), readcartList());
        recyclerView.setAdapter(cartAdapter);
    }

   /* @Override
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
    }*/

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
        private int cartId, qty;
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
            cartId = cartList.get(position).getId();
            name = cartList.get(position).getName();
            category = cartList.get(position).getCategory();
            price = cartList.get(position).getPrice();
            qty = cartList.get(position).getQty();
            imageItem = cartList.get(position).getImg();
            Log.e("Cart Data", imageItem + "-" + name + price);

            holder.foodName.setText(name);
            holder.foodCategory.setText(category);
            holder.foodPrice.setText("Rs. " + String.valueOf(price));

            if (imageItem.length() > 0) {
                String uri = "@drawable/" + cartList.get(position).getImg();
                int imageResource = getResources().getIdentifier(uri, null, getContext().getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.icon.setImageDrawable(res);
            } else {
                holder.icon.setImageResource(R.mipmap.ic_launcher);

            }
            Log.e("cartQty", String.valueOf(qty));

            calculateTotal();
            holder.numberBtn.setNumber(String.valueOf(qty));
            holder.numberBtn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    cart = cartList.get(position);
                    cartId = cartList.get(position).getId();
                    cart.setQty(newValue);
                    updateCart();

                    Log.e("Pos", String.valueOf(position) + "--" + cartId);
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db = projectDatabase.getWritableDatabase();
                    db.delete(Constants.cart_tableName, "id = ?", new String[]{String.valueOf(cartId)});

                    calculateTotal();
                    notifyDataSetChanged();
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
            // ImageView pos, neg;
            public ElegantNumberButton numberBtn;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.imageFood);
                foodName = itemView.findViewById(R.id.foodName);
                foodPrice = itemView.findViewById(R.id.fPrice);
                foodCategory = itemView.findViewById(R.id.food_Category);
                delete = itemView.findViewById(R.id.delItem);
                numberBtn = itemView.findViewById(R.id.number_button);


            }
        }

        private void calculateTotal() {
            db = projectDatabase.getReadableDatabase();
            Cursor cursorTotal = db.rawQuery("SELECT SUM(" + Constants.cart_col_qty + " * " + Constants.cart_col_price + ") as Total FROM " + Constants.cart_tableName, null);
            if (cursorTotal.moveToFirst()) {
                int total = cursorTotal.getInt(cursorTotal.getColumnIndex("Total"));
//                Log.e("Total", String.valueOf(total));
                totalPrice.setText("Rs. " + total);
            }
            cursorTotal.close();
            db.close();
        }


      /*  private void calculatePrice(int newId) {
            db = projectDatabase.getReadableDatabase();
            try {
                Cursor curSubTotal = db.rawQuery("Select " + Constants.cart_col_qty + " * " + Constants.cart_col_price +
                        " as SubTotal From " + Constants.cart_tableName, null);
                if (curSubTotal.moveToFirst()) {
                    SubTotal = curSubTotal.getInt(curSubTotal.getColumnIndex("SubTotal"));
                    Log.e("SubTotal", String.valueOf(SubTotal));
                    //cart.setSubTotal(SubTotal);
                    db = projectDatabase.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Constants.cart_col_subTotal, SubTotal);
                    db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(newId)});
                    db.close();
                }
                curSubTotal.close();

                //db.close();
            } catch (Exception e) {
                Log.e("Excep", e.toString());
            }
        }*/

        private void updateCart() {
            db = projectDatabase.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.cart_col_qty, cart.getQty());
            try {
                String query = String.format("UPDATE Cart SET Quantity= %s WHERE id = %d", cart.getQty(), cart.getId());
                db.execSQL(query);
                db.close();
            } catch (Exception e) {
                Log.e("Qu Exce", e.toString());
            }
            //db.update(Constants.cart_tableName, values, "id = ?", new String[]{String.valueOf(id)});
            calculateTotal();
            notifyDataSetChanged();
            setAdapter();
        }


    }


}
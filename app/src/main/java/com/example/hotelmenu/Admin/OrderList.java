package com.example.hotelmenu.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hotelmenu.Constants;
import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.Models.FoodModel;
import com.example.hotelmenu.Models.OrderModel;
import com.example.hotelmenu.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrderList extends AppCompatActivity {

    RecyclerView recyclerView;
    ProjectDatabase projectDatabase;
    private ArrayList<OrderModel> orderLists;
    OrderModel orderModel;
    SQLiteDatabase db;
    OrderListAdapter orderListAdapter;
    TextView totalPrice;
    Button checkout;
    int getTableNo;
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getTableNo = getIntent().getIntExtra("Table_NO", -1);
        Log.e("Get Table No", String.valueOf(getTableNo));
        projectDatabase = new ProjectDatabase(this);
        orderLists = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        orderListAdapter = new OrderListAdapter(this, readOrderList());
        recyclerView.setAdapter(orderListAdapter);
//        setAdapter();

        totalPrice = findViewById(R.id.totalPrice);
        checkout = findViewById(R.id.confirm);
        checkout.setVisibility(View.GONE);


    }

    public void setAdapter() {
        orderLists.clear();
        orderListAdapter = new OrderListAdapter(this, readOrderList());
        recyclerView.setAdapter(orderListAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //       setAdapter();
    }

    private ArrayList<OrderModel> readOrderList() {
        boolean distinct = true;
        db = projectDatabase.getReadableDatabase();
        String selection = Constants.col_tableNO + " = ?";
        String[] column = {Constants.cart_col_id, Constants.cart_col_fName, Constants.cart_col_category,
                Constants.cart_col_price, Constants.cart_col_image, Constants.cart_col_qty,
                Constants.col_userName, Constants.col_tableNO};
        String[] args = {String.valueOf(getTableNo)};
        Cursor cursor = db.query(distinct, Constants.order_tableName, column, selection, args,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String FoodName = cursor.getString(1);
                String Category = cursor.getString(2);
                double Price = cursor.getDouble(3);
                String Image = cursor.getString(4);
                int Qty = cursor.getInt(5);
                String Username = cursor.getString(6);
                int tableNo = cursor.getInt(7);

                long val = projectDatabase.insertAdminOrder(FoodName, Category, Price, Image, Qty, Username, tableNo);
                Log.e("Val Added", String.valueOf(val));

                Log.e("Order List", Qty + "- " + Username + " -- " + tableNo);
                orderModel = new OrderModel();
                orderModel.setId(id);
                orderModel.setName(FoodName);
                orderModel.setCategory(Category);
                orderModel.setPrice(Price);
                orderModel.setImg(Image);
                orderModel.setQty(Qty);
                orderModel.setCustName(Username);
                orderModel.setTableNo(tableNo);


                orderLists.add(orderModel);
                Log.e("OrderLists", orderLists.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return orderLists;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        db = projectDatabase.getWritableDatabase();
        db.execSQL("delete from " + Constants.adminOrder_tableName);
        db.close();
        total = 0;
        OrderList.this.finish();

    }

    private class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
        Context context;
        private List<OrderModel> orderModelList;

        public OrderListAdapter(Context context, ArrayList<OrderModel> orderModelList) {
            this.context = context;
            this.orderModelList = orderModelList;
        }

        @NonNull
        @Override
        public OrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.admin_orderlistitem, parent, false);

            return new OrderListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderListAdapter.ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final int id = orderModelList.get(position).getId();
            final String name = orderModelList.get(position).getName();
            final String category = orderModelList.get(position).getCategory();
            final double price = orderModelList.get(position).getPrice();
            final String imageFood = orderModelList.get(position).getImg();
            final String custName = orderModelList.get(position).getCustName();
            //final int table = orderModelList.get(position).getTableNo();
            Log.e("Data", imageFood + "-" + name + price);

            holder.foodName.setText(name);
            holder.custName.setText(custName);
            holder.foodCategory.setText(category);
            holder.foodPrice.setText("Rs. " + String.valueOf(price));

            calculateTotal();

            if (imageFood.length() > 0) {
                String uri = "@drawable/" + imageFood;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                try {
                    Drawable res = getResources().getDrawable(imageResource);
                    holder.icon.setImageDrawable(res);
                } catch (Resources.NotFoundException e) {
                    holder.icon.setImageResource(R.mipmap.ic_launcher);

                }
            }
            holder.edit.setVisibility(View.GONE);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderList.this);
                    builder.setTitle("Delete Item");
                    builder.setMessage("Are you sure to Delete this Item");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db = projectDatabase.getWritableDatabase();
                            db.delete(Constants.order_tableName, "id = ?", new String[]{String.valueOf(id)});
                            Toast.makeText(context, "Item has been deleted", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            setAdapter();
                            calculateTotal();

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
            return orderModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView icon, delete, edit;
            TextView foodName, foodPrice, foodCategory, custName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.imageFood);
                foodName = itemView.findViewById(R.id.foodName);
                foodPrice = itemView.findViewById(R.id.fPrice);
                foodCategory = itemView.findViewById(R.id.food_Category);
                delete = itemView.findViewById(R.id.delItem);
                edit = itemView.findViewById(R.id.editItem);
                custName = itemView.findViewById(R.id.custName);
                //tableNo = itemView.findViewById(R.id.tablNoo);

            }
        }

        private void calculateTotal() {

            db = projectDatabase.getReadableDatabase();
            try {
                total = 0;
                Cursor cursorTotal = db.rawQuery("SELECT SUM(" + Constants.adminOrder_col_qty + " * " + Constants.adminOrder_col_price + ") as Total FROM "
                        + Constants.adminOrder_tableName, null);

                if (cursorTotal.moveToFirst()) {
                    total = cursorTotal.getInt(cursorTotal.getColumnIndex("Total"));
                    // Log.e("Total", String.valueOf(total));
                    totalPrice.setText("Rs. " + total);
                }
                cursorTotal.close();
                db.close();
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());

            }

        }
    }
}

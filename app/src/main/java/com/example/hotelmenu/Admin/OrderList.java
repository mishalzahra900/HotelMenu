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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        projectDatabase = new ProjectDatabase(this);
        orderLists = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setAdapter();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderList.this, AddFood.class));
            }
        });
    }

    public void setAdapter() {
        orderLists.clear();
        orderListAdapter = new OrderListAdapter(this, readAllData());
        recyclerView.setAdapter(orderListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    private ArrayList<OrderModel> readAllData() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select " + Constants.cart_col_id + ", " + Constants.cart_col_fName + ", " + Constants.cart_col_category
                + ", " + Constants.cart_col_price + ", " + Constants.cart_col_image +
                ", " + Constants.cart_col_qty + ", " + Constants.order_custName + " From " + Constants.order_tableName, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String FoodName = cursor.getString(1);
                String Category = cursor.getString(2);
                double Price = cursor.getDouble(3);
                String Image = cursor.getString(4);
                int quan = cursor.getInt(5);
                String custName = cursor.getString(6);

                orderModel = new OrderModel();
                orderModel.setId(id);
                orderModel.setName(FoodName);
                orderModel.setCategory(Category);
                orderModel.setPrice((int) Price);
                orderModel.setImg(Image);
                orderModel.setQty(quan);
                orderModel.setCustName(custName);
                orderLists.add(orderModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return orderLists;

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
            View view = LayoutInflater.from(context).inflate(R.layout.food_listitems, parent, false);

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
            Log.e("Data", imageFood + "-" + name + price);

            holder.foodName.setText(name);
            holder.custName.setText(custName);
            holder.foodCategory.setText(category);
            holder.foodPrice.setText("Rs. " + String.valueOf(price));

            if (imageFood.length() > 0) {
                String uri = "@drawable/" + imageFood;
                Log.e("image", uri);
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                holder.icon.setImageDrawable(res);
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

            }
        }
    }
}

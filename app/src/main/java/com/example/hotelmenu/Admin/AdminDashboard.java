package com.example.hotelmenu.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmenu.R;

public class AdminDashboard extends AppCompatActivity {

    Button list_order, list_items, add_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        list_order = findViewById(R.id.list_orders);
        list_items = findViewById(R.id.list_items);
        add_items = findViewById(R.id.add_food);

        add_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, AddFood.class));
            }
        });
        list_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, FoodItemList.class));
            }
        });

    }
}

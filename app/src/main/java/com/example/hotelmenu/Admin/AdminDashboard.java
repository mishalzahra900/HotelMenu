package com.example.hotelmenu.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmenu.LoginActivity;
import com.example.hotelmenu.MainActivity;
import com.example.hotelmenu.R;
import com.example.hotelmenu.User.Checkout;

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
        list_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, OrderList.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(new Intent(this, MainActivity.class));
                AdminDashboard.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

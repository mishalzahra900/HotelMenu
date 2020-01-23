package com.example.hotelmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrderActivty extends AppCompatActivity {

    TextView food;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_activty);

        food = findViewById(R.id.food);
        confirm = findViewById(R.id.confirmButton);

        String name = getIntent().getStringExtra("FName");
        String price = getIntent().getStringExtra("FPrice");
        food.setText(name + "\n" + price);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderActivty.this, "Order is Confirm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

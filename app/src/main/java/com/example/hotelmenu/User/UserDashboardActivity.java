package com.example.hotelmenu.User;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.LoginActivity;
import com.example.hotelmenu.MainActivity;
import com.example.hotelmenu.R;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CategoriesAdapter categoriesAdapter;
    TextView textView;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<String> categoryList;
    public static String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        recyclerView = findViewById(R.id.recyvlerView);

        textView = findViewById(R.id.usernameText);
        user = getIntent().getStringExtra("Username");
        textView.setText("Welcome\n" + user);
        projectDatabase = new ProjectDatabase(UserDashboardActivity.this);
        categoryList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setAdapter();

    }

    public void setAdapter() {
        categoryList.clear();
        categoriesAdapter = new CategoriesAdapter(UserDashboardActivity.this, readCategories());
        recyclerView.setAdapter(categoriesAdapter);
    }

    private ArrayList<String> readCategories() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select DISTINCT Category From FoodItems", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                String Category = cursor.getString(0);
                Log.e("Category List", Category);
                categoryList.add(Category);
                Log.e("List", categoryList.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return categoryList;

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

            case R.id.userLogout:
                startActivity(new Intent(this, MainActivity.class));
                UserDashboardActivity.this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
        private List<String> catList;
        Context context;

        public CategoriesAdapter(Context context, ArrayList<String> cateList) {
            this.context = context;
            this.catList = cateList;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_list_items, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final String categ = catList.get(position);
            holder.textView.setText(categ);
            if (categ.equals("Starter")) {
                holder.imageView.setImageResource(R.drawable.specialchicken);
                holder.imageView.setImageResource(R.drawable.butterflychicken);
                holder.imageView.setImageResource(R.drawable.fishchillidry);
                holder.imageView.setImageResource(R.drawable.daccachicken);
            } else if (categ.equals("Soup")) {
                holder.imageView.setImageResource(R.drawable.specialsoup);
            } else if (categ.equals("Rice")) {
                holder.imageView.setImageResource(R.drawable.muttonbaryani);
                holder.imageView.setImageResource(R.drawable.muttonpulao);
                holder.imageView.setImageResource(R.drawable.janglipulao);
                holder.imageView.setImageResource(R.drawable.chickenbaryani);
                holder.imageView.setImageResource(R.drawable.chickenpulao);
            } else if (categ.equals("pakistani chicken")) {
                holder.imageView.setImageResource(R.drawable.chickenqoorma);
            } else if (categ.equals("BAR B.Q")) {
                holder.imageView.setImageResource(R.drawable.specialgreenboti);
                holder.imageView.setImageResource(R.drawable.muttonchamp);
                holder.imageView.setImageResource(R.drawable.chickenchestpiece);
                holder.imageView.setImageResource(R.drawable.legpiece);
                holder.imageView.setImageResource(R.drawable.lubnanipiece);
                holder.imageView.setImageResource(R.drawable.chickenbalaiboti);
                holder.imageView.setImageResource(R.drawable.kastooriboti);
                holder.imageView.setImageResource(R.drawable.achariboti);
                holder.imageView.setImageResource(R.drawable.qalmitika);
                holder.imageView.setImageResource(R.drawable.chickenboti);
                holder.imageView.setImageResource(R.drawable.muttonkabab);
                holder.imageView.setImageResource(R.drawable.muttonkabab);
                holder.imageView.setImageResource(R.drawable.golakabab);
                holder.imageView.setImageResource(R.drawable.kabab);
                holder.imageView.setImageResource(R.drawable.reshmikabab);
            } else if (categ.equals("Mutton")) {
                holder.imageView.setImageResource(R.drawable.mutttonkarahi);
            } else if (categ.equals("Tandoor")) {
                holder.imageView.setImageResource(R.drawable.roti);
            } else if (categ.equals("Salad")) {
                holder.imageView.setImageResource(R.drawable.raita);
                holder.imageView.setImageResource(R.drawable.specialsalad);
                holder.imageView.setImageResource(R.drawable.fruitsalad);
                holder.imageView.setImageResource(R.drawable.freshsalad);
                holder.imageView.setImageResource(R.drawable.rusiansalad);
                holder.imageView.setImageResource(R.drawable.kachoomarsalad);
            } else if (categ.equals("Beverages")) {
                holder.imageView.setImageResource(R.drawable.tinpak);
                holder.imageView.setImageResource(R.drawable.freshlime);
                holder.imageView.setImageResource(R.drawable.regulardrinks);
                holder.imageView.setImageResource(R.drawable.minearlwater);
                holder.imageView.setImageResource(R.drawable.minearlwatersmall);
                holder.imageView.setImageResource(R.drawable.coke);
            } else if (categ.equals("Chinese Gravery")) {
                holder.imageView.setImageResource(R.drawable.manchurian);
                holder.imageView.setImageResource(R.drawable.fishmanchurian);
                holder.imageView.setImageResource(R.drawable.chickenwithalmond);
                holder.imageView.setImageResource(R.drawable.chickenwithpineapple);
                holder.imageView.setImageResource(R.drawable.sweetandsourchicken);
                holder.imageView.setImageResource(R.drawable.hotplatechicken);
                holder.imageView.setImageResource(R.drawable.shashilkwithrice);
            } else if (categ.equals("SEA FOOD")) {
                holder.imageView.setImageResource(R.drawable.fingerfish);
            } else if (categ.equals("SEA FOOD")) {
                holder.imageView.setImageResource(R.drawable.specialchowmein);
            } else if (categ.equals("chopesy")) {
                holder.imageView.setImageResource(R.drawable.specialchopsey);
            } else if (categ.equals("Plater")) {
                holder.imageView.setImageResource(R.drawable.specialchopsey);


            } else {
                holder.imageView.setImageResource(R.mipmap.ic_launcher);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, categ, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserDashboardActivity.this, UserFoodList.class);
                    intent.putExtra("Category", categ);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return catList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }
}

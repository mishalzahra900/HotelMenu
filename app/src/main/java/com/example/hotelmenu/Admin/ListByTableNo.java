package com.example.hotelmenu.Admin;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.hotelmenu.Constants;
import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.LoginActivity;
import com.example.hotelmenu.R;
import com.example.hotelmenu.User.Checkout;
import com.example.hotelmenu.User.UserDashboardActivity;
import com.example.hotelmenu.User.UserFoodList;

import java.util.ArrayList;
import java.util.List;

public class ListByTableNo extends AppCompatActivity {
    RecyclerView recyclerView;
    ByListAdapter byListAdapter;
    ProjectDatabase projectDatabase;
    SQLiteDatabase db;
    ArrayList<String> tableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        recyclerView = findViewById(R.id.recyvlerView);

        projectDatabase = new ProjectDatabase(ListByTableNo.this);
        tableList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyvlerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setAdapter();


    }

    public void setAdapter() {
        tableList.clear();
        byListAdapter = new ByListAdapter(ListByTableNo.this, readTableNo());
        recyclerView.setAdapter(byListAdapter);
    }

    private ArrayList<String> readTableNo() {
        db = projectDatabase.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select DISTINCT TableNO From Orders", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                int TableNO = cursor.getInt(0);
                Log.e("TableNO List", String.valueOf(TableNO));
                tableList.add(String.valueOf(TableNO));
                Log.e("List", tableList.toString());

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tableList;

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
                startActivity(new Intent(this, LoginActivity.class));
                ListByTableNo.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ByListAdapter extends RecyclerView.Adapter<ByListAdapter.ViewHolder> {
        private List<String> tableList;
        Context context;

        public ByListAdapter(Context context, ArrayList<String> tableList) {
            this.context = context;
            this.tableList = tableList;
        }


        @NonNull
        @Override
        public ByListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.category_list_items, parent, false);
            return new ByListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ByListAdapter.ViewHolder holder, final int position) {
            holder.itemView.requestLayout();
            final int table = Integer.parseInt(tableList.get(position));
            holder.textView.setText("Table No: " + table);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(context, table, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListByTableNo.this, OrderList.class);
                    intent.putExtra("Table_NO", table);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return tableList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }
}

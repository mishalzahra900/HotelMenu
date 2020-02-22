package com.example.hotelmenu.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hotelmenu.Constants;

import static com.example.hotelmenu.Constants.cart_col_category;
import static com.example.hotelmenu.Constants.cart_col_fName;
import static com.example.hotelmenu.Constants.cart_col_id;
import static com.example.hotelmenu.Constants.cart_col_image;
import static com.example.hotelmenu.Constants.cart_col_price;
import static com.example.hotelmenu.Constants.cart_col_qty;
import static com.example.hotelmenu.Constants.cart_tableName;
import static com.example.hotelmenu.Constants.col_tableNO;
import static com.example.hotelmenu.Constants.col_userName;
import static com.example.hotelmenu.Constants.databaseName;
import static com.example.hotelmenu.Constants.food_col_category;
import static com.example.hotelmenu.Constants.food_col_foodName;
import static com.example.hotelmenu.Constants.food_col_id;
import static com.example.hotelmenu.Constants.food_col_image;
import static com.example.hotelmenu.Constants.food_col_price;
import static com.example.hotelmenu.Constants.food_tableName;
import static com.example.hotelmenu.Constants.order_tableName;

public class ProjectDatabase extends SQLiteOpenHelper {

    public ProjectDatabase(Context context) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      /*  db.execSQL("create table " + Constants.user_tableName + "(" +
                user_col_id + " integer primary key autoincrement, " +
                user_col_fullName + " text, " +
                user_col_email + " text, " +
                user_col_username + " text, " +
                user_col_password + " text)"
        );*/
        db.execSQL("create table " + food_tableName + "(" +
                food_col_id + " integer primary key autoincrement, " +
                food_col_foodName + " text, " +
                food_col_category + " text, " +
                food_col_price + " text, " +
                food_col_image + " text)"
        );
        db.execSQL("create table " + cart_tableName + "(" +
                cart_col_id + " integer primary key autoincrement, " +
                cart_col_fName + " text, " +
                cart_col_category + " text, " +
                cart_col_price + " text, " +
                cart_col_qty + " text, " +
                cart_col_image + " text)"
        );

        db.execSQL("create table " + order_tableName + "(" +
                cart_col_id + " integer primary key autoincrement, " +
                cart_col_fName + " text, " +
                cart_col_category + " text, " +
                cart_col_price + " text, " +
                cart_col_image + " text, " +
                cart_col_qty + " text, " +
                col_userName + " text, " +
                col_tableNO + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + tableName);
        //db.execSQL("DROP TABLE IF EXISTS " + food_tableName);
        onCreate(db);
        Log.e("Database", "onUpgrade");

    }

 /*   //    start Login or Register
    public long addUser(String fullName, String email, String userName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(user_col_fullName, fullName);
        contentValues.put(user_col_email, email);
        contentValues.put(user_col_username, userName);
        contentValues.put(user_col_password, password);
        long res = db.insert(user_tableName, null, contentValues);
        db.close();
        Log.e("addUser res", String.valueOf(res));
        return res;
    }*/

  /*  public boolean checkUser(String user, String pass) {
        String[] column = {user_col_id};
        SQLiteDatabase db = getReadableDatabase();
        String selection = user_col_username + "=?" + " and " + user_col_password + "=?";
        String[] args = {user, pass};
        Cursor cursor = db.query(user_tableName, column, selection, args, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        Log.e("checkUser cursor", String.valueOf(cursor));

        if (count > 0) {
            return true;
        } else
            return false;
    }*/

    public long addFood(String FoodName, String Category, double Price, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(food_col_foodName, FoodName);
        contentValues.put(food_col_category, Category);
        contentValues.put(food_col_price, Price);
        contentValues.put(food_col_image, image);

        long res = db.insert(food_tableName, null, contentValues);
        db.close();
        Log.e("Food res", String.valueOf(res));
        return res;
    }

    public long insertCart(String FoodName, String Category, double Price, String image, int qty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(cart_col_fName, FoodName);
        contentValues.put(cart_col_category, Category);
        contentValues.put(cart_col_price, Price);
        contentValues.put(cart_col_image, image);
        contentValues.put(cart_col_qty, qty);

        long res = db.insert(cart_tableName, null, contentValues);
        db.close();
        Log.e("Cart res", String.valueOf(res));
        return res;
    }

    public long confirmOrder(String FoodName, String Category, double Price, String image, int qty, String CustName, int tableNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(cart_col_fName, FoodName);
        contentValues.put(cart_col_category, Category);
        contentValues.put(cart_col_price, Price);
        contentValues.put(cart_col_image, image);
        contentValues.put(cart_col_qty, qty);
        contentValues.put(col_userName, CustName);
        contentValues.put(col_tableNO, tableNo);

        long res = db.insert(order_tableName, null, contentValues);
        db.close();
        Log.e("Cart res", String.valueOf(res));
        return res;
    }

}

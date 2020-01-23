package com.example.hotelmenu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmenu.Admin.AdminDashboard;
import com.example.hotelmenu.Database.ProjectDatabase;
import com.example.hotelmenu.User.UserDashboardActivity;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button login, register;
    String strUsername, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ProjectDatabase projectDatabase = new ProjectDatabase(LoginActivity.this);
                    strUsername = username.getText().toString();
                    strPassword = password.getText().toString();
                    if (strUsername.contains("admin") && strPassword.contains("678")) {
                        startActivity(new Intent(LoginActivity.this, AdminDashboard.class));
                    }
                    Log.e(strUsername + " -- ", strPassword);
                    boolean res = projectDatabase.checkUser(strUsername, strPassword);
                    // Log.e("Result", String.valueOf(res));
                    if (res) {
                        Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                        intent.putExtra("Username", strUsername);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("Error");
                        builder.setMessage("Login Error");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }


                } catch (Exception e) {
                    Log.e("Login Exception", e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }


            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}

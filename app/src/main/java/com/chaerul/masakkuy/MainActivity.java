package com.chaerul.masakkuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseRef;
    boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getSharedPreferences("app", Context.MODE_PRIVATE).getString("username",  "") != "" && getSharedPreferences("app", Context.MODE_PRIVATE).getString("password",  "") != "") {
                    startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                }
                else startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                finish();
            }
        }, 2000L);
    }

    private void login(String username, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("users");
        Query checkUser = databaseRef.orderByKey().equalTo(username);

        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    toast(dataSnapshot.child(username).child("password").getValue().toString().equals(password) + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void newActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
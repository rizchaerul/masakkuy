package com.chaerul.masakkuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().hide();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("username", null).apply();
        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("password", null).apply();
    }

    public void login(View view) {
        String username = this.username.getText() + "";
        String password = this.password.getText() + "";

        tryLogin(username, password);
    }

    public void tryLogin(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Tidak boleh kosong!", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("users");

        Query checkUser = databaseRef.orderByKey().equalTo(username);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.child(username).child("password").getValue().toString().equals(password)) {
                        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("username", username).apply();
                        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("password", password).apply();

                        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else {
                        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("username", null).apply();
                        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("password", null).apply();

                        toast("Password salah!");
                    }
                }
                else toast("Username belum terdaftar!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
package com.chaerul.masakkuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    EditText username, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().hide();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
    }

    public void register(View view) {
        String username = this.username.getText() + "";
        String password = this.password.getText() + "";
        String confirmPassword = this.confirmPassword.getText() + "";

        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Tidak boleh kosong!", Toast.LENGTH_LONG).show();
            return;
        }

        else if(!password.contentEquals(confirmPassword)) {
            Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users/" + username);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "Username sudah pernah terdaftar!", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseRef.child("password").setValue(password);

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

    }
}
package com.chaerul.masakkuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CategoryActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            startActivity(intent);
            finishAffinity();
        }

        if(item.getItemId() == R.id.logout) {
            getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("username", null).apply();
            getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("password", null).apply();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.masakkuylogored);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public void search(View view) {
        EditText key = findViewById(R.id.searchText);
        App.search(key.getText().toString(), this);
    }

    public void raya(View view) {
        App.searchCategory("masakan-hari-raya", this);
    }

    public void siang(View view) {
        App.searchCategory("makan-siang", this);
    }

    public void seafood(View view) {
        App.searchCategory("resep-seafood", this);
    }

    public void daging(View view) {
        App.searchCategory("resep-daging", this);
    }
}
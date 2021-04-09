package com.chaerul.masakkuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chaerul.masakkuy.adapter.RecipesAdapter;
import com.chaerul.masakkuy.api.ApiClient;
import com.chaerul.masakkuy.api.ApiService;
import com.chaerul.masakkuy.model.Result;
import com.chaerul.masakkuy.api.SearchResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainMenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecipesAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home) {
//            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
//            startActivity(intent);
//            finish();
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
        setContentView(R.layout.activity_main_menu);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.masakkuylogored);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);

        adapter = new RecipesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addData();
    }

    public void addData() {
        Retrofit retrofit = ApiClient.getRetrofit();
        ApiService service = retrofit.create(ApiService.class);
        Call<SearchResponse> call = service.newRecipes();

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                if(!response.toString().contains("code=502")) {
                    List<Result> listRecipes = response.body().getResults();
                    if(adapter.getItemCount() == 0) {
                        adapter.setListResult(listRecipes);
                        if(listRecipes.size() == 0) addData();
//                        System.out.println(listRecipes.size());
                    }
                }
                else {
                    addData();
                }

                System.out.println(response.toString());
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void search(View view) {
        EditText key = findViewById(R.id.searchText);
        App.search(key.getText().toString(), this);
    }

    public void category(View view) {
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        startActivity(intent);
    }

    public void catatan(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("key", "-");
        intent.putExtra("title", "Catatan");
        startActivity(intent);
    }

    //    private void test() {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference databaseRef = database.getReference("users");
//
//        String username = getSharedPreferences("app", Context.MODE_PRIVATE).getString("username",  "");
//        Query checkUser = databaseRef.orderByKey().equalTo(username);
//
//        checkUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                  ArrayList<String> keys = new ArrayList<String>();
//
//                    for(DataSnapshot child : dataSnapshot.child(username).child("savedRecipes").getChildren()) {
//                        keys.add(child.getValue().toString());
////                        System.out.println();
//                    }
//
////                    addData();
////                    System.out.println(dataSnapshot.child(username).getValue().toString());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
}
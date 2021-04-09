package com.chaerul.masakkuy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.chaerul.masakkuy.adapter.RecipesAdapter;
import com.chaerul.masakkuy.api.ApiClient;
import com.chaerul.masakkuy.api.ApiService;
import com.chaerul.masakkuy.api.RecipeResponse;
import com.chaerul.masakkuy.api.SearchResponse;
import com.chaerul.masakkuy.model.Recipe;
import com.chaerul.masakkuy.model.Result;
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

public class SearchActivity extends AppCompatActivity {

    TextView title, key;
    RecyclerView recyclerView;
    RecipesAdapter adapter;
    boolean isError;

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
        setContentView(R.layout.activity_search);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.masakkuylogored);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        title = findViewById(R.id.judul);
        key = findViewById(R.id.key);

        title.setText(getIntent().getStringExtra("title"));
        key.setText('"' + getIntent().getStringExtra("key").replace("-", " ") + '"');

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new RecipesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        isError = true;
//        adapter.setListResult(null);
//        new Thread()
//        {
//            @Override
//            public void run()
//            {
//                while(isError == true) {
//                    addData();
//
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();

        if(getIntent().getStringExtra("title").equals("Catatan")) {
            requestDataSaved();
            key.setVisibility(View.GONE);
        }
        else addData();

//        requestDataSaved();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(getIntent().getStringExtra("title").equals("Catatan")) {
            requestDataSaved();
        }
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
////        if(getIntent().getStringExtra("title").equals("Catatan")) {
////            adapter.
//            adapter.setListResult(new ArrayList<Result>());
//            requestDataSaved();
////        }
//
//
//    }

    public void addData() {
        Retrofit retrofit = ApiClient.getRetrofit();
        ApiService service = retrofit.create(ApiService.class);

        Call<SearchResponse> call = null;

        if(getIntent().getStringExtra("title").equals("Hasil Pencarian")){
            call = service.searchRecipes(getIntent().getStringExtra("key"));
        }
        else {
            call = service.getCategory(getIntent().getStringExtra("key"));
        }


        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {

                if(!response.toString().contains("code=502")) {
                    List<Result> listRecipes = response.body().getResults();
                    if(adapter.getItemCount() == 0) {
                        adapter.setListResult(listRecipes);
                        isError = false;
                        System.out.println(listRecipes.size());
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

    private void addDataSaved(String key) {
        Retrofit retrofit = ApiClient.getRetrofit();
        ApiService service = retrofit.create(ApiService.class);

        Call<RecipeResponse> call = service.getRecipe(key);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                Recipe recipe = response.body().getResults();
                if(!response.toString().contains("code=502")) {
                    Result result = new Result();

                    result.setKey(key);
                    result.setThumb(recipe.getThumb());
                    result.setTitle(recipe.getTitle());
                    result.setTimes(recipe.getTimes());

//                    System.out.println(result.getKey());
//                    System.out.println(result.getThumb());
//                    System.out.println(result.getTitle());
//                    System.out.println(recipe.getTimes());

                    adapter.addListResult(result);
                }
                else addDataSaved(key);

                System.out.println(response.toString());
                System.out.println(response.body().getResults().getTitle());
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void requestDataSaved() {
        System.out.println("tes");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("users");

        String username = getSharedPreferences("app", Context.MODE_PRIVATE).getString("username",  "");
        Query checkUser = databaseRef.orderByKey().equalTo(username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> keys = new ArrayList<String>();

                    for(DataSnapshot child : dataSnapshot.child(username).child("savedRecipes").getChildren()) {
                        keys.add(child.getValue().toString());
//                        addDataSaved(child.getValue().toString());
//                        System.out.println("child: " + child.getValue().toString());
                    }

                    bridge(keys);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void bridge(ArrayList<String> strings) {
        adapter.empty();

        for(String string : strings) {
            addDataSaved(string);
        }
    }

    public void search(View view) {
        EditText key = findViewById(R.id.searchText);
        App.search(key.getText().toString(), this);
    }
}
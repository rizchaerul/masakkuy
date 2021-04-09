package com.chaerul.masakkuy;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaerul.masakkuy.api.ApiClient;
import com.chaerul.masakkuy.api.ApiService;
import com.chaerul.masakkuy.api.RecipeResponse;
import com.chaerul.masakkuy.api.SearchResponse;
import com.chaerul.masakkuy.model.Recipe;
import com.chaerul.masakkuy.model.Result;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecipeActivity extends AppCompatActivity {

    TextView deskripsi, komposisi, langkah;
    String key;
    ImageView deskripsiInd, komposisiInd, langkahInd;
    TextView deskripsiText, komposisiText, langkahText, kesulitan;
    Recipe recipe;
    FloatingActionButton bookmark;
    LinearLayout deskripsiLay;
    ArrayList<String> saves;

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
        setContentView(R.layout.activity_recipe);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.masakkuylogored);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        key = getIntent().getStringExtra("key");

        bookmark = findViewById(R.id.bookmark);
        saves = new ArrayList<String>();
        getData();
//        if(isSaved()) bookmark.setImageResource(R.drawable.bookmark_filled);

        deskripsi = findViewById(R.id.deskripsi);
        komposisi = findViewById(R.id.komposisi);
        langkah = findViewById(R.id.langkah);

        deskripsiInd = findViewById(R.id.deskripsiInd);
        komposisiInd = findViewById(R.id.komposisiInd);
        langkahInd = findViewById(R.id.langkahInd);

        deskripsiText = findViewById(R.id.deskripsiText);
        komposisiText = findViewById(R.id.komposisiText);
        langkahText = findViewById(R.id.langkahText);

        deskripsiText.setVisibility(View.VISIBLE);
        komposisiText.setVisibility(View.GONE);
        langkahText.setVisibility(View.GONE);

        deskripsi.setTypeface(Typeface.DEFAULT_BOLD);
        deskripsiInd.setVisibility(View.VISIBLE);
        komposisiInd.setVisibility(View.GONE);
        langkahInd.setVisibility(View.GONE);

        deskripsiLay = findViewById(R.id.deskripsiLay);
        kesulitan = findViewById(R.id.kesulitan);

        recipe = null;
        requestData();
    }

    public void requestData() {
        if(key == "") return;


        Retrofit retrofit = ApiClient.getRetrofit();
        ApiService service = retrofit.create(ApiService.class);

        Call<RecipeResponse> call = service.getRecipe(key);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {

                if(!response.toString().contains("code=502")) {
                    recipe = response.body().getResults();

                    addContent();
                }
                else requestData();

                System.out.println(response.toString());
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    public void addContent() {
        TextView key = findViewById(R.id.key);
        key.setText(capitalizeWord(this.key.replace("-", " ")).replace("Resep ", ""));

        TextView author = findViewById(R.id.author);
        author.setText("oleh " + recipe.getAuthor().getUser());

        Glide.with(this)
                .load(recipe.getThumb())
                .into((ImageView) findViewById(R.id.thumb));

        for(String step : recipe.getStep()) {
            langkahText.setText(langkahText.getText() + step + "\n\n");
        }

        for(String ingridient : recipe.getIngredient()) {
            komposisiText.setText(komposisiText.getText() + "\u2022 " + ingridient + "\n\n");
        }

        deskripsiText.setText(recipe.getDesc().replaceAll("([.])([A-Z])", "$1\n\n$2"));
        kesulitan.setText(recipe.getDificulty());
    }

    public void deskripsi(View view) {
        deskripsi.setTypeface(Typeface.DEFAULT_BOLD);
        komposisi.setTypeface(Typeface.DEFAULT);
        langkah.setTypeface(Typeface.DEFAULT);

        deskripsiInd.setVisibility(View.VISIBLE);
        komposisiInd.setVisibility(View.GONE);
        langkahInd.setVisibility(View.GONE);

        deskripsiLay.setVisibility(View.VISIBLE);
        komposisiText.setVisibility(View.GONE);
        langkahText.setVisibility(View.GONE);
    }

    public void komposisi(View view) {
        deskripsi.setTypeface(Typeface.DEFAULT);
        komposisi.setTypeface(Typeface.DEFAULT_BOLD);
        langkah.setTypeface(Typeface.DEFAULT);

        deskripsiInd.setVisibility(View.GONE);
        komposisiInd.setVisibility(View.VISIBLE);
        langkahInd.setVisibility(View.GONE);

        deskripsiLay.setVisibility(View.GONE);
        komposisiText.setVisibility(View.VISIBLE);
        langkahText.setVisibility(View.GONE);
    }

    public void langkah(View view) {
        deskripsi.setTypeface(Typeface.DEFAULT);
        komposisi.setTypeface(Typeface.DEFAULT);
        langkah.setTypeface(Typeface.DEFAULT_BOLD);

        deskripsiInd.setVisibility(View.GONE);
        komposisiInd.setVisibility(View.GONE);
        langkahInd.setVisibility(View.VISIBLE);

        deskripsiLay.setVisibility(View.GONE);
        komposisiText.setVisibility(View.GONE);
        langkahText.setVisibility(View.VISIBLE);
    }

    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("users");

        String username = getSharedPreferences("app", Context.MODE_PRIVATE).getString("username",  "");
        Query checkUser = databaseRef.orderByKey().equalTo(username);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> saved = new ArrayList<String>();
                    for(DataSnapshot child : dataSnapshot.child(username).child("savedRecipes").getChildren()) {
                        if(child.getValue().toString().equals(key)) {
                            bookmark.setImageResource(R.drawable.bookmark_filled);
                        }
                        saved.add(child.getValue().toString());
                    }

                    saves = saved;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public boolean isSaved() {
        for(String save : saves) {
            if(save.equals(key)) return true;
        }
        return false;
    }

    public void delete() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("users").child(getSharedPreferences("app", Context.MODE_PRIVATE).getString("username",  ""));

        databaseRef = databaseRef.child("savedRecipes");

//        for(String save : saves) {
//            if(save.equals(key)) {
//                saves.remove(save);
//            }
//        }

        for(int i = 0; i < saves.size(); i++) {
            if(saves.get(i).equals(key)) {
                saves.remove(i);
            }
        }

        databaseRef.setValue(saves);
    }

    public void add() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("users").child(getSharedPreferences("app", Context.MODE_PRIVATE).getString("username",  ""));

        databaseRef = databaseRef.child("savedRecipes");

        saves.add(key);
        databaseRef.setValue(saves);
    }

    public void save(View view) {
        if(isSaved()) {
            bookmark.setImageResource(R.drawable.bookmark);
            delete();
            Toast.makeText(this, "Resep berhasil dihapus!", Toast.LENGTH_SHORT).show();
        }
        else {
            add();
            Toast.makeText(this, "Resep berhasil disimpan!", Toast.LENGTH_SHORT).show();
            bookmark.setImageResource(R.drawable.bookmark_filled);
        }

    }

    public void search(View view) {
        EditText key = findViewById(R.id.searchText);
        App.search(key.getText().toString(), this);
    }

    private String capitalizeWord(String str){
        String words[]=str.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }
}
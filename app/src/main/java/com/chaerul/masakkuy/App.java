package com.chaerul.masakkuy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class App {
    public static void search(String key, Context context) {
        if(key.isEmpty()) return;
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("title", "Hasil Pencarian");
        context.startActivity(intent);
    }

    public static void searchCategory(String key, Context context) {
        if(key.isEmpty()) return;
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("title", "Kategori");
        context.startActivity(intent);
    }

    public static void logout(Context context) {
        context.getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("username", null).apply();
        context.getSharedPreferences("app", Context.MODE_PRIVATE).edit().putString("password", null).apply();

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

        Activity activity = (Activity) context;
        activity.finish();
    }
}

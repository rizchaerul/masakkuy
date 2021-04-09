package com.chaerul.masakkuy.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chaerul.masakkuy.R;
import com.chaerul.masakkuy.RecipeActivity;
import com.chaerul.masakkuy.model.Result;

import java.util.ArrayList;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    ArrayList<Result> results;

    public RecipesAdapter() {
        results = new ArrayList<Result>();
    }

    public void setListResult(List<Result> results) {
        if(results != null) {
            this.results.addAll(results);
            notifyDataSetChanged();
        }
    }

    public void empty() {
        results = new ArrayList<Result>();
        notifyDataSetChanged();
    }

    public void addListResult(Result result) {
        for(Result cek : results) {
            if(cek.getKey().equals(result.getKey())) return;
        }

        this.results.add(result);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(results.get(position).getTitle());
        holder.key.setText(capitalizeWord(results.get(position).getKey().replace('-', ' ').replace("resep ", "")));
        holder.difficulty.setText(results.get(position).getTimes().replaceAll( "(\\d)([A-Za-z])", "$1 $2" ));

        Result result = results.get(position);
        if(result.getThumb() == null) {
            Glide.with(holder.title.getContext())
                    .load("https://lh3.googleusercontent.com/proxy/h8cDoGHJC7UoAS-rL42RJj9G1TujtlPGFjA99a9lN4G1G3D-hMemreceqmACp6EW57sstjD_iGizAiE-n81cL3S_zxYZkUGej654SlG63WzVmVg")
                    .into(holder.thumb);
        }
        else {
            Glide.with(holder.title.getContext())
                    .load(result.getThumb())
                    .into(holder.thumb);
        }

    }

    @Override
    public int getItemCount() {
        return results.size();
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, key, difficulty;
        ImageView thumb;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            thumb = itemView.findViewById(R.id.thumb);
            key = itemView.findViewById(R.id.key);
            difficulty = itemView.findViewById(R.id.difficulty);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), RecipeActivity.class);
            intent.putExtra("key", results.get(getAdapterPosition()).getKey());
            view.getContext().startActivity(intent);
        }
    }
}

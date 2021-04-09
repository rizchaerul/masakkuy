package com.chaerul.masakkuy.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("search/")
    Call<SearchResponse> searchRecipes(
            @Query(value="q") String parameter
    );

    @GET("recipes/")
    Call<SearchResponse> newRecipes(
    );

    @GET("recipe/{key}")
    Call<RecipeResponse> getRecipe(
            @Path("key") String key
    );

    @GET("categorys/recipes/{key}")
    Call<SearchResponse> getCategory(
            @Path("key") String key
    );

}

package com.chaerul.masakkuy.api;

import com.chaerul.masakkuy.model.Recipe;

public class RecipeResponse {

    private String method;
    private Boolean status;
    private Recipe results;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Recipe getResults() {
        return results;
    }

    public void setResults(Recipe results) {
        this.results = results;
    }

}

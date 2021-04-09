package com.chaerul.masakkuy.api;

import com.chaerul.masakkuy.model.Result;

import java.util.List;

public class SearchResponse {

    private String method;
    private Boolean status;
    private List<Result> results = null;

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

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

}
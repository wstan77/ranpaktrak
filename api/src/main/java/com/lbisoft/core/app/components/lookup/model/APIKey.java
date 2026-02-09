package com.lbisoft.core.app.components.lookup.model;

public class APIKey {
    private String type;
    private String apiKey;

    public APIKey(String type, String apiKey) {
        this.type = type;
        this.apiKey = apiKey;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}

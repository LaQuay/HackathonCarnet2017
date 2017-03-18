package com.jfem.hackathoncarnet.carnethackathon.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Venue {

    private String id;
    private String name;
    private JSONObject location;
    private JSONArray categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject getLocation() {
        return location;
    }

    public void setLocation(JSONObject location) {
        this.location = location;
    }

    public JSONArray getCategories() {
        return categories;
    }

    public void setCategories(JSONArray categories) {
        this.categories = categories;
    }
}

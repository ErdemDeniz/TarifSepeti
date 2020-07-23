package com.yjs3408.tarifsepeti.model;

import java.io.Serializable;
import java.util.Date;

public class Recipe implements Serializable {

    private String id;
    private String name;
    private Date createdTime;
    private String category;
    private String ingredients;
    private String description;
    private String url;

    public Recipe() {
    }

    public Recipe(String id, String name, Date createdTime, String category, String ingredients, String description, String url) {
        this.id = id;
        this.name = name;
        this.createdTime = createdTime;
        this.category = category;
        this.ingredients = ingredients;
        this.description = description;
        this.url = url;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Date getCreatedTime() { return createdTime; }

    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getIngredients() { return ingredients; }

    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {
        return "Recipe{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdTime=" + createdTime +
                ", category='" + category + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

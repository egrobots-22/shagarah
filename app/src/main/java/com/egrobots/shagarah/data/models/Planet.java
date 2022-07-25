package com.egrobots.shagarah.data.models;

public class Planet {

    private String image;
    private String name;

    public Planet() {
    }

    public Planet(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

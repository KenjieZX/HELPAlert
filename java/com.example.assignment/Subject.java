package com.example.assignment;

public class Subject {
    private String name;
    private String imageUrl;
    private String id;
    private String code;

    public Subject() {
        // Required empty constructor for Firebase
    }


//    public Subject(String name, String imageUrl, String code, String id) {
//        this.id = id;
//        this.name = name;
//        this.imageUrl = imageUrl;
//        this.code = code;
//    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

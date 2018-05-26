package com.example.hitesh.dexfileexplorer;

/**
 * Created by hitesh on 3/6/2018.
 */

public class fileItem {
    String name,path,date,data,image,type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public fileItem(String name, String path, String date, String data, String image, String type) {
        this.name = name;
        this.path = path;
        this.date = date;
        this.data = data;
        this.image = image;
        this.type=type;
    }


    public String getName() {
        return name;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

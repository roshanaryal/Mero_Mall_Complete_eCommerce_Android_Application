package com.roshanaryal.meromall.Model;

public class ProductsModal {
    private String pname,description,price,image,category,pid,date,time;

    public ProductsModal() {
    }

    public ProductsModal(String pname, String description, String price, String image, String category, String pid, String date, String time) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.pid = pid;
        this.date = date;
        this.time = time;
    }

    public String getPname() {
        return pname;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public String getPid() {
        return pid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}

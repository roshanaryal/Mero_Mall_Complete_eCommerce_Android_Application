package com.roshanaryal.meromall.Model;

public class CartModel {
    private String pid,pname,price,quantity,discount;

    public CartModel() {
    }

    public CartModel(String pid, String pname, String price, String quantity, String discount) {
        this.pid = pid;
        this.pname = pname;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    public String getPid() {
        return pid;
    }

    public String getPname() {
        return pname;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDiscount() {
        return discount;
    }
}

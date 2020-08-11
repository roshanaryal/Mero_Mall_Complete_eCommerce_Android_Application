package com.roshanaryal.meromall.Model;

public class OrderModal {
    private String adress,city,date,name,phone,state,time,totalAmount;

    public OrderModal()
    {

    }

    public OrderModal(String adress, String city, String date, String name, String phone, String state, String time, String totalAmount) {
        this.adress = adress;
        this.city = city;
        this.date = date;
        this.name = name;
        this.phone = phone;
        this.state = state;
        this.time = time;
        this.totalAmount = totalAmount;
    }

    public String getAdress() {
        return adress;
    }

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getState() {
        return state;
    }

    public String getTime() {
        return time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }
}

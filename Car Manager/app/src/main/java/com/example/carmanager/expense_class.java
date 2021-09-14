package com.example.carmanager;

public class expense_class {
    String type,cost,url,odometer;

    public expense_class(String type, String cost, String url, String odometer) {
        this.type = type;
        this.cost = cost;
        this.url = url;
        this.odometer = odometer;
    }

    public String getType() {
        return type;
    }


    public String getCost() {
        return cost;
    }

    public String getUrl() {
        return url;
    }

    public String getOdometer() {
        return odometer;
    }
}

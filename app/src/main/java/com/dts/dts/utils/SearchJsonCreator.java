package com.dts.dts.utils;

/**
 * Created by Android Dev E5550 on 11/25/2016.
 */

public class SearchJsonCreator {
    private String jsonString = "";

    public void addJsonItem(String field, String operator, String value) {
        if (!jsonString.isEmpty()) {
            jsonString += ",";
        }
        jsonString += "{\"field\":\"" + field + "\"," +
                "\"operator\":\"" + operator + "\"," +
                "\"value\":\"" + value + "\"}";
    }

    public String jsonString(){
        return jsonString;
    }

    public String createJson() {
        return "{\"criteria\":[" + jsonString + "]}";
    }

    public void clearAll() {
        jsonString = "";
    }

    public void addJsonItemAsArray(String field, String operator, String value) {
        if (!jsonString.isEmpty()) {
            jsonString += ",";
        }
        jsonString += "{\"field\":\"" + field + "\"," +
                "\"operator\":\"" + operator + "\"," +
                "\"value\":" + value + "}";
    }
}

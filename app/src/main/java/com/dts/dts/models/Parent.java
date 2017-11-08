package com.dts.dts.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by silver on 12/23/16.
 */

public class Parent {
    public int State;
    public ArrayList<JSONObject> childs;
    public JSONObject dict;
    public JSONObject dictProperty;

    public static int Collapsed = 0;
    public static int Expanded = 1;
}

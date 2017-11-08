package com.dts.dts.remote;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;


public abstract class JsonResponseListener implements ErrorListener {

    public abstract void onErrorResponse(VolleyError error);

    public abstract void onResponse(JsonObject response);

}

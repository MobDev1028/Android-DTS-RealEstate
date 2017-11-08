
package com.dts.dts.remote;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.dts.dts.Constants;
import com.dts.dts.utils.Helper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class JsonRequest extends Request<JsonObject> {

    private static final String TAG = JsonRequest.class.getSimpleName();

    //private static final String BASE_URL = "http://developers.swaamlabs.com/leo-app/api/";

    private JsonResponseListener listener;

    private boolean isFocusOnEditTextUsername = false;

    private Map<String, String> headers;

    private JsonObject params;

    public JsonRequest(String action, JsonResponseListener listener) {
        super(Method.GET, createUrl(action), listener);
        this.listener = listener;
        this.headers = null;

        setTimeout(20 * 1000); //20 seconds - change to what you want
    }

    public JsonRequest(JsonResponseListener listener) {
        super(Method.GET, createLocalHostUrl(), listener);
        this.listener = listener;
        this.headers = null;

        setTimeout(20 * 1000); //20 seconds - change to what you want
    }

    public JsonRequest(String action, JsonObject params, JsonResponseListener listener) {
        super(Method.POST, createUrl(action), listener);
        this.listener = listener;
        this.params = params;

        setTimeout(20 * 1000); //20 seconds - change to what you want
    }

    public JsonRequest(int method, String action, JsonObject params, JsonResponseListener listener) {
        super(method, createUrl(action), listener);
        this.listener = listener;
        this.params = params;

        setTimeout(20 * 1000); //20 seconds - change to what you want
    }

    public JsonRequest(String action, Map<String, String> headers, JsonObject params, JsonResponseListener listener) {
        super(Method.POST, createUrl(action), listener);
        this.listener = listener;
        this.headers = headers;
        this.params = params;
    }

    public JsonRequest(int method, String action, Map<String, String> headers, JsonObject params, JsonResponseListener listener) {
        super(method, createUrl(action), listener);
        this.listener = listener;
        this.headers = headers;
        this.params = params;
    }

    private static String createUrl(String action) {
        String url = Constants.API_BASEURL + action;
        Log.i(JsonRequest.class.getSimpleName(), "Request URL: " + url);
        //String url = "http://10.0.0.107/twoity/rest/" + module + "/" + action;
        return url;
    }

    private static String createLocalHostUrl() {
        String url = "http://10.0.0.102:8012/learn-ci/index.php/api/USER/login";
        Log.i(JsonRequest.class.getSimpleName(), "Request URL: " + url);
        //String url = "http://10.0.0.107/twoity/rest/" + module + "/" + action;
        return url;
    }

    private void setTimeout(int timeout) {
        RetryPolicy policy = new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.setRetryPolicy(policy);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        Log.i(TAG, "Request params: " + params.toString());
        return params.toString().getBytes();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Accept", "application/json");
        return headers;
    }

    @Override
    protected Response<JsonObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d(TAG, "Raw network response : " + jsonString);
            Gson gson = new GsonBuilder().create();
            JsonObject result = gson.fromJson(jsonString, JsonObject.class);
            //every time it should be setup by dev....
            if (result.get("success").getAsBoolean()) {
                return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
            } else if (result.get("message") != null) {
                return Response.error(new VolleyError(result.get("message").getAsString()));
            } else {
                return Response.error(new VolleyError("Oops, Something went wrong!"));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JsonObject response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        listener.onErrorResponse(error);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(new VolleyError(Helper.getErrorAlert(volleyError)));
    }
}

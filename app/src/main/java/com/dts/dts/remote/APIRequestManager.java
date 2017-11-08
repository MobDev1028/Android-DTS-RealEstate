
package com.dts.dts.remote;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.dts.dts.App;
import com.dts.dts.Constants;
import com.dts.dts.library.HTTPUtils;
import com.dts.dts.models.UserGeneralInfo;
import com.dts.dts.utils.LocalPreferences;
import com.dts.dts.views.CustomDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import static com.dts.dts.remote.APIRequestManager.APIRequestType.PROPERTY_LIST;

public class APIRequestManager {
    private static final String TAG = APIRequestManager.class.getSimpleName();
    private static final int SHOW_PROGRESS_DIALOG = 123;
    private static final int HIDE_PROGRESS_DIALOG = 124;
    private static final String DEFAULT_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIwLCJpc3MiOiJodHRwOlwvXC9hZG1pbmR0cy5sb2NhbGhvc3QuY29tXC9vbGRhcGlcL2F1dGhlbnRpY2F0ZSIsImlhdCI6MTQ2MzkzMzg4NSwiZXhwIjoxNTU3MjQ1ODg1LCJuYmYiOjE0NjM5MzM4ODUsImp0aSI6IjJkOGY4YWE3YzU5MWRmYmVkOTAxODE2ZmRiYmU3ZWFkIn0.uPteNq6R9e35rBFuy6UmjNOXL0VJoaehk_OPqHWtFhE";
    private JsonRequest pendingRequest;
    private Handler mUIHandler;
    private boolean shouldHideProgress = true;


    public boolean hideProgress()
    {
        return shouldHideProgress;
    }

    private String getToken() {
        String token = LocalPreferences.getUserToken(mContext);
        if (token.isEmpty())
            token = DEFAULT_TOKEN;
        return token;
    }

    public enum APIRequestType {
        LOGIN, PROPERTY_LIST, CREATE_USER_SEARCH, SEARCH_RESULTS, GET_DURATION, REQUEST_PIN, REGISTER_USER, LIKE_PROPERTY, INQUIRE_PROPERTY, GET_USER_GENERAL, SAVE_USER_GENERAL, SUGGESTIONS

    }


    public interface APIRequestListener {
        void onNetworkRequestSuccess(APIRequestType type, JsonObject response);

        void onNetworkRequestError(APIRequestType type, VolleyError error);
    }

    private Context mContext;

    private APIRequestListener listener;

    private ProgressDialog mProgressDialog;

    public APIRequestManager(Context mContext, APIRequestListener listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    public void setShouldHideProgress(boolean shouldHideProgress) {
        this.shouldHideProgress = shouldHideProgress;
    }

    public void getSearchAgents(final Handler myHandler){
        final String actionWithParams = Constants.API_BASEURL + "getsearchagents"+
                "?token=" + getToken();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.SEARCH_AGENTS;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();

    }

    public void saveSearchAgent(final Handler myHandler, String userInfo, int searchId, int disabled, String name){
         final String actionWithParams = Constants.API_BASEURL + "editsearchagent";

        final JsonObject params = new JsonObject();
        params.addProperty("token", getToken());
        params.addProperty("search_agent_id", searchId);
        params.addProperty("disabled", disabled);
        params.addProperty("search_data", userInfo);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPPost(actionWithParams, "", params);
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.EDIT_AGENTS;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();

    }

    public JsonRequest getPropertyList(final Handler myHandler, double latitude, double longitude, int page) {

        final String actionWithParams = Constants.API_BASEURL + "getproperty" +
                "?token=" + getToken() +
                "&page="+page+"&show_owned_only=0&show_active_only=1" +
                "&latitude=" + latitude + "&longitude=" + longitude;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.PROPERTY_LIST;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest getMyPropertyList(final Handler myHandler) {

        final String actionWithParams = Constants.API_BASEURL + "getproperty" +
                "?token=" + getToken() +
                "&page=1"+
                "&show_owned_only=1"+
                "&show_active_only=0" +
                "&show_reviewed_only=0";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.PROPERTY_LIST;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }


    public JsonRequest getTermsOfService(final Handler myHandler, double latitude, double longitude, int page) {

        final String actionWithParams = Constants.API_BASEURL + "tos" +
                "?token=" + getToken();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.TERMS_OF_SERVICE;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest getMessageList(final Handler myHandler, final String msgId) {

        String actionWithParams = Constants.API_BASEURL + "getmsg" +
                "?token=" + getToken() +
                "&type=thread&paginated=0&page=1&archived=0";

        if (msgId != null && !msgId.isEmpty())
        {
            actionWithParams = Constants.API_BASEURL + "getmsg" +
                    "?token=" + getToken() +
                    "&msgId=" + msgId +
                    "&type=thread&paginated=0&page=1&archived=0";
        }
        final String finalActionWithParams = actionWithParams;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(finalActionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.MESSAGE_LIST;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }



    public JsonRequest getFavoriteList(final Handler myHandler) {

        final String actionWithParams = Constants.API_BASEURL + "getuserfav" +
                "?token=" + getToken();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.FAVORITE_LIST;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }



    public String getDoccontent(final Handler myHandler, String filename, String title, String template) {

        String actionWithParams = Constants.API_BASEURL + "getdoccontent" +
                "?token=" + getToken() +
                "&filename=" + filename +
                "&type=" + template +
                "&title=" + title;

        try {
            actionWithParams = URLEncoder.encode(actionWithParams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return actionWithParams;
//        final String finalActionWithParams = actionWithParams;
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String response = HTTPUtils.HTTPGetWithToken(finalActionWithParams, "");
//                Message msg = myHandler.obtainMessage();
//                msg.arg1 = Constants.DOC_CONTENT;
//
//                JSONObject json = null;
//                try {
//                    json = new JSONObject(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                msg.obj = json;
//
//                myHandler.sendMessage(msg);
//            }
//        });
//        thread.start();
//        return null;
    }
    public JsonRequest getPrivacy(final Handler myHandler) {

        final String actionWithParams = Constants.API_BASEURL + "privacy" +
                "?token=" + getToken();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.TERMS_OF_SERVICE;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest getTermsOfService(final Handler myHandler) {

        final String actionWithParams = Constants.API_BASEURL + "tos" +
                "?token=" + getToken();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.TERMS_OF_SERVICE;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest getUpdateMsg(final Handler myHandler, String messageId, String action) {

        final String actionWithParams = Constants.API_BASEURL + "updatemsg" +
                "?token=" + getToken() +
                "&msg_id=" + messageId +
                "&action=" + action;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.UPDATE_MSG;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest signDoc(final Handler myHandler, String base64, int template_id, String ip) {



        final JsonObject params = new JsonObject();
        params.addProperty("token", getToken());
        params.addProperty("doc_template_id", template_id);
        params.addProperty("signature", base64);
        params.addProperty("ip", ip);

        final String actionWithParams = Constants.API_BASEURL + "signdoc";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPPost(actionWithParams, "", params);
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.SIGN_DOC;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest confirmDocSignature(final Handler myHandler, String docId, String msgId) {

        final String actionWithParams = Constants.API_BASEURL + "confirmdocsignature" +
                "?token=" + getToken() +
                "&doc_id=" + docId +
                "&msg_id=" + msgId;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.CONFIRM_CONFIG;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest createUserSearch(final Handler myHandler, String searchCriteria, String searchSchedule, boolean isSearchAgent) {
        Gson gson = new GsonBuilder().create();
        JsonObject params = new JsonObject();
//        params = gson.fromJson(searchCriteria, JsonObject.class);

        String actionWithParams = Constants.API_BASEURL + "createusersearch" +
                "?token=" + getToken();

        if (searchSchedule.isEmpty()){
            actionWithParams = actionWithParams + "&create_search_agent=0";
            params = gson.fromJson(searchCriteria, JsonObject.class);
        }
        else{
            actionWithParams = actionWithParams + "&create_search_agent=1";
//            searchCriteria = searchCriteria.replace("{\"criteria\":", "");
            searchCriteria = searchCriteria.substring(0, searchCriteria.length()-1);
//            searchSchedule = searchSchedule.replace("{\"schedule\":", "");
            searchSchedule = searchSchedule.substring(1, searchSchedule.length());

            String s = searchCriteria + ", " + searchSchedule;
//            params.addProperty("criteria", searchCriteria);
//            params.addProperty("schedule", searchSchedule);
            params = gson.fromJson(s, JsonObject.class);

        }

//        String s = "{\"criteria\":\"[{\"field\":\"geo\",\"operator\":\"=\",\"value\":\"57.0|12.0|10\"},{\"field\":\"price\",\"operator\":\">=\",\"value\":\"2747\"},{\"field\":\"price\",\"operator\":\"<=\",\"value\":\"4104\"},{\"field\":\"type\",\"operator\":\"in\",\"value\":[\"apt\",\"condo\",\"house\",\"other\"]},{\"field\":\"unit_amen_dishwasher\",\"operator\":\"=\",\"value\":\"1\"},{\"field\":\"unit_amen_laundry\",\"operator\":\"=\",\"value\":\"1\"},{\"field\":\"build_amen_elevator\",\"operator\":\"=\",\"value\":\"1\"},{\"field\":\"build_amen_secure_entry\",\"operator\":\"=\",\"value\":\"1\"}]\",\"schedule":\"{\"frequency\":\"weekly\",\"start\":\"2017-03-14\",\"end\":\"2017-03-21\"}\"}";
        final String finalActionWithParams = actionWithParams;
        final JsonObject finalParams = params;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPPost(finalActionWithParams, "", finalParams);
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.CREATE_USER_SEARCH;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();

        return null;
    }

    public JsonRequest createSupportTicket(final Handler myHandler, String type, String message) {
        Gson gson = new GsonBuilder().create();

        final JsonObject params = new JsonObject();
        params.addProperty("token", getToken());
        params.addProperty("type", type);
        params.addProperty("message", message);

        final String actionWithParams = Constants.API_BASEURL + "createsupportticket";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPPost(actionWithParams, "", params);
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.CREATE_SUPPORT_TICKET;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();

        return null;
    }

    public JsonRequest sendFeedback(final Handler myHandler, String type, String message) {
        Gson gson = new GsonBuilder().create();

        final JsonObject params = new JsonObject();
        params.addProperty("token", getToken());
        params.addProperty("type", type);
        params.addProperty("message", message);

        final String actionWithParams = Constants.API_BASEURL + "createsupportticket";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPPost(actionWithParams, "", params);
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.SEND_FEEDBACK;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();

        return null;
    }

    public JsonRequest sendMessage(final Handler myHandler, double recipientID, String message, double messageID) {
        Gson gson = new GsonBuilder().create();

        final JsonObject params = new JsonObject();
        params.addProperty("token", getToken());
        params.addProperty("recipient_id", recipientID);
        params.addProperty("message", message);
        params.addProperty("parent_msg_id", messageID);

        final String actionWithParams = Constants.API_BASEURL + "sendmsg";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPPost(actionWithParams, "", params);
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.SEND_MESSAGE;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();

        return null;
    }

    public JsonRequest getSearchResults(final Handler myHandler, JSONObject data) {
        String strData = "";
        if (data != null)
            try {
                strData = data.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        final String actionWithParams = Constants.API_BASEURL + "getsearchresults" +
                "?token=" + getToken() +
                "&type=user_searches&search_agent=0" +
                "&key=" + strData;



        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.SEARCH_RESULTS;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;


//        return createJsonGetRequest(APIRequestType.SEARCH_RESULTS, actionWithParams);
    }

    public JsonRequest requestPin(final Handler myHandler, String phoneNumber) {

        final String actionWithParams = Constants.API_BASEURL + "requestpin" +
                "?token=" + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlzcyI6Imh0dHA6XC9cL2R0cy5sb2NhbGhvc3QuY29tXC9hcGlcL2F1dGhlbnRpY2F0ZSIsImlhdCI6MTQ1NjI4MzQ2OSwiZXhwIjoxNTQ5NTk1NDY5LCJuYmYiOjE0NTYyODM0NjksImp0aSI6ImViMWQwNTczMjI5MzkwZGM2MGFmYTJlOWQzNjdkNTJkIn0.2frBoRUv2xdL73g42EY2Jqf8GUiB8YELizcWZELbs9s" +
                "&cid=" + phoneNumber + "&country_code=";

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.REQUEST_PIN;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest registerUser(final Handler myHandler, String phoneNumber, String pinCode) {
        final String actionWithParams = Constants.API_BASEURL + "registeruser" +
                "?token=" + getToken() +
                "&cid=" + phoneNumber +
                "&code=" + pinCode;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    boolean success = json.getBoolean("success");
                    msg.arg1 = Constants.REGISTER_USER;
                    if (success) {
                        msg.obj = json;
                        msg.arg2 = 1;
                    }
                    else{
                        msg.arg2 = 0;
                        msg.obj = json;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;

//        return createJsonGetRequest(APIRequestType.REGISTER_USER, actionWithParams);
    }

    public JsonRequest inquireProperty(final Handler myHandler, String propertyId) {
        //http://api.ditchthe.space/api/inquireproperty?token=\(token)&property_id=\(propertyId)")
        final String actionWithParams = Constants.API_BASEURL + "inquireproperty" +
                "?token=" + getToken() +
                "&property_id=" + propertyId;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.INQUIRE_PROPERTY;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest deleteProperty(final Handler myHandler, String propertyId) {
        //http://api.ditchthe.space/api/inquireproperty?token=\(token)&property_id=\(propertyId)")
        final String actionWithParams = Constants.API_BASEURL + "deleteproperty" +
                "?token=" + getToken() +
                "&property_id=" + propertyId;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.DELETE_PROPERTY;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }

    public JsonRequest hideProperty(final Handler myHandler, String propertyId) {
        //http://api.ditchthe.space/api/inquireproperty?token=\(token)&property_id=\(propertyId)")
        final String actionWithParams = Constants.API_BASEURL + "hideproperty" +
                "?token=" + getToken() +
                "&property_id=" + propertyId;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.HIDE_PROPERTY;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
    }
    public JsonRequest reportProperty(final Handler myHandler, String propertyId) {
        //http://api.ditchthe.space/api/inquireproperty?token=\(token)&property_id=\(propertyId)")
        final String actionWithParams = Constants.API_BASEURL + "createsupportticket" +
                "?token=" + getToken() +
                "&type=reported_listing" +
                "&message=Reported%20listing"+
                "&ip="+ propertyId;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.CREATE_SUPPORT_TICKET;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();

        return null;
    }


    public JsonRequest likeProperty(final Handler myHandler, String propertyId) {
        //http://api.ditchthe.space/api/likeproperty?token=\(token)&property_id=\(propertyId)
        final String actionWithParams = Constants.API_BASEURL + "likeproperty" +
                "?token=" + getToken() +
                "&property_id=" + propertyId;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.LIKE_PROPERTY;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
//        return createJsonGetRequest(APIRequestType.LIKE_PROPERTY, actionWithParams);
    }

    public JsonRequest getUserGeneral(final Handler myHandler) {
        final String actionWithParams = Constants.API_BASEURL + "getusergeneral" +
                "?token=" + getToken();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HTTPUtils.HTTPGetWithToken(actionWithParams, "");
                Message msg = myHandler.obtainMessage();
                msg.arg1 = Constants.GET_USER_GENERAL;

                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                msg.obj = json;

                myHandler.sendMessage(msg);
            }
        });
        thread.start();
        return null;
//        return createJsonGetRequest(APIRequestType.GET_USER_GENERAL, actionWithParams);
    }

    public JsonRequest saveUserGenerals(UserGeneralInfo userInfo) {
        /*String actionWithParams = "saveusergeneral" +
                "?token=" + getToken() +
                "&first_name=" + userInfo.getFirstName() +
                "&last_name=" + userInfo.getLastName() +
                "&email=" + userInfo.getEmail() +
                "&address1=" + userInfo.getAddress1() +
                "&address2=" + userInfo.getAddress2() +
                "&zip=" + userInfo.getZip();*/

        Gson gson = new GsonBuilder().create();
        JsonObject params = gson.fromJson(gson.toJson(userInfo), JsonObject.class);
        params.addProperty("token", getToken());
        return createJsonPostRequest(APIRequestType.SAVE_USER_GENERAL, "saveusergeneral", params);
    }

    public JsonRequest login(String email, String password) {
        JsonObject params = new JsonObject();

        params.addProperty("email", email);
        params.addProperty("password", password);

        //Log.d("result",request.toString());
        return createJsonPostRequest(APIRequestType.LOGIN, "login", params);
    }

    public JsonRequest suggestions(String API_key, String username) {
        JsonObject params = new JsonObject();

        params.addProperty("apiKey", API_key);
        params.addProperty("userName", username);

        JsonRequest request = createJsonPostRequest(APIRequestType.SUGGESTIONS, "userNameCheck", params);

        //Log.d("result",request.toString());
        return request;
    }


    private JsonRequest createJsonPostRequest(final APIRequestType requestType, String action, JsonObject params) {

        return new JsonRequest(action, params, new JsonResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorResponse(requestType, error);
            }

            @Override
            public void onResponse(JsonObject response) {
                networkSuccessResponse(requestType, response);
            }
        });
    }

    private JsonRequest createJsonGetRequest(final APIRequestType requestType, String action) {

        return new JsonRequest(action, new JsonResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorResponse(requestType, error);
            }

            @Override
            public void onResponse(JsonObject response) {
                networkSuccessResponse(requestType, response);
            }
        });
    }


    private JsonRequest createJsonGetRequest(final APIRequestType requestType, String action, final Map<String, String> headers) {

        return new JsonRequest(action, new JsonResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                networkErrorResponse(requestType, error);
            }

            @Override
            public void onResponse(JsonObject response) {
                networkSuccessResponse(requestType, response);
            }
        });
    }

    public void addToRequestQueue(JsonRequest request, boolean showProgessBar) {
        if (!checkInternetServices()) {
            showInternetServicesErrorPopup(showProgessBar);
        } else {
            if (showProgessBar)
                showProgressDialog();
        }
    }

    public boolean checkInternet(boolean showPopup) {
        boolean isOk = false;
        if (!checkInternetServices()) {
            if (showPopup)
                showInternetServicesErrorPopup(true);
        } else
            isOk = true;
        return isOk;
    }

    public void showInternetServicesErrorPopup(final boolean showProgessBar) {
        final CustomDialog errorDialog = new CustomDialog(mContext
                , "Error"
                , "No internet access. please try again later.", "OK"
                , "Retry", CustomDialog.DialogType.INTERNET_CONNECTION_ERROR);
        CustomDialog.CallBackListener listener = new CustomDialog.CallBackListener() {
            @Override
            public void onPositiveButtonPressed(DialogInterface dialog, CustomDialog.DialogType dialogType) {
                dialog.dismiss();
            }

            @Override
            public void onNegativeButtonPressed(DialogInterface dialog, CustomDialog.DialogType dialogType) {
                addToRequestQueue(pendingRequest, showProgessBar);
            }
        };
        errorDialog.setCallBackListener(listener);
        errorDialog.showDialog();
    }

    public void showDefaultPopup(String title, String errorMessage) {
        if (errorMessage == null) {
            errorMessage = "Server is not responding at the moment. Please try again later";
        }
        final CustomDialog dialog = new CustomDialog(mContext, title, errorMessage, mContext.getString(android.R.string.ok), "", CustomDialog.DialogType.DEFAULT_ERROR);
        CustomDialog.CallBackListener listener = new CustomDialog.CallBackListener() {
            @Override
            public void onPositiveButtonPressed(DialogInterface dialog, CustomDialog.DialogType dialogType) {
                dialog.dismiss();
            }

            @Override
            public void onNegativeButtonPressed(DialogInterface dialog, CustomDialog.DialogType dialogType) {

            }
        };
        dialog.setCallBackListener(listener);
        dialog.showDialog();
    }

    public void showDefaultSuccessPopup(String successMessage) {
        final CustomDialog dialog = new CustomDialog(mContext, "Success!", successMessage, "OK", "", CustomDialog.DialogType.DEFAULT_ERROR);
        CustomDialog.CallBackListener listener = new CustomDialog.CallBackListener() {
            @Override
            public void onPositiveButtonPressed(DialogInterface dialog, CustomDialog.DialogType dialogType) {
                dialog.dismiss();
            }

            @Override
            public void onNegativeButtonPressed(DialogInterface dialog, CustomDialog.DialogType dialogType) {

            }
        };
        dialog.setCallBackListener(listener);
        dialog.showDialog();
    }

    private void networkSuccessResponse(APIRequestType type, JsonObject response) {
        if (shouldHideProgress)
            hideProgressDialog();
        listener.onNetworkRequestSuccess(type, response);
    }

    private void networkErrorResponse(APIRequestType type, VolleyError response) {
        hideProgressDialog();
        listener.onNetworkRequestError(type, response);
    }

    public boolean checkInternetServices() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    /*public boolean checkLocationServices() {
        LocationManager locationManager = null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }*/

    public void showProgressDialog() {

        if (mUIHandler == null)
            mUIHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        if (mProgressDialog == null) {
                            mProgressDialog = new ProgressDialog(mContext);
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.setMessage("Processing...");
                        }
                        if (msg.what == SHOW_PROGRESS_DIALOG) {
                            if (!mProgressDialog.isShowing())
                                mProgressDialog.show();
                        } else {
                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.hide();
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    super.handleMessage(msg);
                }
            };
        mUIHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
    }

    public void destroyProgressDialog()
    {
        if (mProgressDialog != null)
        {
            mProgressDialog.dismiss();
            mProgressDialog = null;

        }
    }

    public void hideProgressDialog() {
        shouldHideProgress = true;
        if (mUIHandler != null)
            mUIHandler.sendEmptyMessage(HIDE_PROGRESS_DIALOG);
    }


    private boolean isErrorMessageNull(VolleyError error) {
        hideProgressDialog();
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //showDefaultErrorPopup("Network Error: " + error.toString());
            return true;
        } else if (error instanceof AuthFailureError) {
            //showDefaultErrorPopup("Authentication Problem: " + error.toString());
            return true;
        } else if (error instanceof ServerError) {
            //showDefaultErrorPopup("Server Problem: " + error.toString());
            return true;
        } else if (error instanceof NetworkError) {
            //showDefaultErrorPopup("Network Problem: " + error.toString());
            return true;
        } else //showDefaultErrorPopup("Parsing Problem: " + error.toString());
            return error instanceof ParseError;
    }
}

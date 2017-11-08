package com.dts.dts;

/**
 * Created by Android Dev E5550 on 11/18/2016.
 */

public interface Constants {
    String API_BASEURL = "https://api.ditchthe.space/api/";
    String EXTRA_PROPERTY_LIST_JSON = "extra_property_list_json";
    String EXTRA_PROPERTY_JSON = "extra_property_json";
    String EXTRA_CURRENT_LOCATION = "extra_current_location";
    String EXTRA_REQUEST_TYPE = "extra_request_type";
    String EXTRA_PROPERTY_ID = "extra_property_id";


    public static final int OK 										= 200;
    public static final int CONNECTION_ERROR 						= 201;
    public static final int SYSTEM_ERROR 							= 202;
    public static final int CONNECT_TIMEOUT 						= 203;
    public static final int READ_TIMEOUT 							= 204;
    public static final int IO_ERROR 								= 205;
    public static final int CLIENT_PROTOCOL_ERROR 					= 206;


    public static final String KEY_STATUS 							= "success";

    public static final int LOGIN = 0;
    public static final int PROPERTY_LIST=1;
    public static final int  CREATE_USER_SEARCH = 2;
    public static final int SEARCH_RESULTS= 3;
    public static final int GET_DURATION=4;
    public static final int REQUEST_PIN=5;
    public static final int REGISTER_USER=6;
    public static final int LIKE_PROPERTY=7;
    public static final int INQUIRE_PROPERTY=8;
    public static final int GET_USER_GENERAL=9;
    public static final int SAVE_USER_GENERAL=10;
    public static final int SUGGESTIONS=11;
    public static final int FAVORITE_LIST = 12;
    public static final int CREATE_SUPPORT_TICKET = 13;
    public static final int HIDE_PROPERTY=14;
    public static final int MESSAGE_LIST = 15;

    public static final int DOC_CONTENT = 16;
    public static final int UPDATE_MSG = 17;
    public static final int CONFIRM_CONFIG = 18;
    public static final int SEND_FEEDBACK = 19;
    public static final int TERMS_OF_SERVICE = 20;
    public static final int SEND_MESSAGE = 21;
    public static final int SIGN_DOC = 22;
    public static final int DELETE_PROPERTY = 23;
    public static final int SEARCH_AGENTS = 24;
    public static final int EDIT_AGENTS = 25;

}

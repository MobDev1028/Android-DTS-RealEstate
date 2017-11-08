package com.dts.dts.library;

import android.util.Log;

import com.google.gson.JsonObject;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class HTTPUtils {
	
	public static String HTTPPostWithToken(String urlAddress, String lan, String token, String... postDataPair) {
		HttpParams myParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(myParams, 30000);
		HttpConnectionParams.setSoTimeout(myParams, 30000);

		DefaultHttpClient hc=new DefaultHttpClient(myParams);
		ResponseHandler <String> res=new BasicResponseHandler();

		HttpPost postMethod=new HttpPost(urlAddress);

		postMethod.addHeader("Accept", "application/json");
		postMethod.addHeader("Accept-language", lan);
		postMethod.setHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.setHeader("Authorization", token);
//		postMethod.setHeader("token", device_token);
		int c = postDataPair.length;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(postDataPair.length / 2);

		String result = urlAddress + " : param {";
		for (int i = 0; i < postDataPair.length; i += 2) {
			nameValuePairs.add(new BasicNameValuePair(postDataPair[i], postDataPair[i + 1]));
			result = result + postDataPair[i] + "=" + postDataPair[i+1] + " , ";
		}

		result += " }";
		Log.d("HTTP REQUEST: ", result);
		String response = "";
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = hc.execute(postMethod,res);
		} catch (UnsupportedEncodingException e) {
			Log.d("HTTP Post", "HTTP Post : UnsupportedEncodingException, " + e.toString());
			response = "";
		} catch (ClientProtocolException e) {
			Log.d("HTTP Post", "HTTP Post : ClientProtocolException, " + e.toString());
			response = "";
		} catch (IOException e) {
			Log.d("HTTP Post", "HTTP Post : IOException, " + e.toString());
			response = "";
		}
		return response;
	}
	
	
	public static String HTTPPost(String urlAddress, String lan, JsonObject postDataPair) {
		
		HttpParams myParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(myParams, 30000);
        HttpConnectionParams.setSoTimeout(myParams, 30000);
      
		DefaultHttpClient hc=new DefaultHttpClient(myParams);   
		ResponseHandler <String> res=new BasicResponseHandler();

		HttpPost postMethod=new HttpPost(urlAddress);
		
		postMethod.addHeader("Accept", "application/json");
		postMethod.addHeader("Accept-language", lan);
		postMethod.setHeader("Content-Type", "application/json");


		String response = "";
		try {
			if (postDataPair != null) {
				JSONObject jsonObject = new JSONObject(postDataPair.toString());

				postMethod.setHeader("Content-type", "application/json");

				StringEntity se = new StringEntity(jsonObject.toString());
				se.setContentType("application/x-www-form-urlencoded");
				postMethod.setEntity(se);
			}
			 response = hc.execute(postMethod, res);


		} catch (ClientProtocolException e) {
			Log.d("", e.getMessage());
			e.printStackTrace();

		} catch (IOException e) {
			Log.d("", e.getMessage());
			e.printStackTrace();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;

//		int c = postDataPair.length;
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(postDataPair.length / 2);
//
//		String result = urlAddress + " : param {";
//		for (int i = 0; i < postDataPair.length; i += 2) {
//			nameValuePairs.add(new BasicNameValuePair(postDataPair[i], postDataPair[i + 1]));
//			result = result + postDataPair[i] + "=" + postDataPair[i+1] + " , ";
//		}
//
//		result += " }";
//		Log.d("HTTP REQUEST: ", result);
//		String response = "";
//		try {
//			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//			response = hc.execute(postMethod,res);
//		} catch (UnsupportedEncodingException e) {
//			Log.d("HTTP Post", "HTTP Post : UnsupportedEncodingException, " + e.toString());
//			response = "";
//		} catch (ClientProtocolException e) {
//			Log.d("HTTP Post", "HTTP Post : ClientProtocolException, " + e.toString());
//			response = "";
//		} catch (IOException e) {
//			Log.d("HTTP Post", "HTTP Post : IOException, " + e.toString());
//			response = "";
//		}
//		return response;
	}

	public static String HTTPPostWithArray(String urlAddress, String lan, String... postDataPair) {

		HttpParams myParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(myParams, 30000);
		HttpConnectionParams.setSoTimeout(myParams, 30000);

		DefaultHttpClient hc=new DefaultHttpClient(myParams);
		ResponseHandler <String> res=new BasicResponseHandler();

		HttpPost postMethod=new HttpPost(urlAddress);

//		int c = postDataPair.length;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(postDataPair.length / 2);

		String result = urlAddress + " : param {";
		for (int i = 0; i < postDataPair.length; i += 2) {
			nameValuePairs.add(new BasicNameValuePair(postDataPair[i], postDataPair[i + 1]));
			result = result + postDataPair[i] + "=" + postDataPair[i+1] + " , ";
		}
//
//		result += " }";


//		Log.d("HTTP REQUEST: ", result);
		postMethod.setHeader("Accept", "application/json");
//		postMethod.setHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.setHeader("Content-type", "application/json");

		String response = "";
		try {
//			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			StringEntity se = new StringEntity(postDataPair[1],"UTF-8");
			se.setContentType("application/json");
			postMethod.setEntity(se);


			response = hc.execute(postMethod,res);
		} catch (UnsupportedEncodingException e) {
			Log.d("HTTP Post", "HTTP Post : UnsupportedEncodingException, " + e.toString());
			response = "";
		} catch (ClientProtocolException e) {
			Log.d("HTTP Post", "HTTP Post : ClientProtocolException, " + e.toString());
			response = "";
		} catch (IOException e) {
			Log.d("HTTP Post", "HTTP Post : IOException, " + e.toString());
			response = "";
		}
		return response;
	}
	public static String HTTPDeleteWithToken(String urlAddress, String lan, String token, String... postDataPair) {
		HttpParams myParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(myParams, 30000);
		HttpConnectionParams.setSoTimeout(myParams, 30000);

		DefaultHttpClient hc=new DefaultHttpClient(myParams);
		ResponseHandler <String> res=new BasicResponseHandler();

		HttpDelete postMethod=new HttpDelete(urlAddress);

		postMethod.addHeader("Accept", "application/json");
		postMethod.addHeader("Accept-language", lan);
		postMethod.setHeader("Content-Type", "application/x-www-form-urlencoded");
		postMethod.setHeader("Authorization", token);
//		postMethod.setHeader("device_token", Global.device_token);

		int c = postDataPair.length;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(postDataPair.length / 2);

		String result = urlAddress + " : param {";
		for (int i = 0; i < postDataPair.length; i += 2) {
			nameValuePairs.add(new BasicNameValuePair(postDataPair[i], postDataPair[i + 1]));
			result = result + postDataPair[i] + "=" + postDataPair[i+1] + " , ";
		}

		result += " }";
		Log.d("HTTP REQUEST: ", result);
		String response = "";
		try {
//			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = hc.execute(postMethod,res);
		} catch (UnsupportedEncodingException e) {
			Log.d("HTTP Post", "HTTP Post : UnsupportedEncodingException, " + e.toString());
			response = "";
		} catch (ClientProtocolException e) {
			Log.d("HTTP Post", "HTTP Post : ClientProtocolException, " + e.toString());
			response = "";
		} catch (IOException e) {
			Log.d("HTTP Post", "HTTP Post : IOException, " + e.toString());
			response = "";
		}
		return response;
	}
	public static String HTTPGetWithToken(String urlAddress, String lan, String... postDataPair) {
		HttpGet httpget = new HttpGet(urlAddress);
		httpget.addHeader("Accept", "application/json");
		httpget.addHeader("Accept-language", lan);
		httpget.setHeader("Content-Type", "application/json");

		HttpClient Client = new DefaultHttpClient();
		String response = "";

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		try {
			response = Client.execute(httpget, responseHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}

package com.dts.dts.library;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;


@SuppressWarnings("deprecation")
public class NetworkHelper {

	public static final String TAG = NetworkHelper.class.getSimpleName();
	/*
	 * Connection and read timeout values in milliseconds.
	 */
	public static final int CONNECTION_TIMEOUT = 80000;
	public static final int WAIT_RESPONSE_TIMEOUT = 80000;

	public static final int OK 										= 200;
	public static final int CONNECTION_ERROR 						= 201;
	public static final int SYSTEM_ERROR 							= 202;
	public static final int CONNECT_TIMEOUT 						= 203;
	public static final int READ_TIMEOUT 							= 204;
	public static final int IO_ERROR 								= 205;
	public static final int CLIENT_PROTOCOL_ERROR 					= 206;
	/**
	 * Check data connection is available or not.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			boolean networkAvailable = activeNetworkInfo.isAvailable();
			boolean networkConnected = activeNetworkInfo.isConnected();
			if (networkAvailable && networkConnected) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Executes an HTTP Get request, pass data as query parameters.
	 * 
	 * @param serviceUrl
	 * @param context
	 * @return
	 */
	public static Response doGet(String serviceUrl, String lan, String token, Context context) {
		Response response = new Response();
		if (!isOnline(context)) {
			// no network connection
			response.setStatusCode(CONNECTION_ERROR);
			return response;
		}
		try {
			// set basic http params
			HttpParams httpParams = new BasicHttpParams();
			// set connection timeout
			HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
			// set socket timeout
			HttpConnectionParams.setSoTimeout(httpParams, WAIT_RESPONSE_TIMEOUT);
			// create http client object
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			
			// build an HttpPost object
			HttpGet httpGet = new HttpGet(serviceUrl);
			
			httpGet.addHeader("Accept", "application/json");
			httpGet.addHeader("Accept-language", lan);
//			httpGet.setHeader("Content-Type", "application/json");
			httpGet.setHeader("Token", token);
			
			// execute get request
			HttpResponse httpReposne = httpClient.execute(httpGet);
			// read response into a string buffer
			String line = null;
			StringBuffer stringBuffer = new StringBuffer();
			InputStreamReader streamReader = new InputStreamReader(httpReposne.getEntity().getContent());
			BufferedReader reader = new BufferedReader(streamReader);
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
			}
			reader.close();
			// set response data
			response.setStatusCode(OK);
			response.setResponseText(stringBuffer.toString());
		}
		catch (SocketTimeoutException ex) {
			response.setStatusCode(READ_TIMEOUT);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (ConnectTimeoutException ex) {
			response.setStatusCode(CONNECT_TIMEOUT);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (ClientProtocolException ex) {
			response.setStatusCode(CLIENT_PROTOCOL_ERROR);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (IOException ex) {
			response.setStatusCode(IO_ERROR);
			Log.e(TAG, ex.getMessage(), ex);
		}
		catch (Exception ex) {
			response.setStatusCode(SYSTEM_ERROR);
			Log.e(TAG, ex.getMessage(), ex);
		}
		return response;
		
	}
}
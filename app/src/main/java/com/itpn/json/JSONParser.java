package com.itpn.json;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by anupamchugh on 29/08/16.
 */
public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static JSONArray jArr = null;
	static String json = "";
	static String error = "";

	// constructor
	public JSONParser() {

	}

	public JSONObject makeHttpRequest(String requestURL, ArrayList<BasicNameValuePair> postDataParams) {
		URL url;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(postDataParams));

			writer.flush();
			writer.close();
			os.close();
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				sb = new StringBuilder();
				String response;
				while ((response = br.readLine()) != null) {
					sb.append(response);
				}
			}
			jObj = new JSONObject(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return jObj;
	}

	private String getPostDataString(ArrayList<BasicNameValuePair> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		ListIterator<BasicNameValuePair> iterator = params.listIterator();
		while(iterator.hasNext()){
			BasicNameValuePair param = iterator.next();
			if (first)
				first = false;
			else
				result.append("&");
			result.append(URLEncoder.encode(param.getName(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(param.getValue(), "UTF-8"));
		}
		System.out.println(result.toString());
		return result.toString();
	}

	// function get json from url
	// by making HTTP POST or GET mehtod
//	public JSONObject makeHttpRequest(String url, String method,
//									  ArrayList params) {
//
//		// Making HTTP request
//		try {
//
//			// check for request method
//			if(method.equals("POST")){
//				// request method is POST
//				// defaultHttpClient
//				HttpClient httpClient = new DefaultHttpClient();
//				HttpPost httpPost = new HttpPost(url);
//
//				httpPost.setEntity(new UrlEncodedFormEntity(params));
//				try {
//					Log.e("API123", " " +convertStreamToString(httpPost.getEntity().getContent()));
//					Log.e("API123",httpPost.getURI().toString());
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//				HttpResponse httpResponse = httpClient.execute(httpPost);
//				Log.e("API123",""+httpResponse.getStatusLine().getStatusCode());
//				error= String.valueOf(httpResponse.getStatusLine().getStatusCode());
//				HttpEntity httpEntity = httpResponse.getEntity();
//				is = httpEntity.getContent();
//			}
//			else if(method.equals("GET")){
//				// request method is GET
//				DefaultHttpClient httpClient = new DefaultHttpClient();
//				if(params!=null){
//					String paramString = URLEncodedUtils.format(params, "utf-8");
//					url += "?" + paramString;
//				}
//				HttpGet httpGet = new HttpGet(url);
//
//				HttpResponse httpResponse = httpClient.execute(httpGet);
//				HttpEntity httpEntity = httpResponse.getEntity();
//				is = httpEntity.getContent();
//			}
//
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					is, StandardCharsets.ISO_8859_1), 8);
//			StringBuilder sb = new StringBuilder("");
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			json = sb.toString();
//			Log.d("API123",json);
//		} catch (Exception e) {
//			Log.e("Buffer Error", "Error converting result " + e);
//		}
//
//		// try to parse the string to a JSON object
//		try {
//			jObj = new JSONObject(json);
//			jObj.put("error_code",error);
//		} catch (JSONException e) {
//			Log.e("JSON Parser", "Error parsing data " + e);
//		}
//
//		// return JSON String
//		return jObj;
//
//	}

	public JSONArray makeHttpRequest(String link) {
		// Making HTTP request
		try {
				// request method is GET
//				DefaultHttpClient httpClient = new DefaultHttpClient();
//				HttpGet httpGet = new HttpGet(url);
//				HttpResponse httpResponse = httpClient.execute(httpGet);
//				HttpEntity httpEntity = httpResponse.getEntity();
				URL url = new URL(link);
				HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
				urlConnection.connect();
				is = urlConnection.getInputStream();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, StandardCharsets.ISO_8859_1), 8);
			StringBuilder sb = new StringBuilder("");
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();

			if(sb.toString().trim().equals("null")){
				json = null;
			}
			else{
				json = sb.toString();
			}
			Log.d("API123",json);
		}
		catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e);
		}

		// try to parse the string to a JSON object
		try {
			if(json!=null){
				jArr = new JSONArray(json);
			}
			else{
				jArr = null;
			}
		}
		catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e);
		}

		// return JSON String
		return jArr;

	}

	private String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		is.close();
		return sb.toString();
	}
}

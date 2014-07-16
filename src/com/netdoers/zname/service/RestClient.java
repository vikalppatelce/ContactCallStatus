/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- ADD IPD ACTIVITY
 * DESCRIPTION 		:- SAVE IPD
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * S0001       VIKALP PATEL    27/02/14         SECURITY        ADDED HASH IN JSON FILE UPLOAD
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

import com.netdoers.zname.AppConstants;

public class RestClient {

	public static String postData(String url, JSONObject dataToSend) throws JSONException{  
        // Create a new HttpClient and Post Header
				
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
 
        String text = null;
        try {
 
            JSONArray postjson=new JSONArray();
            postjson.put(dataToSend);
             
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            httppost.setHeader("Authorization", "Basic " + Base64.encodeToString("netdoersadmin:538f25fc32727".getBytes(),Base64.NO_WRAP));

            StringEntity se = new StringEntity(dataToSend.toString());
            httppost.setEntity(se);
            
                       
            // Execute HTTP Post Request
            System.out.print(dataToSend);
            HttpResponse response = httpclient.execute(httppost);
 
            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();
 
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
 
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text = sb.toString();
            }
  
        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        }
        if(text != null)
        {
        	Log.e("----------> " , text);
        }
        
        return text;
    }

	public static String postStringData(String url, String dataToSend) throws JSONException{  
        // Create a new HttpClient and Post Header
				
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
 
        String text = null;
        try {
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            httppost.setHeader("Authorization", "Basic " + Base64.encodeToString("netdoersadmin:538f25fc32727".getBytes(),Base64.NO_WRAP));

            StringEntity se = new StringEntity(dataToSend.toString());
            httppost.setEntity(se);
            
                       
            // Execute HTTP Post Request
            System.out.print(dataToSend);
            HttpResponse response = httpclient.execute(httppost);
 
            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();
 
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
 
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text = sb.toString();
            }
  
        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        }
        if(text != null)
        {
        	Log.e("----------> " , text);
        }
        
        return text;
    }

	public static String putData(String url, JSONObject dataToSend) throws JSONException{  
        // Create a new HttpClient and Post Header
				
        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpput = new HttpPut(url);
 
        String text = null;
        try {
 
            JSONArray postjson=new JSONArray();
            postjson.put(dataToSend);
             
            httpput.setHeader("Accept", "application/json");
            httpput.setHeader("Content-type", "application/json");
            httpput.setHeader("Authorization", "Basic " + Base64.encodeToString("netdoersadmin:538f25fc32727".getBytes(),Base64.NO_WRAP));

            StringEntity se = new StringEntity(dataToSend.toString());
            httpput.setEntity(se);
            
                       
            // Execute HTTP Post Request
            System.out.print(dataToSend);
            HttpResponse response = httpclient.execute(httpput);
 
            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();
 
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
 
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text = sb.toString();
            }
  
        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        }
        if(text != null)
        {
        	Log.e("----------> " , text);
        }
        
        return text;
    }

	public static String getData(String url) throws JSONException{  
        // Create a new HttpClient and Post Header
				
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
 
        String text = null;
        try {
 
            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("Content-type", "application/json");
            httpget.setHeader("Authorization", "Basic " + Base64.encodeToString("netdoersadmin:538f25fc32727".getBytes(),Base64.NO_WRAP));

//            StringEntity se = new StringEntity(dataToSend.toString());
//            httpget.setEntity(se);
            
                       
            // Execute HTTP Get Request
            HttpResponse response = httpclient.execute(httpget);
 
            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();
 
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
 
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text = sb.toString();
            }
  
        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        }
        if(text != null)
        {
        	Log.e("----------> " , text);
        }
        
        return text;
    }
	
	public static String postData1(String url, JSONObject dataToSend) throws JSONException{  
        // Create a new HttpClient and Post Header
				
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
 
        String text = null;
        try {
 
        	
        	List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        	nvp.add(new BasicNameValuePair("json", dataToSend.toString()));
            /*JSONArray postjson=new JSONArray();
            postjson.put(dataToSend);
             
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            StringEntity se = new StringEntity(dataToSend.toString());*/
        	
        	
            httppost.setEntity(new UrlEncodedFormEntity(nvp)); 
            
                       
            // Execute HTTP Post Request
            System.out.print(dataToSend);
            HttpResponse response = httpclient.execute(httppost);
 
            // for JSON:
            if(response != null)
            {
                InputStream is = response.getEntity().getContent();
 
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
 
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                text = sb.toString();
            }
  
        }catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();
        }
        if(text != null)
        {
        	Log.e("----------> " , text);
        }
        
        return text;
    }
	
	
	public static boolean postRecordedFile(File file, String url) throws UnsupportedEncodingException, ClientProtocolException, IOException{
		boolean fileSent = false;
		
		HttpClient httpclient = new DefaultHttpClient();

	    HttpPost httppost = new HttpPost(url);
	    	    
	    MultipartEntity mpEntity = new MultipartEntity();
	    ContentBody cbFile = new FileBody(file, "binary/octet-stream");
	    mpEntity.addPart("FILE", cbFile);
	    httppost.setEntity(mpEntity);

	    System.out.println("executing request " + httppost.getRequestLine());
	    HttpResponse response = httpclient.execute(httppost);
	    HttpEntity resEntity = response.getEntity();

	    System.out.println(response.getStatusLine());
	    if (resEntity != null) {
	      System.out.println(EntityUtils.toString(resEntity));
	    }
	    
	    if (resEntity != null) {
	      resEntity.consumeContent();
	    }

	    httpclient.getConnectionManager().shutdown();
	    
		return fileSent;
	}
	
	public static String postRecordedFile1(String type, File file, JSONObject json, String url) throws UnsupportedEncodingException, ClientProtocolException, IOException{
		String fileName = file.getName();
		FileInputStream fileInputStream = new FileInputStream(file);
		int bytesAvailable = fileInputStream.available();
		
        byte[] buffer = new byte[bytesAvailable];
        // read file and write it into form...
        long bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
        
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Authorization", "Basic " + Base64.encodeToString("netdoersadmin:538f25fc32727".getBytes(),Base64.NO_WRAP));
		ByteArrayBody bab = new ByteArrayBody(buffer, fileName);
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart(type, bab);

		postRequest.setEntity(reqEntity);
		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		StringBuffer stringResponse = new StringBuffer();
		while((line = in.readLine()) != null) 
		{
			stringResponse.append(line);
			Log.e("postRecordedFile1", line);
		}
		response = null;
		in.close();
		in = null;
		reqEntity = null;
		postRequest = null;
		
		return stringResponse.toString();
	}
	
	public static String uploadFile(String type, File file, String url) throws UnsupportedEncodingException, ClientProtocolException, IOException{
		String fileName = file.getName();
		FileInputStream fileInputStream = new FileInputStream(file);
		int bytesAvailable = fileInputStream.available();
		
        byte[] buffer = new byte[bytesAvailable];
        // read file and write it into form...
        long bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
        
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Authorization", "Basic " + Base64.encodeToString("netdoersadmin:538f25fc32727".getBytes(),Base64.NO_WRAP));
		ByteArrayBody bab = new ByteArrayBody(buffer, fileName);
		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart(type, bab);

		postRequest.setEntity(reqEntity);
		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		StringBuffer stringResponse = new StringBuffer();
		while((line = in.readLine()) != null) 
		{
			stringResponse.append(line);
			Log.e("postRecordedFile1", line);
		}
		response = null;
		in.close();
		in = null;
		reqEntity = null;
		postRequest = null;
		
		return stringResponse.toString();
	}
}

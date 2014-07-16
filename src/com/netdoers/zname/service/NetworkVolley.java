/* HISTORY
 * CATEGORY			 :- NETWORK
 * DEVELOPER		 :- VIKALP PATEL
 * AIM      		 :- NETWORK RELATED OPERATIONS WITH WEB-SERVICES
 * DESCRIPTION       :- CUSTOMIZE REQUEST WITH BASIC AUTHORIZATION REQUIRED WHILE MAKING WEB-SERVICE REQUEST
 * 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * ZM001      VIKALP PATEL     02/07/2014                       CREATED 
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.netdoers.zname.AppConstants;

public class NetworkVolley {

	public class VolleyPostJsonRequest extends JsonObjectRequest {

		public VolleyPostJsonRequest(String url, JSONObject jsonObject,
				Listener<JSONObject> listener, ErrorListener errorListener) {
			super(url, jsonObject, listener, errorListener);
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			Map<String, String> headers = new HashMap<String, String>();
			String auth = "Basic "
					+ Base64.encodeToString(
							(AppConstants.HEADERS.USER + ":" + AppConstants.HEADERS.PASSWORD)
									.getBytes(), Base64.NO_WRAP);
			headers.put("Authorization", auth);
			return headers;
		}
	}

	public class VolleyGetJsonRequest extends JsonObjectRequest {

		public VolleyGetJsonRequest(String url, JSONObject jsonRequest,
				Listener<JSONObject> listener, ErrorListener errorListener) {
			super(url, jsonRequest, listener, errorListener);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			Map<String, String> headers = new HashMap<String, String>();
			String auth = "Basic "
					+ Base64.encodeToString(
							(AppConstants.HEADERS.USER + ":" + AppConstants.HEADERS.PASSWORD)
									.getBytes(), Base64.NO_WRAP);
			headers.put("Authorization", auth);
			return headers;
		}

	}
	
	public class VolleyStringRequest extends StringRequest{
		
		public VolleyStringRequest(int method, String url,
				Listener<String> listener, ErrorListener errorListener) {
			super(method, url, listener, errorListener);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			Map<String, String> headers = new HashMap<String, String>();
			String auth = "Basic "
					+ Base64.encodeToString(
							(AppConstants.HEADERS.USER + ":" + AppConstants.HEADERS.PASSWORD)
									.getBytes(), Base64.NO_WRAP);
			headers.put("Authorization", auth);
			return headers;
		}
	}
}

package com.qboxus.gograbdriver.helpingclasses;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qboxus.gograbdriver.appinterfaces.CallbackResponce;
import com.qboxus.gograbdriver.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequest {


    public static void callApi(final Context context, String url, JSONObject jsonObject,
                               final CallbackResponce callbackResponce){

        Functions.logDMsg( jsonObject.toString());
        Functions.logDMsg( url);

         JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Functions.logDMsg(response.toString());

                        if(callbackResponce !=null)
                        callbackResponce.responce(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                 if(callbackResponce !=null)
                  callbackResponce.responce(error.toString());
                Functions.logDMsg("Error Volly "+error.toString());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Api-Key", Constants.API_KEY);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);
    }

    public static void callApi(final Context context, String url, JSONObject jsonObject,
                               final CallbackResponce callbackResponce, int type){

        Functions.logDMsg( jsonObject.toString());
        Functions.logDMsg( url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(type,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Functions.logDMsg(response.toString());

                        if(callbackResponce !=null)
                            callbackResponce.responce(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if(callbackResponce !=null)
                    callbackResponce.responce(error.toString());
                Functions.logDMsg("Error Volly "+error);
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Api-Key", Constants.API_KEY);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.getCache().clear();
        requestQueue.add(jsonObjReq);
    }


}

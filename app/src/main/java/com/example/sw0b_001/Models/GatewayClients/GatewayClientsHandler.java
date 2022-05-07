package com.example.sw0b_001.Models.GatewayClients;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class GatewayClientsHandler {

    public static void remoteFetchAndStoreGatewayClients(Context context, String gatewayServerSeedsUrl) {

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest remoteSeedsRequest = new JsonObjectRequest(Request.Method.GET, gatewayServerSeedsUrl, null, new Response.Listener() {

            @Override
            public void onResponse(Object response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(remoteSeedsRequest);
    }
}

package com.example.sw0b_001.Models.GatewayClients;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Database.Datastore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GatewayClientsHandler {

    public static long add(Context context, GatewayClient gatewayClient) throws InterruptedException {
        // Log.d(getClass().getSimpleName(), "Public key for gateway: " + gatewayServer.getPublicKey());
        Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                Datastore.DatabaseName).build();

        final long[] gatewayClientsInsertId = {-1};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GatewayClientsDao gatewayClientsDao = databaseConnector.gatewayClientsDao();
                gatewayClientsInsertId[0] = gatewayClientsDao.insert(gatewayClient);
                Log.d(getClass().getSimpleName(), "Added new gateway client: " + gatewayClient.getMSISDN());
            }
        });
        thread.start();
        thread.join();
        return gatewayClientsInsertId[0];
    }

    public static void remoteFetchAndStoreGatewayClients(Context context, String gatewayServerSeedsUrl) {

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest remoteSeedsRequest = new JsonArrayRequest(Request.Method.GET, gatewayServerSeedsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray responses) {
                for(int i=0;i<responses.length();++i) {
                    try {
                        JSONObject response = responses.getJSONObject(i);
                        String IMSI = response.getString("IMSI");
                        String MSISDN = response.getString("MSISDN");
                        String country = response.getString("country");
                        double LPS = response.getDouble("LPS");
                        String seedType = response.getString("seed_type");

                        GatewayClient gatewayClient = new GatewayClient();
                        gatewayClient.setType(seedType);
                        gatewayClient.setMSISDN(MSISDN);
                        gatewayClient.setLastPingSession(LPS);
                        gatewayClient.setCountry(country);

                        GatewayClientsHandler.add(context, gatewayClient);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(remoteSeedsRequest);
    }

    public static List<GatewayClient> getAllGatewayClients(Context context) {
        final List<GatewayClient>[] gatewayClients = new List[]{new ArrayList<>()};

        Thread fetchGatewayClientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnection = Room.databaseBuilder(context,
                        Datastore.class, Datastore.DatabaseName)
                        .fallbackToDestructiveMigration()
                        .build();

                GatewayClientsDao gatewayClientsDao = databaseConnection.gatewayClientsDao();
                gatewayClients[0] = gatewayClientsDao.getAll();
            }
        });
        fetchGatewayClientThread.start();
        try {
            fetchGatewayClientThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return gatewayClients[0];
    }
}

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

        final long[] gatewayClientsInsertId = {-1};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                        Datastore.DatabaseName).build();
                GatewayClientsDao gatewayClientsDao = databaseConnector.gatewayClientsDao();
                gatewayClientsInsertId[0] = gatewayClientsDao.insert(gatewayClient);
                Log.d(getClass().getSimpleName(), "Added new gateway client: " + gatewayClient.getMSISDN());
            }
        });
        thread.start();
        thread.join();
        return gatewayClientsInsertId[0];
    }

    public static void toggleDefault(Context context, GatewayClient gatewayClient) throws InterruptedException {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                        Datastore.DatabaseName).build();
                GatewayClientsDao gatewayClientsDao = databaseConnector.gatewayClientsDao();
                gatewayClientsDao.resetAllDefaults();
                gatewayClientsDao.updateDefault(gatewayClient.isDefault(), gatewayClient.getId());
            }
        });
        thread.start();
        thread.join();
    }

    public static void remoteFetchAndStoreGatewayClients(Context context, String gatewayServerSeedsUrl, Runnable callbackFunction) throws InterruptedException {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest remoteSeedsRequest = new JsonArrayRequest(Request.Method.GET, gatewayServerSeedsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray responses) {
                Log.d(getClass().getName(), "___> got nre response from thread");
                for(int i=0;i<responses.length();++i) {
                    try {
                        // TODO: Add algorithm for default Gateway Client
                        JSONObject response = responses.getJSONObject(i);
                        String IMSI = response.getString("IMSI");
                        String MSISDN = response.getString("MSISDN");
                        String country = response.getString("country");
                        String operatorName = response.getString("operator_name");
                        double LPS = response.getDouble("LPS");
                        String seedType = response.getString("seed_type");

                        GatewayClient gatewayClient = new GatewayClient();
                        gatewayClient.setType(seedType);
                        gatewayClient.setMSISDN(MSISDN);
                        gatewayClient.setLastPingSession(LPS);
                        gatewayClient.setCountry(country);
                        gatewayClient.setOperatorName(operatorName);

                        GatewayClientsHandler.add(context, gatewayClient);
                        Log.d(getClass().getName(), "-> Added new gateway client: " + gatewayClient.getMSISDN());
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                if(callbackFunction != null)
                    callbackFunction.run();
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

    public static void clearStoredGatewayClients(Context context) {

        Thread clearGatewayClientsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnection = Room.databaseBuilder(context,
                        Datastore.class, Datastore.DatabaseName)
                        .fallbackToDestructiveMigration()
                        .build();

                GatewayClientsDao gatewayClientsDao = databaseConnection.gatewayClientsDao();
                gatewayClientsDao.deleteAll();
            }
        });
        clearGatewayClientsThread.start();
        try {
            clearGatewayClientsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static GatewayClient getGatewayClientMSISDN(Context context) throws Throwable {

        GatewayClient defaultGatewayClient = new GatewayClient();

        List<GatewayClient> gatewayClients = GatewayClientsHandler.getAllGatewayClients(context);
        for(GatewayClient gatewayClient : gatewayClients) {
            if(gatewayClient.isDefault()) {
                defaultGatewayClient = gatewayClient;
                break;
            }
        }

        return defaultGatewayClient;
    }

    public static String getDefaultGatewayClientMSISDN(Context context) throws Throwable {
        GatewayClient gatewayClient = getGatewayClientMSISDN(context);
        if(gatewayClient.getMSISDN() == null || gatewayClient.getMSISDN().isEmpty()) {
            // TODO should have fallback GatewayClients that can be used in the code
            String defaultSeedFallbackGatewayClientMSISDN = "+237672451860";
            gatewayClient.setMSISDN(defaultSeedFallbackGatewayClientMSISDN);
        }

        return gatewayClient.getMSISDN();
    }
}

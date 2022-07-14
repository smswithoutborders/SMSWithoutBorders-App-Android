package com.example.sw0b_001.Models.GatewayClients;

import android.content.Context;
import android.telephony.TelephonyManager;

import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GatewayClientsHandler {

    public static long add(Context context, GatewayClient gatewayClient) throws InterruptedException {

        final long[] gatewayClientsInsertId = {-1};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                        Datastore.DatabaseName).build();
                GatewayClientsDao gatewayClientsDao = databaseConnector.gatewayClientsDao();
                gatewayClientsInsertId[0] = gatewayClientsDao.insert(gatewayClient);
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

    public static String getOperatorId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String operatorId = telephonyManager.getSimOperator();

        return operatorId;
    }

    public static boolean containsDefaultProperties(Context context, String gatewayClientOperatorId) {
        String operatorId = getOperatorId(context);
        return operatorId.equals(gatewayClientOperatorId);
    }

    public static void remoteFetchAndStoreGatewayClients(Context context, String gatewayServerSeedsUrl, Runnable callbackFunction) throws InterruptedException {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest remoteSeedsRequest = new JsonArrayRequest(Request.Method.GET, gatewayServerSeedsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray responses) {
                for(int i=0, findDefaultCounter=0;i<responses.length();++i, ++findDefaultCounter) {
                    try {
                        // TODO: Add algorithm for default Gateway Client
                        JSONObject response = responses.getJSONObject(i);
                        String IMSI = response.getString("IMSI");
                        String MSISDN = response.getString("MSISDN");
                        String country = response.getString("country");
                        String operatorName = response.getString("operator_name");
                        String operatorId = response.getString("operator_id");
                        double LPS = response.getDouble("LPS");
                        String seedType = response.getString("seed_type");

                        GatewayClient gatewayClient = new GatewayClient();
                        gatewayClient.setType(seedType);
                        gatewayClient.setMSISDN(MSISDN);
                        gatewayClient.setLastPingSession(LPS);
                        gatewayClient.setCountry(country);
                        gatewayClient.setOperatorName(operatorName);
                        gatewayClient.setOperatorId(operatorId);

                        // Random Gateway client selector
                        GatewayClientsHandler.add(context, gatewayClient);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                List<GatewayClient> gatewayClients = new ArrayList<>();
                try {
                    gatewayClients = appendDefaultGatewayClients(context, gatewayClients);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                boolean defaultSet = false;
                for(GatewayClient gatewayClient : gatewayClients) {
                    try {
                        if(!defaultSet && containsDefaultProperties(context, gatewayClient.getOperatorId())) {

                            gatewayClient.setDefault(true);

                            defaultSet = true;
                        }

                        GatewayClientsHandler.add(context, gatewayClient);

                    } catch (InterruptedException e) {
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
                callbackFunction.run();
            }
        });
        queue.add(remoteSeedsRequest);
    }

    private static List<GatewayClient> appendDefaultGatewayClients(Context context, List<GatewayClient> gatewayClientList) throws InterruptedException {
        GatewayClient gatewayClient = new GatewayClient();
        gatewayClient.setCountry("Cameroon");
        gatewayClient.setMSISDN(context.getString(R.string.default_gateway_MSISDN_0));
        gatewayClient.setOperatorName("MTN Cameroon");
        gatewayClient.setOperatorId("62401");

        GatewayClient gatewayClient1 = new GatewayClient();
        gatewayClient1.setCountry("Cameroon");
        gatewayClient1.setMSISDN(context.getString(R.string.default_gateway_MSISDN_1));
        gatewayClient1.setOperatorName("MTN Cameroon");
        gatewayClient1.setOperatorId("62401");

        GatewayClient gatewayClient2 = new GatewayClient();
        gatewayClient2.setCountry("Cameroon");
        gatewayClient2.setMSISDN(context.getString(R.string.default_gateway_MSISDN_2));
        gatewayClient2.setOperatorName("Orange Cameroon");
        gatewayClient2.setOperatorId("62402");

        gatewayClientList.add(gatewayClient);
        gatewayClientList.add(gatewayClient1);
        gatewayClientList.add(gatewayClient2);

        return gatewayClientList;
    }

    public static List<GatewayClient> getAllGatewayClients(Context context) throws InterruptedException {
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
            String defaultSeedFallbackGatewayClientMSISDN = context.getString(R.string.default_gateway_MSISDN_0);
            gatewayClient.setMSISDN(defaultSeedFallbackGatewayClientMSISDN);
        }

        return gatewayClient.getMSISDN();
    }
}

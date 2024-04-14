package com.example.sw0b_001.Data.GatewayClients;

import android.content.Context;
import android.telephony.TelephonyManager;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.R;

import java.util.ArrayList;
import java.util.List;

public class GatewayClientsHandler {

    public static long add(Context context, GatewayClient gatewayClient) throws InterruptedException {

        final long[] gatewayClientsInsertId = {-1};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                        Datastore.databaseName).build();
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
                        Datastore.databaseName).build();
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


    public static void storeGatewayClients(Context context, List<GatewayClient> gatewayClients) {
       for(GatewayClient gatewayClient : gatewayClients) {
           try {
               add(context, gatewayClient);
           } catch(Exception e) {
               e.printStackTrace();
           }
       }
    }

    public static List<GatewayClient> setDefaults(Context context, List<GatewayClient> gatewayClients) throws InterruptedException {
        for(GatewayClient gatewayClient : gatewayClients)
            if (gatewayClient.isDefault)
                return gatewayClients;

        for(GatewayClient gatewayClient : gatewayClients) {
            if(containsDefaultProperties(context, gatewayClient.getOperatorId())) {
                gatewayClient.setDefault(true);
                toggleDefault(context, gatewayClient);
                return gatewayClients;
            }
        }

        // probably an international number from CM
        // orange CM would be best to handle this request
        // going with the first available option now
        String defaultOperatorId = context.getString(R.string.default_operator_id);
        for(GatewayClient gatewayClient : gatewayClients) {
            if(gatewayClient.getOperatorId().equals(defaultOperatorId)) {
                gatewayClient.setDefault(true);
                toggleDefault(context, gatewayClient);
                break;
            }
        }

        return gatewayClients;
    }

//    public static List<GatewayClient> getAllGatewayClients(Context context) throws InterruptedException {
//        final List<GatewayClient>[] gatewayClients = new List[]{new ArrayList<>()};
//        Thread fetchGatewayClientThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Datastore databaseConnection = Room.databaseBuilder(context,
//                                Datastore.class, Datastore.databaseName)
//                        .fallbackToDestructiveMigration()
//                        .build();
//
//                GatewayClientsDao gatewayClientsDao = databaseConnection.gatewayClientsDao();
//                gatewayClients[0] = gatewayClientsDao.getAll();
//            }
//        });
//        fetchGatewayClientThread.start();
//        try {
//            fetchGatewayClientThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        return gatewayClients[0];
//    }

    private static List<GatewayClient> getDefaultGatewayClients(Context context) throws InterruptedException {
        List<GatewayClient> gatewayClientList = new ArrayList<>();
        GatewayClient gatewayClient = new GatewayClient();
        gatewayClient.setCountry("Cameroon");
        gatewayClient.setMSISDN(context.getString(R.string.default_gateway_MSISDN_0));
        gatewayClient.setOperatorName("MTN Cameroon");
        gatewayClient.setOperatorId("62401");

        GatewayClient gatewayClient2 = new GatewayClient();
        gatewayClient2.setCountry("Cameroon");
        gatewayClient2.setMSISDN(context.getString(R.string.default_gateway_MSISDN_2));
        gatewayClient2.setOperatorName("Orange Cameroon");
        gatewayClient2.setOperatorId("62402");

        gatewayClientList.add(gatewayClient);
        gatewayClientList.add(gatewayClient2);

        return gatewayClientList;
    }

//    public static GatewayClient getGatewayClientMSISDN(Context context) throws Throwable {
//
//        GatewayClient defaultGatewayClient = new GatewayClient();
//
//        List<GatewayClient> gatewayClients = GatewayClientsHandler.getAllGatewayClients(context);
//        for(GatewayClient gatewayClient : gatewayClients) {
//            if(gatewayClient.isDefault()) {
//                defaultGatewayClient = gatewayClient;
//                break;
//            }
//        }
//
//        return defaultGatewayClient;
//    }

//    public static String getDefaultGatewayClientMSISDN(Context context) throws Throwable {
//        GatewayClient gatewayClient = getGatewayClientMSISDN(context);
//
//        if(gatewayClient.getMSISDN() == null || gatewayClient.getMSISDN().isEmpty()) {
//            // TODO should have fallback GatewayClients that can be used in the code
//            String defaultSeedFallbackGatewayClientMSISDN = context.getString(R.string.default_gateway_MSISDN_0);
//            gatewayClient.setMSISDN(defaultSeedFallbackGatewayClientMSISDN);
//        }
//
//        return gatewayClient.getMSISDN();
//    }
}

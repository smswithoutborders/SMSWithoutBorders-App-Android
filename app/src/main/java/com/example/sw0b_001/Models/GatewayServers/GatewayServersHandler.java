package com.example.sw0b_001.Models.GatewayServers;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;

public class GatewayServersHandler {

    private Context context;

    public GatewayServersHandler() {}

    public GatewayServersHandler(Context context) {
        this.context = context;
    }

    public long add(GatewayServers gatewayServer) throws InterruptedException {
        // Log.d(getClass().getSimpleName(), "Public key for gateway: " + gatewayServer.getPublicKey());
        Datastore databaseConnector = Room.databaseBuilder(this.context, Datastore.class,
                Datastore.DatabaseName).build();
        final long[] gatewayServerInsertId = {-1};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GatewayServersDAO gatewayServersDAO = databaseConnector.gatewayServersDAO();
                gatewayServerInsertId[0] = gatewayServersDAO.insert(gatewayServer);
                Log.d(getClass().getSimpleName(), "Added new gateway...");
            }
        });
        thread.start();
        thread.join();
        return gatewayServerInsertId[0];
    }

    public boolean hasPublicKey(GatewayServers gatewayServers) {
        return false;
    }

    public static String buildKeyStoreAlias(String gatewayServerUrl) {
        return gatewayServerUrl + "-keystore-alias";
    }
}

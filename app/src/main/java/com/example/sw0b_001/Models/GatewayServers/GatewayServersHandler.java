package com.example.sw0b_001.Models.GatewayServers;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;

import java.util.ArrayList;
import java.util.List;

public class GatewayServersHandler {

    private Context context;

    public GatewayServersHandler() {}

    public GatewayServersHandler(Context context) {
        this.context = context;
    }

    public long add(GatewayServer gatewayServer) throws InterruptedException {
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

    public void updateSeedsUrl(String seedsUrl, long gatewayServerId) throws InterruptedException {
        Datastore databaseConnector = Room.databaseBuilder(this.context, Datastore.class,
                Datastore.DatabaseName).build();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GatewayServersDAO gatewayServersDAO = databaseConnector.gatewayServersDAO();
                gatewayServersDAO.updateSeedsUrl(seedsUrl, gatewayServerId);
                Log.d(getClass().getSimpleName(), "Updated seedsUrl for gateway server");
            }
        });
        thread.start();
        thread.join();
    }

    public boolean hasPublicKey(GatewayServer gatewayServer) {
        return false;
    }

    public static String buildKeyStoreAlias(String gatewayServerUrl) {
        return gatewayServerUrl + "-keystore-alias";
    }

    public static List<GatewayServer> getAllGatewayServers(Context context) throws InterruptedException {
        Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                Datastore.DatabaseName).build();

        final List<GatewayServer>[] gatewayServerList = new List[]{new ArrayList<>()};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GatewayServersDAO gatewayServersDAO = databaseConnector.gatewayServersDAO();
                gatewayServerList[0] = gatewayServersDAO.getAll();
            }
        });
        thread.start();
        thread.join();

        return gatewayServerList[0];
    }
}

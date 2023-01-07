package com.example.sw0b_001.Security;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class SecurityHelpers {

    private static List<GatewayServer> getGatewayServers(Context context) throws Throwable {
        Datastore databaseConnection = Room.databaseBuilder(context,
                Datastore.class, Datastore.DatabaseName).build();
        final List<GatewayServer>[] gatewayServers = new List[]{new ArrayList<>()};
        Thread fetchGatewayClientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                GatewayServersDAO gatewayServerDao = databaseConnection.gatewayServersDAO();
                gatewayServers[0] = gatewayServerDao.getAll();
            }
        });

        try {
            fetchGatewayClientThread.start();
            fetchGatewayClientThread.join();
        } catch (InterruptedException e) {
            throw e.fillInStackTrace();
        }

        return gatewayServers[0];
    }

    public static byte[] getDecryptedSharedKey(Context context) throws Throwable {

        SecurityHandler securityHandler = new SecurityHandler(context);
        SecurityRSA securityRSA = new SecurityRSA(context);

        GatewayServer gatewayServer = getGatewayServers(context).get(0);
        String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServer.getUrl() );

        byte[] sharedKey = securityHandler.getSharedKey();
        byte[] decryptedSharedKey = securityRSA.decrypt(sharedKey, keystoreAlias);

        return decryptedSharedKey;
    }

    @NonNull
    public static String convert_to_pem_format(byte[] key) {
        String keyString = Base64.encodeToString(key, Base64.DEFAULT);
        keyString = "-----BEGIN PUBLIC KEY-----\n" + keyString;
        keyString += "-----END PUBLIC KEY-----";

        return keyString;
    }
}

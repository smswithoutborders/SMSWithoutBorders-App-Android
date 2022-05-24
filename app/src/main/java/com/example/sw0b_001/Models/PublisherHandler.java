package com.example.sw0b_001.Models;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Security.SecurityHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PublisherHandler {

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

    public static String getDecryptedEmailContent(Context context, String encryptedContent) throws Throwable {
        // Transform from Base64

        Log.d("", "** encrypted content: " + encryptedContent);
        String decodedEncryptedContent = new String(Base64.decode(encryptedContent, Base64.DEFAULT));
        Log.d("", "** decoded encrypted content: " + decodedEncryptedContent);

        String iv = decodedEncryptedContent.substring(0, 16);
        String encodedEncryptedContent = decodedEncryptedContent.substring(16);
        Log.d("", "** iv: " + iv);
        Log.d("", "** encoded encrypted content: " + encodedEncryptedContent);


        SecurityHandler securityHandler = new SecurityHandler(context);

        GatewayServer gatewayServer = getGatewayServers(context).get(0);
        String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServer.getUrl() );

        try {
            byte[] decryptedEmailContent = securityHandler.decryptWithSharedKeyAES(
                    iv.getBytes(), Base64.decode(encodedEncryptedContent, Base64.NO_WRAP), keystoreAlias);

            return new String(decryptedEmailContent, StandardCharsets.UTF_8);
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }


    public static String[] getEncryptEmailContent(Context context, String emailContent) throws Throwable {
        SecurityHandler securityHandler = new SecurityHandler(context);
        String randomStringForIv = securityHandler.generateRandom(16);

        GatewayServer gatewayServer = getGatewayServers(context).get(0);
        String keystoreAlias = GatewayServersHandler.buildKeyStoreAlias(gatewayServer.getUrl() );

        try {
            byte[] encryptedEmailContent = securityHandler.encryptWithSharedKeyAES(randomStringForIv.getBytes(), emailContent.getBytes(StandardCharsets.UTF_8), keystoreAlias);

            return new String[]{randomStringForIv, Base64.encodeToString(encryptedEmailContent, Base64.NO_WRAP)};
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }

    public static String formatForPublishing(Context context, String formattedContent) throws Throwable {
        try {
            String[] encryptedIVEmailContent = getEncryptEmailContent(context, formattedContent);

            String IV = encryptedIVEmailContent[0];
            String encryptedEmailContent = encryptedIVEmailContent[1];

            final String encryptedContent = IV + encryptedEmailContent;

            return Base64.encodeToString(encryptedContent.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }
}

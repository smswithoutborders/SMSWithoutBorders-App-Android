package com.example.sw0b_001.Models.Messages;

import android.content.Context;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;

import java.util.Date;

public class EncryptedContentHandler {

    public static void clearedStoredEncryptedContents(Context context) {
        Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                Datastore.databaseName).build();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EncryptedContentDAO encryptedContentDAO = databaseConnector.encryptedContentDAO();
                encryptedContentDAO.deleteAll();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void store(Context context, String encryptedContentBase64, String gatewayClientMSISDN, String platformName) throws InterruptedException {
        Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                Datastore.databaseName).build();

        EncryptedContent encryptedContent = new EncryptedContent();
        encryptedContent.setEncryptedContent(encryptedContentBase64);
        encryptedContent.setPlatformName(platformName);
        encryptedContent.setGatewayClientMSISDN(gatewayClientMSISDN);

        encryptedContent.setDate(new Date().getTime());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                EncryptedContentDAO encryptedContentDAO = databaseConnector.encryptedContentDAO();
                encryptedContentDAO.insert(encryptedContent);
            }
        });
        thread.start();
        // thread.join();
    }
}

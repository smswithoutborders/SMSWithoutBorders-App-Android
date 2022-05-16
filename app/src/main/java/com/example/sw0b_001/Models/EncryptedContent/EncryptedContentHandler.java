package com.example.sw0b_001.Models.EncryptedContent;

import android.content.Context;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;

import java.util.Date;

public class EncryptedContentHandler {

    public static void store(Context context, String encryptedContentBase64, String gatewayClientMSISDN, String platformName) throws InterruptedException {
        Datastore databaseConnector = Room.databaseBuilder(context, Datastore.class,
                Datastore.DatabaseName).build();

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

package com.example.sw0b_001.Models;

import android.content.Context;
import android.util.Base64;

import androidx.room.Room;

import com.example.sw0b_001.Database.Datastore;
import com.example.sw0b_001.Models.GatewayServers.GatewayServer;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersDAO;
import com.example.sw0b_001.Models.GatewayServers.GatewayServersHandler;
import com.example.sw0b_001.Security.SecurityAES;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityHelpers;
import com.example.sw0b_001.Security.SecurityRSA;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PublisherHandler {


    public static String decryptPublishedContent(Context context, String encryptedContent) throws Throwable {
        // Transform from Base64
        String decodedEncryptedContent = new String(Base64.decode(encryptedContent, Base64.DEFAULT));

        String iv = decodedEncryptedContent.substring(0, 16);
        String encodedEncryptedContent = decodedEncryptedContent.substring(16);

        SecurityAES securityAES = new SecurityAES(context);
        try {
            byte[] sharedKey = SecurityHelpers.getDecryptedSharedKey(context);
            byte[] decryptedEmailContent = securityAES.decrypt(
                    iv.getBytes(),
                    Base64.decode(encodedEncryptedContent, Base64.DEFAULT),
                    sharedKey);

            return new String(decryptedEmailContent, StandardCharsets.UTF_8);
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }


    public static String[] encryptContentForPublishing(Context context, String emailContent) throws Throwable {
        SecurityHandler securityHandler = new SecurityHandler(context);
        String randomStringForIv = securityHandler.generateRandom(16);

        SecurityAES securityAES = new SecurityAES(context);
        try {
            byte[] sharedKey = SecurityHelpers.getDecryptedSharedKey(context);
            byte[] encryptedContent = securityAES.encrypt(
                    randomStringForIv.getBytes(),
                    emailContent.getBytes(StandardCharsets.UTF_8),
                    sharedKey);

            return new String[]{randomStringForIv, Base64.encodeToString(encryptedContent, Base64.NO_WRAP)};
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }

    public static String formatForPublishing(Context context, String formattedContent) throws Throwable {
        try {
            String[] encryptedIVEmailContent = encryptContentForPublishing(context, formattedContent);

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

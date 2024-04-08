package com.example.sw0b_001.Models;

import android.content.Context;
import android.util.Base64;

import com.example.sw0b_001.Security.SecurityAES;
import com.example.sw0b_001.Security.SecurityHelpers;

import java.nio.charset.StandardCharsets;

public class PublisherHandler {

    // TODO: clean up methods using this method
    public static String decryptPublishedContent(Context context, String encryptedContent) throws Throwable {
        // Transform from Base64

        byte[] decodedEncryptedContent = Base64.decode(encryptedContent, Base64.DEFAULT);

        SecurityAES securityAES = new SecurityAES(context);
        try {
            byte[] sharedKey = SecurityHelpers.getDecryptedSharedKey(context);
            byte[] encodedContent = new byte[decodedEncryptedContent.length - 16];

            System.arraycopy(decodedEncryptedContent, 16, encodedContent, 0, encodedContent.length);
            byte[] decodedContent = Base64.decode(encodedContent, Base64.DEFAULT);
            byte[] originalContent = new byte[16 + decodedContent.length];

            // copy iv
            System.arraycopy(decodedEncryptedContent, 0, originalContent, 0, 16);
            // copy content
            System.arraycopy(decodedContent, 0, originalContent, 16, decodedContent.length);

            byte[] decryptedEmailContent = securityAES.decrypt(originalContent, sharedKey);

            return new String(decryptedEmailContent, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new Throwable(e);
        }
    }


    public static byte[] encryptContentForPublishing(Context context, String emailContent) throws Throwable {
        SecurityAES securityAES = new SecurityAES(context);
        try {
            byte[] sharedKey = SecurityHelpers.getDecryptedSharedKey(context);
            return securityAES.encrypt(emailContent.getBytes(StandardCharsets.UTF_8),
                    sharedKey);

        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }

    public static String formatForPublishing(Context context, String formattedContent) throws Throwable {
        try {
            byte[] encryptedContent = encryptContentForPublishing(context, formattedContent);

            byte[] iv = new byte[16];
            byte[] content = new byte[encryptedContent.length - 16];

            System.arraycopy(encryptedContent, 0, iv, 0, iv.length);
            System.arraycopy(encryptedContent, 16, content, 0, content.length);
            byte[] encodedContent = Base64.encode(content, Base64.DEFAULT);
            byte[] finalContent = new byte[16 + encodedContent.length];

            System.arraycopy(iv, 0, finalContent, 0, iv.length);
            System.arraycopy(encodedContent, 0, finalContent, 16, encodedContent.length);

            return Base64.encodeToString(finalContent, Base64.DEFAULT);
        }
        catch(Exception e ) {
            throw new Throwable(e);
        }
    }
}

package com.example.sw0b_001.Models;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.sw0b_001.BuildConfig;
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
import java.util.concurrent.Executor;

public class PublisherHandler {

    public static String decryptPublishedContent(Context context, String encryptedContent) throws Throwable {
        // Transform from Base64
        Log.d(PublisherHandler.class.getName(), "Yes unlocked!");
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
        } catch (Exception e) {
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

package com.example.sw0b_001.Models;

import android.content.Intent;
import android.net.Uri;

public class SMSHandler {
    public static Intent transferToDefaultSMSApp(String gatewayClientMSISDN, String encryptedContent) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + gatewayClientMSISDN));
        intent.putExtra("sms_body", encryptedContent);

        return intent;
    }
}

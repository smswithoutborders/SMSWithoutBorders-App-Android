package com.example.sw0b_001.Models

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.sw0b_001.R

class SMSHandler {
    companion object {
        fun transferToDefaultSMSApp(gatewayClientMSISDN: String,
                                    encryptedContent: String?): Intent {
            val intent = Intent().apply {
                type = "text/plain"
                action = Intent.ACTION_SEND
                setData(Uri.parse("smsto:" + gatewayClientMSISDN))
                putExtra("sms_body", encryptedContent)
            }
            return intent
        }
    }
}

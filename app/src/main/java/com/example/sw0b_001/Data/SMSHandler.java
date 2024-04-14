package com.example.sw0b_001.Data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.example.sw0b_001.R;

import java.util.List;

public class SMSHandler {
    public static Intent transferToDefaultSMSApp(Context context, String gatewayClientMSISDN, String encryptedContent) throws Exception {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + gatewayClientMSISDN));
        intent.putExtra("sms_body", encryptedContent);

        List<ResolveInfo> possibleActivitiesList = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_ALL);

        // Verify that an activity in at least two apps on the user's device
        // can handle the intent. Otherwise, start the intent only if an app
        // on the user's device can handle the intent.
        if (possibleActivitiesList.size() > 1) {

            // Create intent to show chooser.
            // Title is something similar to "Share this photo with."

            String title = context.getResources().getString(R.string.choose_sms_app);
            Intent chooser = Intent.createChooser(intent, title);
            return chooser;
        } else if (intent.resolveActivity(context.getPackageManager()) == null) {
            throw new Exception();
        }

        return intent;
    }
}

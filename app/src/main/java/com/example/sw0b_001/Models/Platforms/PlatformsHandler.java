package com.example.sw0b_001.Models.Platforms;

import android.content.Context;
import android.content.Intent;

import com.example.sw0b_001.EmailThreadsActivity;
import com.example.sw0b_001.TextThreadActivity;

public class PlatformsHandler {
    static public Intent getIntent(Context context, String platform, String type) {
        Intent intent = new Intent();
        switch(type) {
            case "email": {
                intent = new Intent(context, EmailThreadsActivity.class);
                intent.putExtra("platform", platform);
                break;
            }

            case "text": {
                intent = new Intent(context, TextThreadActivity.class);
                intent.putExtra("platform", platform);
                break;
            }
            // TODO: put a default here
        }
        return intent;
    }
}

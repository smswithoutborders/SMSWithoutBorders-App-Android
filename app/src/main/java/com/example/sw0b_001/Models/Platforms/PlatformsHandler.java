package com.example.sw0b_001.Models.Platforms;

import android.content.Context;
import android.content.Intent;

import com.example.sw0b_001.EmailComposeActivity;
import com.example.sw0b_001.TextThreadActivity;

public class PlatformsHandler {
    static public Intent getIntent(Context context, String platform_name, String type) {
        Intent intent = null;
        switch(type) {
            case "email": {
                intent = new Intent(context, EmailComposeActivity.class);
                break;
            }

            case "text": {
                intent = new Intent(context, TextThreadActivity.class);
                break;
            }
            // TODO: put a default here
        }
        if(intent != null ) {
            intent.putExtra("platform_name", platform_name);
        }
        return intent;
    }
}

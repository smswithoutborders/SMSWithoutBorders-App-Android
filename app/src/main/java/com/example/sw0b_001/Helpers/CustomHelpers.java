package com.example.sw0b_001.Helpers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.room.Room;

import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomHelpers {
    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
    public static int getLetterImage(char letter) {
        int image = R.drawable.ic_round_message_24;
        switch (letter) {
            case 'i':
                image = R.mipmap.letter_i;
            break;

            case 's':
                image = R.mipmap.letter_s;
            break;
        }
        return image;
    }

    public static void sendEmailSMS(Context context, String text, String phonenumber, long emailId) {
        //TODO: Research what to do in case of a double sim phone
        //---when the SMS has been sent---
        String SMS_SENT = "SENT";
        String SMS_DELIVERED = "DELIVERED";
        Intent for_sentPendingIntent = new Intent(SMS_SENT);
        for_sentPendingIntent.putExtra("email_id", emailId);
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, 0, for_sentPendingIntent, 0);
        Intent for_deliveredPendingIntent = new Intent(SMS_DELIVERED);
        for_deliveredPendingIntent.putExtra("email_id", emailId);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(context, 0, for_deliveredPendingIntent, 0);
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Thread storeEmailMessage;
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent",
                                Toast.LENGTH_LONG).show();

                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("sent", emailId);
                            }
                        });
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("Generic failure", emailId);
                            }
                        });
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service",
                                Toast.LENGTH_SHORT).show();
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("No service", emailId);
                            }
                        });
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("Null PDU", emailId);
                            }
                        });
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("Radio off", emailId);
                            }
                        });
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + getResultCode());
                }
                storeEmailMessage.start();
                try {
                    storeEmailMessage.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, new IntentFilter(SMS_SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                Thread storeEmailMessage;
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_LONG).show();
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("delivered", emailId);
                            }
                        });
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_LONG).show();
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("not delivered", emailId);
                            }
                        });
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + getResultCode());
                }
                storeEmailMessage.start();
                try {
                    storeEmailMessage.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, new IntentFilter(SMS_DELIVERED));
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phonenumber, null, text, sentPendingIntent, deliveredPendingIntent);

        Toast.makeText(context, "Sending SMS....", Toast.LENGTH_LONG).show();
    }
}

package com.example.sw0b_001.Helpers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;

import com.example.sw0b_001.EmailThreadActivity;
import com.example.sw0b_001.EmailThreadsActivity;
import com.example.sw0b_001.Providers.Emails.EmailMessageDao;
import com.example.sw0b_001.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class CustomHelpers {
    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
    public static int getLetterImage(char letter) {
        int image = R.drawable.ic_round_message_24;
        switch (letter) {
            case 'a':
                image = R.mipmap.letter_a;
                break;
            case 'b':
                image = R.mipmap.letter_b;
                break;
            case 'c':
                image = R.mipmap.letter_c;
                break;
            case 'd':
                image = R.mipmap.letter_d;
                break;
            case 'e':
                image = R.mipmap.letter_e;
                break;
            case 'f':
                image = R.mipmap.letter_f;
                break;
            case 'g':
                image = R.mipmap.letter_g;
                break;
            case 'h':
                image = R.mipmap.letter_h;
                break;
            case 'i':
                image = R.mipmap.letter_i;
                break;
            case 'j':
                image = R.mipmap.letter_j;
                break;
            case 'k':
                image = R.mipmap.letter_k;
                break;
            case 'l':
                image = R.mipmap.letter_l;
                break;
            case 'm':
                image = R.mipmap.letter_m;
                break;
            case 'n':
                image = R.mipmap.letter_n;
                break;
            case 'o':
                image = R.mipmap.letter_o;
                break;
            case 'p':
                image = R.mipmap.letter_p;
                break;
            case 'q':
                image = R.mipmap.letter_q;
                break;
            case 'r':
                image = R.mipmap.letter_r;
                break;
            case 's':
                image = R.mipmap.letter_s;
                break;
            case 't':
                image = R.mipmap.letter_t;
                break;
            case 'u':
                image = R.mipmap.letter_u;
                break;
            case 'v':
                image = R.mipmap.letter_v;
                break;
            case 'w':
                image = R.mipmap.letter_w;
                break;
            case 'x':
                image = R.mipmap.letter_x;
                break;
            case 'y':
                image = R.mipmap.letter_y;
            break;
            case 'z':
                image = R.mipmap.letter_z;
            break;
        }
        return image;
    }

    public static void sendEmailSMS(Context context, String text, String phonenumber, long emailId) {
        //TODO: Research what to do in case of a double sim phone
        //---when the SMS has been sent---
        String SMS_SENT = "SENT";
        String SMS_DELIVERED = "DELIVERED";


        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Thread storeEmailMessage;
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:

                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("sent", intent.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(context, "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                Log.i(this.getClass().getSimpleName(), "Event for Email: " + intent.getLongExtra("email_id", 0));
                                platformsDao.updateStatus("Generic failure", intent.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(context, "Generic failure",
                                Toast.LENGTH_SHORT).show();
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
                                platformsDao.updateStatus("No service", intent.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
                                platformsDao.updateStatus("Null PDU", intent.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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
                                platformsDao.updateStatus("Radio off", intent.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + getResultCode());
                }
                Intent bIntent = new Intent("sms_state_changed");
                LocalBroadcastManager.getInstance(context).sendBroadcast(bIntent);
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
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("delivered", arg1.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DBName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
                                platformsDao.updateStatus("not delivered", arg1.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + getResultCode());
                }
                Intent bIntent = new Intent("sms_state_changed");
                LocalBroadcastManager.getInstance(context).sendBroadcast(bIntent);
            }
        }, new IntentFilter(SMS_DELIVERED));

        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> texts = smsManager.divideMessage(text);

        ArrayList<PendingIntent> listSentPendingIntent = new ArrayList<>();
        ArrayList<PendingIntent> listDeliveredPendingIntent = new ArrayList<>();


        for(int i=0;i<texts.size();++i) {
            int number = (int) (new Random().nextDouble()*100L);
            Intent for_sentPendingIntent = new Intent(SMS_SENT);
            for_sentPendingIntent.putExtra("email_id", emailId);
            PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, number, for_sentPendingIntent, 0);

            Intent for_deliveredPendingIntent = new Intent(SMS_DELIVERED);
            for_deliveredPendingIntent.putExtra("email_id", emailId);
            PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(context, number, for_deliveredPendingIntent, 0);

            listSentPendingIntent.add(sentPendingIntent);
            listDeliveredPendingIntent.add(deliveredPendingIntent);
        }

        smsManager.sendMultipartTextMessage(phonenumber, null, texts, listSentPendingIntent, listDeliveredPendingIntent);

        Toast.makeText(context, "Sending SMS....", Toast.LENGTH_LONG).show();
    }
}

package com.example.sw0b_001.Helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomHelpers {
    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-d HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }


    /*
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
                                        Datastore.class, Datastore.DatabaseName).build();

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
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DatabaseName).build();

                                EmailMessageDao platformsDao = emailStoreDb.emailDao();
//                                Log.i(this.getClass().getSimpleName(), "Event for Email: " + intent.getLongExtra("email_id", 0));
                                platformsDao.updateStatus("Generic failure", intent.getLongExtra("email_id", 0));
                            }
                        });
                        storeEmailMessage.start();
                        try {
                            storeEmailMessage.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DatabaseName).build();

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
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DatabaseName).build();

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
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DatabaseName).build();

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
                                        Datastore.class, Datastore.DatabaseName).build();

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
                        break;
                    case Activity.RESULT_CANCELED:
                        storeEmailMessage = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Datastore emailStoreDb = Room.databaseBuilder(context,
                                        Datastore.class, Datastore.DatabaseName).build();

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

     */
}

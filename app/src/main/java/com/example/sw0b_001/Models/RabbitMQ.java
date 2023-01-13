package com.example.sw0b_001.Models;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.example.sw0b_001.BuildConfig;
import com.example.sw0b_001.R;
import com.example.sw0b_001.Security.SecurityHandler;
import com.example.sw0b_001.Security.SecurityHelpers;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {

    ConnectionFactory factory = new ConnectionFactory();
    Connection connection;
    Channel channel;

    String queue_name = "";
    String exchange_name = "";

    String msisdnHash = "";

    boolean durable = true;    //durable - RabbitMQ will never lose the queue if a crash occurs
    boolean exclusive = false;  //exclusive - if queue only will be used by one connection
    boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

    public RabbitMQ(Context context) throws Throwable {
        // TODO: hide credentials from leaking
        SecurityHandler securityHandler = new SecurityHandler(context);

        msisdnHash = securityHandler.getMSISDN();

        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "MSISDN fetched: " + msisdnHash);

        String sharedKey = securityHandler.getSharedKeyNoneBase64();

        String sharedKeyURLEncoded = URLEncoder.encode(sharedKey, "UTF-8");

        String uri = "amqp://" + msisdnHash + ":" + sharedKeyURLEncoded + "@" + context.getString(R.string.notifications_url);

        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "AMP connection: " + uri);

        factory.setUri(uri);
        factory.setConnectionTimeout(30000);

        connection = factory.newConnection();
        channel = connection.createChannel();

        queue_name = context.getString(R.string.notifications_queue_name) + msisdnHash;
        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "Queue name: " + queue_name);

        exchange_name = context.getString(R.string.notifications_exchange_name);
        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "Exchange name: " + exchange_name);
    }

    public void publish() throws IOException {
        channel.queueDeclare(queue_name, durable, exclusive, autoDelete, null);
        String message = "Hello CloudAMQP!";

        String routingKey = msisdnHash;
        channel.basicPublish(exchange_name, routingKey, null, message.getBytes());

        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "[+] RMQ published successfully!");
    }

    public void consume() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            if(BuildConfig.DEBUG)
                Log.d(getClass().getName(), "[x] Received '" + message + "'");
        };

        channel.queueDeclare(queue_name, durable, exclusive, autoDelete, null);
        channel.basicConsume(queue_name, true, deliverCallback, consumerTag -> { });
    }
}

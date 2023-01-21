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
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.TopologyRecoveryException;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

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

    String connectionName = "Android-User";

    boolean durable = true;    //durable - RabbitMQ will never lose the queue if a crash occurs
    boolean exclusive = false;  //exclusive - if queue only will be used by one connection
    boolean autoDelete = false; //autodelete - queue is deleted when last consumer unsubscribes

    public RabbitMQ(Context context) throws Throwable {
        // TODO: hide credentials from leaking
        // https://www.rabbitmq.com/api-guide.html#connecting
        SecurityHandler securityHandler = new SecurityHandler(context);

        msisdnHash = securityHandler.getMSISDN();

        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "MSISDN fetched: " + msisdnHash);

        String sharedKey = securityHandler.getSharedKeyNoneBase64();

        factory.setUsername(msisdnHash);
        factory.setPassword(sharedKey);
        factory.setVirtualHost("/");
        factory.setHost(context.getString(R.string.notifications_url));
        factory.setPort(5672);
        factory.setConnectionTimeout(10000);

        setFactoryExceptionHandlers();

        queue_name = context.getString(R.string.notifications_queue_name) + msisdnHash;
        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "Queue name: " + queue_name);

        exchange_name = context.getString(R.string.notifications_exchange_name);
        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "Exchange name: " + exchange_name);
    }

    public Connection getConnection() {
        return this.connection;
    }

    public boolean isOpen() {
        return connection != null && connection.isOpen();
    }

    public void startConnection() throws IOException, TimeoutException {
        connection = factory.newConnection(connectionName);
        channel = connection.createChannel();
    }

    private void setFactoryExceptionHandlers() {
        factory.setExceptionHandler(new DefaultExceptionHandler());
    }

    public void publish(String message) throws IOException {
        channel.queueDeclare(queue_name, durable, exclusive, autoDelete, null);

        String routingKey = msisdnHash;
        channel.basicPublish(exchange_name, routingKey, null, message.getBytes());

        if(BuildConfig.DEBUG)
            Log.d(getClass().getName(), "[+] RMQ published successfully!");
    }

    public void consume(DeliverCallback deliverCallback) throws IOException {
        channel.queueDeclare(queue_name, durable, exclusive, autoDelete, null);
        channel.basicConsume(queue_name, true, deliverCallback, consumerTag -> { });
    }
}

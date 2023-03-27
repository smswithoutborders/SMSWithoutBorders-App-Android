package com.example.sw0b_001.Models;

import android.content.Context;

import com.example.sw0b_001.R;
import com.example.sw0b_001.Security.SecurityHandler;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

import java.io.IOException;
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

        String sharedKey = securityHandler.getEncryptedBase64SharedKey();

        factory.setUsername(msisdnHash);
        factory.setPassword(sharedKey);
        factory.setVirtualHost("/");
        factory.setHost(context.getString(R.string.notifications_url));
        factory.setPort(5672);
        factory.setConnectionTimeout(10000);

        setFactoryExceptionHandlers();

        queue_name = context.getString(R.string.notifications_queue_name) + msisdnHash;

        exchange_name = context.getString(R.string.notifications_exchange_name);
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
    }

    public void consume(DeliverCallback deliverCallback) throws IOException {
        channel.queueDeclare(queue_name, durable, exclusive, autoDelete, null);
        channel.basicConsume(queue_name, true, deliverCallback, consumerTag -> { });
    }
}

package com.netease.mq.rabbitmq.topic;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceiveLogTopic implements Runnable{

    private static final String EXCHANGE_NAME = "topic_logs";

    public ReceiveLogTopic(String bindingKey){
        this.bindingKey = bindingKey;
    }

    public String getBindingKey() {
        return bindingKey;
    }

    public void setBindingKey(String bindingKey) {
        this.bindingKey = bindingKey;
    }

    private String bindingKey;

    public static void main(String[] argv) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.submit(new ReceiveLogTopic("#"));
        executorService.submit(new ReceiveLogTopic("kern.*"));
        executorService.submit(new ReceiveLogTopic("*.critical"));
    }


    public void run() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            String queueName = channel.queueDeclare().getQueue();

            if (bindingKey == null) {
                System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
                System.exit(1);
            }
            channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);


            System.out.println(Thread.currentThread().getName() + " [" + bindingKey + "] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(Thread.currentThread().getName() + " [" + bindingKey + "] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}

package com.netease.mq.rabbitmq.pubsub;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReceiveLogs implements Runnable{
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(new ReceiveLogs());
        executorService.submit(new ReceiveLogs());
        executorService.submit(new ReceiveLogs());
    }

    public void run() {
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x]" + Thread.currentThread().getName() + " Received '" + message + "'");
                }
            };
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}

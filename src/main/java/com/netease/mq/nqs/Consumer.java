package com.netease.mq.nqs;


import com.netease.cloud.nqs.client.ClientConfig;
import com.netease.cloud.nqs.client.Message;
import com.netease.cloud.nqs.client.MessageSessionFactory;
import com.netease.cloud.nqs.client.SimpleMessageSessionFactory;
import com.netease.cloud.nqs.client.consumer.ConsumerConfig;
import com.netease.cloud.nqs.client.consumer.MessageConsumer;
import com.netease.cloud.nqs.client.consumer.MessageHandler;
import com.netease.cloud.nqs.client.exception.MessageClientException;

public class Consumer {

    public static void main(String agrs[]) throws MessageClientException {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setHost("127.0.0.1");
        clientConfig.setPort(5672);
        clientConfig.setProductId("/"); // Use vhost
        clientConfig.setAccessSecret("guest");
        clientConfig.setAccessKey("guest");
        clientConfig.setAuthMechanism(ClientConfig.AUTH_PLAIN);
        MessageSessionFactory simpleMessageSessionFactory = new SimpleMessageSessionFactory(clientConfig);


        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setProductId("/");
        consumerConfig.setQueueName("nqs_queue");
        consumerConfig.setGroup("consumer_group");
        consumerConfig.setRequireAck(true);

        MessageConsumer consumer = simpleMessageSessionFactory.createConsumer(consumerConfig);
        try {
            while (true){
                consumer.consumeMessage(new MessageHandler() {
                    public boolean handle(Message message) {
                        System.out.println("received: " + new String(message.getBody()));
                        return true;

                    }
                });
            }

        } catch (MessageClientException var1) {
            if (consumer != null) {
                try {
                    consumer.shutdown();
                } catch (Exception var2) {
                    // ignore
                } }
            consumer = simpleMessageSessionFactory.createConsumer(consumerConfig);
            var1.printStackTrace();
        }

    }
}

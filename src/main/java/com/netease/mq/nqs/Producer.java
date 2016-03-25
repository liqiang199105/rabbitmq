package com.netease.mq.nqs;


import com.netease.cloud.nqs.client.ClientConfig;
import com.netease.cloud.nqs.client.Message;
import com.netease.cloud.nqs.client.MessageSessionFactory;
import com.netease.cloud.nqs.client.SimpleMessageSessionFactory;
import com.netease.cloud.nqs.client.exception.MessageClientException;
import com.netease.cloud.nqs.client.producer.MessageProducer;
import com.netease.cloud.nqs.client.producer.ProducerConfig;

import java.util.concurrent.TimeUnit;

public class Producer {

    public static void main(String args[]) throws InterruptedException, MessageClientException {
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.setHost("127.0.0.1");
//        clientConfig.setPort(5672);
//        clientConfig.setProductId("9159c0613f5f486aacacb9f677dfe6bc");
//        clientConfig.setAccessKey("81f4f75ba4634796838ef01c725fd5e0");
//        clientConfig.setAccessSecret("7ec03b1bbd254c94b6091583f1f67439");

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setHost("127.0.0.1");
        clientConfig.setPort(5672);
        clientConfig.setProductId("/"); // Use vhost
        clientConfig.setAccessSecret("guest");
        clientConfig.setAccessKey("guest");
        clientConfig.setAuthMechanism(ClientConfig.AUTH_PLAIN);

        MessageSessionFactory simpleMessageSessionFactory = new SimpleMessageSessionFactory(clientConfig);
        ProducerConfig producerConfig = new ProducerConfig();
        producerConfig.setProductId("/");
        producerConfig.setQueueName("nqs_queue");
        MessageProducer producer = simpleMessageSessionFactory.createProducer(producerConfig);

        while (true){
            Message message = new Message("hello world".getBytes());
            try {
                producer.sendMessage(message);
                break;
            } catch (Exception var1) {
                if (producer != null) {
                    try {
                        producer.shutdown();
                    } catch (Exception var2) {
                        // ignore
                    } }

                TimeUnit.SECONDS.sleep(5000);
                producer = simpleMessageSessionFactory.createProducer(producerConfig);
            }
        }
    }
}

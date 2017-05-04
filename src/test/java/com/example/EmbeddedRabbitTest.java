package com.example;

import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class EmbeddedRabbitTest {

    private void createExchange(final String identifier) {
        final CachingConnectionFactory cf = new CachingConnectionFactory(EmbeddedBroker.BROKER_PORT);
        final RabbitAdmin admin = new RabbitAdmin(cf);
        final Queue queue = new Queue("myQueue", false);
        admin.declareQueue(queue);
        final TopicExchange exchange = new TopicExchange("myExchange");
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with("#"));
        cf.destroy();
    }

    private void deleteExchange() {
        final CachingConnectionFactory cf = new CachingConnectionFactory(EmbeddedBroker.BROKER_PORT);
        final RabbitAdmin admin = new RabbitAdmin(cf);
        admin.deleteExchange("myExchange");
        cf.destroy();
    }

    public void sendMessage() throws Exception {
        final CachingConnectionFactory cf = new CachingConnectionFactory(EmbeddedBroker.BROKER_PORT);
        final RabbitTemplate template = new RabbitTemplate(cf);
        final String message = "hello world message!";
        template.convertAndSend("myExchange", "#", message);
        cf.destroy();
        // waitForMessageBeConsumed();
    }

    public void readMessage() throws Exception {
        final CachingConnectionFactory cf = new CachingConnectionFactory(EmbeddedBroker.BROKER_PORT);
        final RabbitTemplate template = new RabbitTemplate(cf);
        Message message = template.receive("myQueue");
        System.out.println("received message:" + new String(message.getBody()));
    }


    @Before
    public void setupBroker() throws Exception {
        EmbeddedBroker broker = new EmbeddedBroker();

        createExchange("myIdentifier");
        sendMessage();

    }

    @Test
    public void sendAndReceive() throws Exception {
        sendMessage();
        readMessage();
    }

}

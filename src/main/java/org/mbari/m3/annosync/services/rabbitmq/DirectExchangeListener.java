package org.mbari.m3.annosync.services.rabbitmq;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.mbari.m3.annosync.ConceptNameChangedMsg;
import org.mbari.m3.annosync.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2018-01-26T09:14:00
 */
public class DirectExchangeListener {

    private final String host;
    private final Integer port;
    private final String exchange;
    private final String routingKey;
    private final String username;
    private final String password;
    private Optional<String> virtualHost;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public DirectExchangeListener(String host,
            Integer port, String exchange, String routingKey,
            String username, String password, String virtualHost) {
        this.host = host;
        this.port = port;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.username = username;
        this.password = password;
        this.virtualHost = Optional.ofNullable(virtualHost);
    }

    public DirectExchangeListener(String host,
            Integer port, String exchange, String routingKey,
            String username, String password) {
        this(host, port, exchange, routingKey, username, password, null);
        try {
            listen();
        }
        catch (Exception e) {
            log.error("Failed to listen to rabbitmq on " + host, e);
        }
    }

    private void listen() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setUsername(username);
        virtualHost.ifPresent(factory::setVirtualHost);
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchange, "direct");
        final String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, routingKey);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);

                // parse message as JSON
                final ConceptNameChangedMsg conceptNameChangedMsg =
                        gson.fromJson(message, ConceptNameChangedMsg.class);

                // push data onto EventBus
                Initializer.NAME_CHANGE_SUBJECT.onNext(conceptNameChangedMsg);
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}

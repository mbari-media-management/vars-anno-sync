package org.mbari.m3.annosync.services.rabbitmq;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mbari.m3.annosync.ConceptNameChangedMsg;
import org.mbari.m3.annosync.util.ConfigUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * @author Brian Schlining
 * @since 2018-03-20T11:34:00
 */
public class RabbitMQPublisher {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String host;
    private final Integer port;
    private final String exchange;
    private final String routingKey;
    private final String username;
    private final String password;
    private Optional<String> virtualHost;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public RabbitMQPublisher(Config config) {
        host = config.getString("rabbitmq.host");
        port = config.getInt("rabbitmq.port");
        exchange = config.getString("rabbitmq.exchange");
        routingKey = config.getString("rabbitmq.routing.key");
        username = config.getString("rabbitmq.username");
        password = config.getString("rabbitmq.password");
        try {
            String vh = config.getString("rabbitmq.virtualhost");
            virtualHost = Optional.ofNullable(vh);
        }
        catch (Exception e) {
            virtualHost = Optional.empty();
        }
    }

    public void send(String newConcept, Collection<String> oldConcepts) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setUsername(username);
        virtualHost.ifPresent(factory::setVirtualHost);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(exchange, "direct");
            ConceptNameChangedMsg msg = new ConceptNameChangedMsg(newConcept,
                    new ArrayList<>(oldConcepts));
            byte[] bytes = gson.toJson(msg).getBytes(StandardCharsets.UTF_8);
            if (log.isDebugEnabled()) {
                log.debug("Sending message: " + new String(bytes, StandardCharsets.UTF_8));
            }
            channel.basicPublish(exchange, routingKey, null, bytes);
            channel.close();
            connection.close();
        }
        catch (TimeoutException | IOException e) {
            log.warn("Failed to connect to RabbitMQ", e);
        }
    }
}

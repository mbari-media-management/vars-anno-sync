package org.mbari.m3.annosync.services.rabbitmq;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.mbari.m3.annosync.ConceptNameChangedMsg;
import org.mbari.m3.annosync.util.ConfigUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2018-03-19T10:46:00
 */
public class RabbitMQDemo {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Subject<Object> rxSubject = PublishSubject.create().toSerialized();
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    private final DirectExchangeListener directExchangeListener;
    private final RabbitMQPublisher publisher;

    public RabbitMQDemo(Config config) {


        rxSubject.ofType(ConceptNameChangedMsg.class)
                .subscribeOn(Schedulers.newThread())
                .subscribe(msg -> {
                    String s = gson.toJson(msg);
                    log.info("Received: {}", s);
                });

        directExchangeListener = DirectExchangeListenerFactory.load(rxSubject, config);
        publisher = new RabbitMQPublisher(config);
    }

    public void send(String newName, Collection<String> oldNames) {
        publisher.send(newName, oldNames);
    }

    public static void main(String[] args) throws Exception{
        File configFile = ConfigUtilities.toFile(RabbitMQDemo.class.getResource("/test.conf"));
        final Config config = ConfigFactory.parseFile(configFile).resolve();
        RabbitMQDemo demo = new RabbitMQDemo(config);
        Thread t = new Thread(() -> {
           demo.send("Foo", List.of("Bar", "Baz"));
        });
        t.setDaemon(true);
        t.start();
        Thread.sleep(3000L);
        demo.rxSubject.onComplete();
    }



}

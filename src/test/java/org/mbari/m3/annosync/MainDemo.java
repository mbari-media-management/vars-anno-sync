package org.mbari.m3.annosync;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.mbari.m3.annosync.services.annosaurus.v1.AnnotationServiceImpl;
import org.mbari.m3.annosync.services.rabbitmq.DirectExchangeListener;
import org.mbari.m3.annosync.services.rabbitmq.DirectExchangeListenerFactory;
import org.mbari.m3.annosync.services.rabbitmq.RabbitMQPublisher;
import org.mbari.m3.annosync.util.ConfigUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.io.File;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2018-03-20T10:39:00
 */
public class MainDemo {

    private static DirectExchangeListener listener;
    private static List<AnnotationServiceImpl> annosaurus;
    private static List<org.mbari.m3.annosync.services.varsjdbc.AnnotationServiceImpl> vars;
    public static void main(String[] args) throws Exception {

        Logger log = LoggerFactory.getLogger(MainDemo.class);

        // --- Listen for rabbit MQ messages
        final Subject<Object> nameChangeBus = PublishSubject.create().toSerialized();
        final File configFile = ConfigUtilities.toFile(MainDemo.class.getResource("/mbari_development.conf"));
        final Config config = ConfigFactory.parseFile(configFile).resolve();

        Thread appThread = new Thread(() -> {
            listener = DirectExchangeListenerFactory.load(nameChangeBus, config);


            // Use rest to connect to annosaurus and update annotations and associations
            annosaurus = org.mbari.m3.annosync.services.annosaurus.v1
                    .AnnotationServiceFactory.load(nameChangeBus, config);

            // Use JDBC to connect to VARS and update annotations and associations
            vars = org.mbari.m3.annosync.services.varsjdbc
                    .AnnotationServiceFactory.load(nameChangeBus, config);

            RabbitMQPublisher pub = new RabbitMQPublisher(config);
            pub.send("Grimpoteuthis Rex", List.of("Grimpoteuthis"));
        }, MainDemo.class.getSimpleName());
        appThread.setDaemon(false);
        appThread.start();




        Console c = System.console();
        if (c != null) {
            // printf-like arguments
            c.format("\nPress ENTER to exit.\n");
            c.readLine();
        }

    }
}

package org.mbari.m3.annosync;

import com.typesafe.config.Config;
import io.reactivex.subjects.Subject;
import org.mbari.m3.annosync.services.rabbitmq.DirectExchangeListener;
import org.mbari.m3.annosync.services.rabbitmq.DirectExchangeListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2018-01-22T16:10:00
 */
public class Main {

    private static DirectExchangeListener listener;
    private static List<org.mbari.m3.annosync.services.annosaurus.v1.AnnotationServiceImpl> annosaurus;
    private static List<org.mbari.m3.annosync.services.varsjdbc.AnnotationServiceImpl> vars;
    public static void main(String[] args) throws Exception {

        Logger log = LoggerFactory.getLogger(Main.class);

        // --- Listen for rabbit MQ messages
        final Config config = Initializer.getConfig();
        Subject<Object> nameChangeBus = Initializer.NAME_CHANGE_SUBJECT;

        Thread appThread = new Thread(() -> {
            listener = DirectExchangeListenerFactory.load(nameChangeBus, config);


            // Use rest to connect to annosaurus and update annotations and associations
            annosaurus = org.mbari.m3.annosync.services.annosaurus.v1
                    .AnnotationServiceFactory.load(nameChangeBus, config);

            // Use JDBC to connect to VARS and update annotations and associations
            vars = org.mbari.m3.annosync.services.varsjdbc
                    .AnnotationServiceFactory.load(nameChangeBus, config);
        }, Main.class.getSimpleName());
        appThread.setDaemon(false);
        appThread.start();

    }
}

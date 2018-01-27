package org.mbari.m3.annosync;

import com.typesafe.config.Config;
import org.mbari.m3.annosync.services.annosaurus.v1.AnnotationServiceImpl;
import org.mbari.m3.annosync.services.rabbitmq.DirectExchangeListener;

/**
 * @author Brian Schlining
 * @since 2018-01-22T16:10:00
 */
public class Main {
    public static void main(String[] args) {

        // --- Listen for rabbit MQ messageds
        final Config config = Initializer.getConfig();
        //new DirectExchangeListener()

        // Use rest to connec to annosaurus and update annotations and associations
        new org.mbari.m3.annosync.services.annosaurus.v1.AnnotationServiceImpl()

        // Use JDBC to connect to VARS and update annotations and associations
        // new org.mbari.m3.annosync.services.varsjdbc.AnnotationServiceImpl

        System.out.println("Hello World");
    }
}

package org.mbari.m3.annosync.services.rabbitmq;

import com.typesafe.config.Config;
import io.reactivex.subjects.Subject;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2018-01-29T14:00:00
 */
public class DirectExchangeListenerFactory {

    public static DirectExchangeListener load(Subject<Object> nameChangeBus, Config config) {
        String host = config.getString("rabbitmq.host");
        int port = config.getInt("rabbitmq.port");
        String exchange = config.getString("rabbitmq.exchange");
        String routingKey = config.getString("rabbitmq.routing.key");
        String pwd = config.getString("rabbitmq.password");
        String user = config.getString("rabbitmq.username");

        Optional<String> virtualHost = Optional.empty();
        try {
            String vh = config.getString("rabbitmq.virtualhost");
            virtualHost = Optional.ofNullable(vh);
        }
        catch (Exception e) {
            // No nothing. No virtual host was defined.
        }

        if (virtualHost.isPresent()) {
            return new DirectExchangeListener(nameChangeBus, host, port, exchange,
                    routingKey, user, pwd, virtualHost.get());
        }
        else {
            return new DirectExchangeListener(nameChangeBus, host, port, exchange,
                    routingKey, user, pwd);
        }


    }
}

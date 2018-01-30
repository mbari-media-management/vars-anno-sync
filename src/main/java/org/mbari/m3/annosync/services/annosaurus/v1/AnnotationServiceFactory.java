package org.mbari.m3.annosync.services.annosaurus.v1;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigUtil;
import io.reactivex.Observable;
import org.mbari.m3.annosync.util.ConfigUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2018-01-29T10:16:00
 */
public class AnnotationServiceFactory {

    public static List<AnnotationServiceImpl> load(Observable<Object> nameChangeBus,
                                                   Config config) {

        try {
            List<String> paths = ConfigUtilities.getSubpaths("annosaurus", config);
            return paths.stream()
                    .map(path -> {
                        String url = config.getString(path + ".url");
                        Duration timeout = config.getDuration(path + ".timeout");
                        String secret = config.getString(path + "client.secret");
                        return new AnnotationServiceImpl(nameChangeBus, url, secret);
                    })
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LoggerFactory.getLogger(AnnotationServiceFactory.class)
                    .info("Unable to configure sync between knowledgebase and annosaurus", e);
            return new ArrayList<>();
        }
    }

}

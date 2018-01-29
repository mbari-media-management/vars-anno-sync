package org.mbari.m3.annosync.services.varsjdbc;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigUtil;
import io.reactivex.Observable;
import org.mbari.m3.annosync.util.ConfigUtilities;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2018-01-29T10:56:00
 */
public class AnnotationServiceFactory {

    public static List<AnnotationServiceImpl> load(Observable<Object> nameChangeBus,
                                                    Config config) {
        List<String> paths = ConfigUtilities.getSubpaths("vars", config);
        return paths.stream()
                .map( path -> {
                    String driver = config.getString(path + ".driver");
                    String pwd = config.getString(path + ".password");
                    String url = config.getString(path + ".url");
                    String user = config.getString(path + ".user");
                    return new AnnotationServiceImpl(nameChangeBus, url, user, pwd, driver);
                })
                .collect(Collectors.toList());
    }


}

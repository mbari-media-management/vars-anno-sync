package org.mbari.m3.annosync.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigUtil;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2018-01-29T11:03:00
 */
public class ConfigUtilities {

    /**
     * Extract subpaths below a given path
     * @param path
     * @param config
     * @return
     */
    public static List<String> getSubpaths(String path, Config config) {
        try {
            Config varsConfig = config.getConfig(path);
            List<String> paths = varsConfig.entrySet()
                    .stream()
                    .map(Map.Entry::getKey)
                    .map(ConfigUtil::splitPath)
                    .map(p -> p.get(0))
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            return paths.stream()
                    .map(p -> path + "." + p)
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LoggerFactory.getLogger(ConfigUtilities.class)
                    .info("Failed to parse sub-paths below " + path, e);
            return new ArrayList<>();
        }
    }

    public static File toFile(URL url) {
        File f;
        try {
            f = new File(url.toURI());
        }
        catch (URISyntaxException e) {
            f = new File(url.getPath());
        }
        return f;
    }

}

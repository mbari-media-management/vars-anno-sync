package org.mbari.m3.annosync.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2018-03-19T10:34:00
 */
public class ConfigUtilitiesTest {

    private final URL testConfigUrl = getClass().getResource("/test.conf");

    @Test
    public void testGetSubpaths() {
        File testFile = ConfigUtilities.toFile(testConfigUrl);
        Config testConfig = ConfigFactory.parseFile(testFile);
        List<String> subpaths = ConfigUtilities.getSubpaths("a", testConfig);
        assertEquals(subpaths.get(0), "a.b");
        assertEquals(subpaths.get(1), "a.c");
        assertEquals(subpaths.get(2), "a.d");
    }
}

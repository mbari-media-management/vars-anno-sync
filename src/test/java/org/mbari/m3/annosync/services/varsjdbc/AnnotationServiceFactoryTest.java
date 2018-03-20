package org.mbari.m3.annosync.services.varsjdbc;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mbari.m3.annosync.util.ConfigUtilities;

import java.io.File;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2018-03-20T10:04:00
 */
public class AnnotationServiceFactoryTest  {



    @Test
    public void testLoad() {
        final Subject<Object> rxSubject = PublishSubject.create().toSerialized();
        final File configFile = ConfigUtilities.toFile(getClass().getResource("/test.conf"));
        final Config config = ConfigFactory.parseFile(configFile).resolve();
        List<AnnotationServiceImpl> services = AnnotationServiceFactory.load(rxSubject, config);

        assertTrue("Whoops ... we expected 3 services", services.size() == 3);

    }
}

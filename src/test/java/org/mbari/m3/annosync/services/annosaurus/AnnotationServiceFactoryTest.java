package org.mbari.m3.annosync.services.annosaurus;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mbari.m3.annosync.services.annosaurus.v1.AnnotationServiceFactory;
import org.mbari.m3.annosync.services.annosaurus.v1.AnnotationServiceImpl;
import org.mbari.m3.annosync.util.ConfigUtilities;

import java.io.File;
import java.util.List;

/**
 * @author Brian Schlining
 * @since 2018-03-20T10:25:00
 */
public class AnnotationServiceFactoryTest {

    @Test
    public void testLoad() {
        final Subject<Object> rxSubject = PublishSubject.create().toSerialized();
        final File configFile = ConfigUtilities.toFile(getClass().getResource("/test.conf"));
        final Config config = ConfigFactory.parseFile(configFile).resolve();

        List<AnnotationServiceImpl> services = AnnotationServiceFactory.load(rxSubject, config);
        assertTrue("Uh oh ... we expected 2 services", services.size() == 2);
    }
}

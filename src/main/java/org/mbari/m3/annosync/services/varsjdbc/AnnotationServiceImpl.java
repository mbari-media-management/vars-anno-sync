package org.mbari.m3.annosync.services.varsjdbc;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.mbari.m3.annosync.ConceptNameChangedMsg;
import org.mbari.m3.annosync.services.AnnotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2018-01-26T09:45:00
 */
public class AnnotationServiceImpl implements AnnotationService {

    private final String url;
    private final String username;
    private final String password;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AnnotationServiceImpl(Observable<Object> nameChangeBus,
            String url, String username, String password, String driver) {
        this.url = url;
        this.username = username;
        this.password = password;
        if (driver != null) {
            try {
                Class.forName(driver);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        nameChangeBus.ofType(ConceptNameChangedMsg.class)
                .observeOn(Schedulers.io())
                .subscribe(this::handleNameChange);
        log.debug("Targeting VARS database at " + url);
    }

    public AnnotationServiceImpl(Observable<Object> namechangeBus,
            String url, String username, String password) {
        this(namechangeBus, url, username, password, null);
    }

    public void handleNameChange(ConceptNameChangedMsg msg) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            final Statement statement = connection.createStatement();
            String oldNames = msg.getOldNames()
                    .stream()
                    .map(s -> "'" + s + "'")
                    .collect(Collectors.joining(", "));
            log.debug("Changing annotations from {} to '{}'", oldNames, msg.getNewName());
            String sql0 = "UPDATE Observation SET ConceptName = '" +
                    msg.getNewName() + "' WHERE ConceptName IN (" +
                    oldNames + ")";
            statement.executeUpdate(sql0);
            String sql1 = "UPDATE Association SET ToConcept = '" +
                    msg.getNewName() + "' WHERE ToConcept IN (" +
                    oldNames + ")";
            statement.executeUpdate(sql1);
            statement.close();
            connection.close();
        }
        catch (Exception e) {
            log.error("An error occurred while updating " + url, e);
        }
    }
}

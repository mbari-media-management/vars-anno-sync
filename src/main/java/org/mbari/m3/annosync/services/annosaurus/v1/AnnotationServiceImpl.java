package org.mbari.m3.annosync.services.annosaurus.v1;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.mbari.m3.annosync.ConceptNameChangedMsg;
import org.mbari.m3.annosync.services.AnnotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

/**
 * @author Brian Schlining
 * @since 2018-01-26T11:14:00
 */
public class AnnotationServiceImpl implements AnnotationService {
    private final String targetUrl;
    private final String clientSecret;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public AnnotationServiceImpl(Observable<Object> nameChangeBus,
            String targetUrl, String clientSecret) {
        this.targetUrl = targetUrl;
        this.clientSecret = clientSecret;
        nameChangeBus.ofType(ConceptNameChangedMsg.class)
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleNameChange);
        log.debug("Targeting Annosaurus at " + targetUrl);
    }

    public void handleNameChange(ConceptNameChangedMsg msg) {
        final Client client = ClientBuilder.newClient();
        // -- Get Authorization
        String authResponse = client.target(targetUrl)
                .path("auth")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "APIKEY " + clientSecret)
                .post(Entity.entity("", MediaType.TEXT_PLAIN_TYPE))
                .readEntity(String.class);

        final Authorization authorization =
                gson.fromJson(authResponse, Authorization.class);

        for (String oldName : msg.getOldNames()) {
            renameObservations(client, oldName, msg.getNewName(), authorization);
            renameAssociations(client, oldName, msg.getNewName(), authorization);
        }

        client.close();

    }

    private void renameObservations(Client client, String oldName, String newName, Authorization authorization) {
        Form form = new Form()
                .param("old", oldName)
                .param("new", newName);

        String response = client.target(targetUrl)
                .path("observations")
                .path("concept")
                .path("rename")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", authorization.getTokenType() +
                        " " + authorization.getAccessToken())
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE))
                .readEntity(String.class);

        final RenameResponse renameResponse = gson.fromJson(response, RenameResponse.class);

        log.debug("Renamed {} observation(s) from {} to {} at {}",
                renameResponse.getNumberUpdated(),
                renameResponse.getOldConcept(),
                renameResponse.getNewConcept(),
                targetUrl);
    }

    private void renameAssociations(Client client, String oldName, String newName, Authorization authorization) {
        Form form = new Form()
                .param("old", oldName)
                .param("new", newName);

        String response = client.target(targetUrl)
                .path("associations")
                .path("toconcept")
                .path("rename")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", authorization.getTokenType() +
                        " " + authorization.getAccessToken())
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE))
                .readEntity(String.class);

        final RenameResponse renameResponse = gson.fromJson(response, RenameResponse.class);

        log.debug("Renamed {} association(s) from {} to {} at {}",
                renameResponse.getNumberUpdated(),
                renameResponse.getOldConcept(),
                renameResponse.getNewConcept(),
                targetUrl);
    }
}

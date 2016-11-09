package com.akamai.purge.api;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

public class PurgeRequest {

    private PurgeOptions options;

    public PurgeRequest(PurgeOptions options) {
        this.options = options;
    }

    /**
     * This method does the basic work of the client -- invoke the web service 
     * and return the result.
     * 
     * @param user The EdgeControl user-name
     * @param password The EdgeControl password
     * @param urls The list of URLs (or ARLs or CPCodes) to be purged.
     * @return The result of the purge call.
     * @throws PurgeException 
     */
    public PurgeResult send(String user, String password, Collection<String> urls) throws PurgeException {

        JsonObject payload = createPurgePayload(urls);

        Response response = null;
        try {
            Client client = ClientBuilder.newClient();
            client.register(HttpAuthenticationFeature.basic(user, password));
            WebTarget target = client.target("https://api.ccu.akamai.com");
            WebTarget resource = target.path("/ccu/v2/queues/default");
            response = resource.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(payload.toString(), MediaType.APPLICATION_JSON_TYPE));

            if (response.getStatus() != 201) {
                throw new PurgeException(response.readEntity(String.class));
            }

            JsonObject object = JsonObject.readFrom(response.readEntity(String.class));
            return new PurgeResult(object.get("estimatedSeconds").asInt(), object.get("progressUri").asString(),
                    object.get("purgeId").asString(), object.get("httpStatus").asInt(), object.get("detail").asString(),
                    object.get("pingAfterSeconds").asInt());

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private JsonObject createPurgePayload(Collection<String> urls) {
        JsonArray array = new JsonArray();
        for (String url : urls) {
            array.add(url);
        }

        JsonObject object = new JsonObject();
        if (PurgeOptions.PurgeType.CPCODE.equals(options.getType())) {
            object.add("type", "cpcode");
        }
        object.add("objects", array);
        return object;
    }
}

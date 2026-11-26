package io.github.tomasevicjovan.qacommercelab.api.clients;

import io.github.tomasevicjovan.qacommercelab.api.model.LabConfig;

import java.util.Map;

import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;

/** Global lab switches. They change the shop for everyone, so only @serial scenarios use them. */
public final class LabClient extends BaseClient {

    public LabClient(ApiSession session) {
        super(session);
    }

    public void configure(LabConfig config) {
        sendIgnoringBody(POST, "/lab/config", request().body(config), 200);
    }

    public void restoreDefaults() {
        sendIgnoringBody(POST, "/lab/defaults", request().body(Map.of()), 200);
    }

    public void status() {
        sendIgnoringBody(GET, "/lab/status", request(), 200);
    }
}

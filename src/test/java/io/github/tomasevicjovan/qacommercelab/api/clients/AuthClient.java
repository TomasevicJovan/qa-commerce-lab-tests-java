package io.github.tomasevicjovan.qacommercelab.api.clients;

import io.github.tomasevicjovan.qacommercelab.api.model.Credentials;
import io.github.tomasevicjovan.qacommercelab.api.model.Session;
import io.github.tomasevicjovan.qacommercelab.api.model.SignInRequest;

import static io.restassured.http.Method.POST;

/** POST /api/auth/register and /api/auth/login. Both sign the scenario's API session in. */
public final class AuthClient extends BaseClient {

    public AuthClient(ApiSession session) {
        super(session);
    }

    public Session register(Credentials credentials) {
        var result = send(POST, "/auth/register", request().body(credentials), Session.class, 201, 200);
        session().use(result);
        return result;
    }

    public Session signIn(String email, String password) {
        var result = send(POST, "/auth/login", request().body(new SignInRequest(email, password)), Session.class, 200);
        session().use(result);
        return result;
    }
}

package io.github.tomasevicjovan.qacommercelab.api.clients;

import io.github.tomasevicjovan.qacommercelab.api.model.Session;

/**
 * The signed-in customer of one scenario, shared by all API clients of that scenario.
 * PicoContainer creates one per scenario, so parallel scenarios never share a token.
 */
public final class ApiSession {

    private Session session;

    public void use(Session session) {
        this.session = session;
    }

    public Session current() {
        if (session == null) {
            throw new IllegalStateException("No customer is signed in through the API in this scenario.");
        }
        return session;
    }

    String tokenOrNull() {
        return session == null ? null : session.token();
    }
}

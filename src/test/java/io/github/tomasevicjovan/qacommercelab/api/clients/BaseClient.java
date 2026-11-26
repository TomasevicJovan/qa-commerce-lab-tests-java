package io.github.tomasevicjovan.qacommercelab.api.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tomasevicjovan.qacommercelab.support.Settings;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Shared plumbing of the API clients: base URL, JSON, the scenario's bearer token, and one rule for
 * responses. A client states which statuses it expects; anything else fails with the method, path,
 * status and body, so a broken precondition is not reported as a failure of the behaviour under test.
 */
abstract class BaseClient {

    private static final RestAssuredConfig CONFIG = RestAssured.config().objectMapperConfig(
            ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory((type, charset) ->
                    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));

    private final ApiSession session;

    BaseClient(ApiSession session) {
        this.session = session;
    }

    protected RequestSpecification request() {
        var spec = new RequestSpecBuilder()
                .setBaseUri(Settings.current().baseUrl())
                .setBasePath("/api")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(CONFIG)
                .build();
        var token = session.tokenOrNull();
        return token == null ? RestAssured.given(spec) : RestAssured.given(spec).auth().oauth2(token);
    }

    protected <T> T send(Method method, String path, RequestSpecification request, Class<T> type, int... expected) {
        return send(method, path, request, response -> response.as(type), expected);
    }

    protected <T> T send(Method method, String path, RequestSpecification request, TypeRef<T> type, int... expected) {
        return send(method, path, request, response -> response.as(type), expected);
    }

    protected void sendIgnoringBody(Method method, String path, RequestSpecification request, int... expected) {
        send(method, path, request, response -> null, expected);
    }

    protected ApiSession session() {
        return session;
    }

    private <T> T send(Method method, String path, RequestSpecification request, Function<Response, T> read, int... expected) {
        var response = request.request(method, path);
        if (Arrays.stream(expected).noneMatch(status -> status == response.statusCode())) {
            throw new IllegalStateException("%s /api%s returned %d, expected %s: %s".formatted(
                    method, path, response.statusCode(), Arrays.toString(expected), response.asString()));
        }
        return read.apply(response);
    }
}

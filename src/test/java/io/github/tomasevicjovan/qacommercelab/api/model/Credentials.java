package io.github.tomasevicjovan.qacommercelab.api.model;

/** A customer's account details, as sent to register. */
public record Credentials(String name, String email, String password) {
}

package io.github.tomasevicjovan.qacommercelab.api.model;

/** Response of register and sign-in: the bearer token and who it belongs to. */
public record Session(String token, User user, long expiresAt) {
}

package org.chefcrew.user.dto.request;

public record LoginRequest(
    String email,
    String password
) {}

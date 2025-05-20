package org.chefcrew.user.dto;

public record SignUpRequest(
    String name,
    String email,
    String password
) {}

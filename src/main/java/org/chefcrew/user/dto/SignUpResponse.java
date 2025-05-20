package org.chefcrew.user.dto;

public record SignUpResponse(
    Long userId,
    String name,
    String email,
    String message
) {}

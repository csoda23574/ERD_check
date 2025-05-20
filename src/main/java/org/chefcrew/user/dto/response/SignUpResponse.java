package org.chefcrew.user.dto.response;

public record SignUpResponse(
    Long userId,
    String name,
    String email,
    String message
) {}

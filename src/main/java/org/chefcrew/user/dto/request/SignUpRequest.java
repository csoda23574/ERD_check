package org.chefcrew.user.dto.request;

public record SignUpRequest(
        String email,
        String password,
        String userName
) {
}

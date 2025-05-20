package org.chefcrew.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoAuthRequest {
    private String code;
    private String redirectUri;
}

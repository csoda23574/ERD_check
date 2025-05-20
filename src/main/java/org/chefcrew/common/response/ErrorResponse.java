package org.chefcrew.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private int status;

    @JsonProperty("timestamp")
    private long timestamp;

    public ErrorResponse(int status, String message) {
        this.message = message;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
}

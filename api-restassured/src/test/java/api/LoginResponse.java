package api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
record LoginResponse(Authentication authentication) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Authentication(String token, int bid, String umail) {
    }
}

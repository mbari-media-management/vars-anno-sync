package org.mbari.m3.annosync.services.annosaurus.v1;

/**
 * @author Brian Schlining
 * @since 2018-01-26T11:16:00
 */
public class Authorization {
    private final String tokenType;
    private final String accessToken;

    public Authorization(String tokenType, String accessToken) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }
}

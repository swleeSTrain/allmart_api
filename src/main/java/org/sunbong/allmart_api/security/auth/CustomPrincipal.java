package org.sunbong.allmart_api.security.auth;

import java.security.Principal;

public class CustomPrincipal implements Principal {

    private final String email;

    public CustomPrincipal(final String email) {
        this.email = email;
    }

    @Override
    public String getName() {
        return email;
    }
}
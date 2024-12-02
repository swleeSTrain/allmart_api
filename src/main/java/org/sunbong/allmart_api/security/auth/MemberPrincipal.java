package org.sunbong.allmart_api.security.auth;

import java.security.Principal;

public class MemberPrincipal implements Principal {

    private final String email;

    public MemberPrincipal(final String email) {
        this.email = email;
    }

    @Override
    public String getName() {
        return email;
    }
}
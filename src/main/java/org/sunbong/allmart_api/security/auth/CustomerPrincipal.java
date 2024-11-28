package org.sunbong.allmart_api.security.auth;

import java.security.Principal;

//public class CustomuserPrincipal implements Principal {
//
//    private final String email;
//
//    public CustomuserPrincipal(final String email) {
//        this.email = email;
//    }
//
//    @Override
//    public String getName() {
//        return email;
//    }
//}

public class CustomerPrincipal implements Principal {
    private String email;
    private String role;
    private String phone;
    private String loginType;

    public CustomerPrincipal(String email, String role, String phone, String loginType) {
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.loginType = loginType;
    }

    @Override
    public String getName() {
        return "";
    }

    // Getters and Setters
}

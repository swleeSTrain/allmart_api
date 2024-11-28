package org.sunbong.allmart_api.customer.exception;

public enum CustomerExceptions {

    BAD_AUTH(400,"ID/PW incorrect"),
    TOKEN_NOT_ENOUGH(401,"More Tokens required"),
    ACCESSTOKEN_TOO_SHORT(401,"Access Token too short"),
    REQUIRE_SIGN_IN(401,"Require sign in"),
    INVALID_TOKEN(401, "check TokenType ");

    private CustomerTaskException exception;

    CustomerExceptions(int statue, String msg) {
        exception = new CustomerTaskException(statue, msg);
    }

    public CustomerTaskException get() {
        return exception;
    }

}
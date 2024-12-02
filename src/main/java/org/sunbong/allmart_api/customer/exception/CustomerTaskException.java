package org.sunbong.allmart_api.customer.exception;

import lombok.Data;

@Data
public class CustomerTaskException extends RuntimeException {

    private int status;
    private String msg;

    public CustomerTaskException(final int status, final String msg) {
        super(status+"_"+msg);
        this.status = status;
        this.msg = msg;
    }
}

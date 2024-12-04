package org.sunbong.allmart_api.customer.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerTaskException extends RuntimeException {

    private int status;
    private String msg;

    public CustomerTaskException(final int status, final String msg) {
        super(status+"_"+msg);
        this.status = status;
        this.msg = msg;
    }
}

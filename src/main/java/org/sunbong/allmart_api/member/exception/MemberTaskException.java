package org.sunbong.allmart_api.member.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemberTaskException extends RuntimeException {

    private int status;
    private String msg;

    public MemberTaskException(final int status, final String msg) {
        super(status+"_"+msg);
        this.status = status;
        this.msg = msg;
    }
}

package net.monkeystudio.chatrbtw.service.bean.sessiontoken;

/**
 * @author xiaxin
 */
public class TokenResult {
    public final static Integer TOKEN_INVALID = 0;
    public final static Integer TOKEN_VALID = 1;

    private Integer valid = TOKEN_INVALID;

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }
}

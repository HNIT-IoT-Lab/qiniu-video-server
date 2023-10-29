package cn.hnit.sdk.orm.mongodb.exception;

import java.io.Serializable;

/**
 * OrmException
 *
 * @author king
 * @since 2022-10-28 16:34
 **/
public class OrmException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = -5875371379845226068L;


    public OrmException() {
        super();
    }

    public OrmException(String msg) {
        super(msg);
    }

    public static OrmException pop(String msg) {
        return new OrmException(msg);
    }
}

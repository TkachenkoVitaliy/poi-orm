package com.poiorm.exception;

public class PoiOrmRestrictionException extends RuntimeException {
    public PoiOrmRestrictionException() {
    }

    public PoiOrmRestrictionException(String message) {
        super(message);
    }

    public PoiOrmRestrictionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoiOrmRestrictionException(Throwable cause) {
        super(cause);
    }

    public PoiOrmRestrictionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

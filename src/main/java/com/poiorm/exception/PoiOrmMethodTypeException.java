package com.poiorm.exception;

public class PoiOrmMethodTypeException extends RuntimeException {
    public PoiOrmMethodTypeException() {
    }

    public PoiOrmMethodTypeException(String message) {
        super(message);
    }

    public PoiOrmMethodTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoiOrmMethodTypeException(Throwable cause) {
        super(cause);
    }

    public PoiOrmMethodTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

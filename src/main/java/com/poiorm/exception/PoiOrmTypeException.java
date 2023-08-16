package com.poiorm.exception;

public class PoiOrmTypeException extends RuntimeException {
    public PoiOrmTypeException() {
    }

    public PoiOrmTypeException(String message) {
        super(message);
    }

    public PoiOrmTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoiOrmTypeException(Throwable cause) {
        super(cause);
    }

    public PoiOrmTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

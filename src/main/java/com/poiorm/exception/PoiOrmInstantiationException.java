package com.poiorm.exception;

public class PoiOrmInstantiationException extends RuntimeException {
    public PoiOrmInstantiationException() {
    }

    public PoiOrmInstantiationException(String message) {
        super(message);
    }

    public PoiOrmInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoiOrmInstantiationException(Throwable cause) {
        super(cause);
    }

    public PoiOrmInstantiationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

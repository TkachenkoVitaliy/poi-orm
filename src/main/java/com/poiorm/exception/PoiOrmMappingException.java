package com.poiorm.exception;

public class PoiOrmMappingException extends RuntimeException {
    public PoiOrmMappingException() {
    }

    public PoiOrmMappingException(String message) {
        super(message);
    }

    public PoiOrmMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoiOrmMappingException(Throwable cause) {
        super(cause);
    }

    public PoiOrmMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

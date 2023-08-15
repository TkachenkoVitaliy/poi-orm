package com.poiorm.util;

public enum ModifierType {
    PUBLIC(1),
    PRIVATE (2),
    PROTECTED(4),
    STATIC(8),
    FINAL(16),
    SYNCHRONIZED(32),
    VOLATILE(64),
    TRANSIENT(128),
    NATIVE(256),
    INTERFACE(512),
    ABSTRACT(1024);

    private final int value;
    ModifierType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

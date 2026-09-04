package com.example.dispatch.exception;

public final class DispatchExceptions {
    private DispatchExceptions() { }

    public static class DuplicateResourceException extends RuntimeException {
        public DuplicateResourceException(String message) { super(message); }
    }

    public static class InvalidDispatchStateException extends RuntimeException {
        public InvalidDispatchStateException(String message) { super(message); }
    }
}

package com.hostel.hostelfinder.exception;

public class AIServiceException extends RuntimeException {
    
    public AIServiceException(String message) {
        super(message);
    }

    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

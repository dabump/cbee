package com.github.dabump.cbee.circuitbreaker;

/**
 * @author Martin Coetzee (martin@martincoetzee.com)
 */
public class CircuitBreakerOpenedException extends RuntimeException {

    public CircuitBreakerOpenedException(String message) {
        super(message);
    }
}

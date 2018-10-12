package com.github.dabump.cbee.circuitbreaker;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Martin Coetzee (martin@martincoetzee.com)
 */
public class CircuitBreakerRegister {

    private static Map<String, CircuitBreakerImpl> breakers = new ConcurrentHashMap<>();

    public static CircuitBreakerImpl get(String scope, long invocationTimeout, long openStateTimeout, int failureThreshold) {
        CircuitBreakerImpl breaker = breakers.get(scope);
        if (breaker == null) {
            breaker = new CircuitBreakerImpl(scope, invocationTimeout, openStateTimeout, failureThreshold);
            breakers.put(scope, breaker);
        }
        return breaker;
    }

    public static CircuitBreakerImpl get(String scope) {
        CircuitBreakerImpl breaker = breakers.get(scope);
        return breaker;
    }

    public static Collection<CircuitBreakerImpl> getMetrics() {
        return breakers.values();
    }

    protected static Map<String, CircuitBreakerImpl> getBreakers() {
        return breakers;
    }

}

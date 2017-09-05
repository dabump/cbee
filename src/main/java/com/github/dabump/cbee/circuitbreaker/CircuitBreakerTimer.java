package com.github.dabump.cbee.circuitbreaker;

import javax.ejb.Schedule;
import javax.ejb.Singleton;

@Singleton
public class CircuitBreakerTimer {

    @Schedule(second="0", minute="*",hour="*", persistent=false)
    public void watchScopeState() {
        for (String scope : CircuitBreakerRegister.getBreakers().keySet()) {
            CircuitBreakerImpl circuitBreaker = CircuitBreakerRegister.getBreakers().get(scope);
            if (circuitBreaker.getState() == CircuitBreakerImpl.CircuitBreakerState.OPENED || circuitBreaker.getState() == CircuitBreakerImpl.CircuitBreakerState.HALFCLOSED) {
                circuitBreaker.isInvocationPermitted(); /** Force recheck of breaker **/
            }
        }
    }
}

package com.github.dabump.cbee.circuitbreaker;

import lombok.extern.java.Log;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.logging.Level;

/**
 * @author Martin Coetzee (martin@martincoetzee.com)
 */
@Log
@Interceptor
@CircuitBreaker
public class CircuitBreakerInterceptor implements Serializable {

    @AroundInvoke
    private Object intercept(InvocationContext ic) throws Exception {
        CircuitBreaker sb = ic.getMethod().getAnnotation(CircuitBreaker.class);
        if (sb != null) {
            CircuitBreakerImpl cb = CircuitBreakerRegister.get(sb.scope(), sb.invocationTimeoutinMillis(), sb.openStatementResetInMillies(), sb.failureThershold());

            log.log(Level.INFO, "Current circuit breaker state: " + cb.getState());

            if (cb.isInvocationPermitted()) {
                CircuitBreakerTransaction txn = cb.openTransaction();
                try {
                    Object returnObject = ic.proceed();
                    cb.closeTransaction(txn);
                    return returnObject;
                } catch (Exception e) {
                    boolean avoided = false;
                    for (Class<? extends Exception> avoidedException : sb.ignoredExceptions()) {
                        if (avoidedException.isInstance(e)) {
                            avoided = true;
                        }
                    }
                    if (!avoided) {
                        txn.setFailed(e);
                        cb.closeTransaction(txn);
                    }
                    throw e;
                }
            } else {
                throw new CircuitBreakerOpenedException("Circuit breaker opened for scope ["+sb.scope()+"]");
            }
        } else return ic.proceed();
    }

}

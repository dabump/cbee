package com.github.dabump.cbee.circuitbreaker;

import lombok.extern.java.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 * @author Martin Coetzee (martin@martincoetzee.com)
 */
@Log
public class CircuitBreakerImpl {

    private String scope;
    private int failedAttempts = 0;
    private CircuitState state = new ClosedState();
    private final int failureThreshold; //Number of attempts before opening the circuit
    private final Random random = new Random();
    private final Duration invocationTimeout; // Maximum time for transaction time to be marked as failed attempt
    private final Duration openStateTimeout; // Max time to wait before closing circuit

    private CircuitBreakerMetrics metrics = new CircuitBreakerMetrics();

    List<CircuitBreakerTransaction> transactions = Collections.synchronizedList(new ArrayList<CircuitBreakerTransaction>());

    CircuitBreakerImpl(String scope, long invocationTimeout, long openStateTimeout, int failureThreshold) {
        this.scope = scope;
        this.invocationTimeout = Duration.ofMillis(invocationTimeout);
        this.openStateTimeout = Duration.ofMillis(openStateTimeout);
        this.failureThreshold = failureThreshold;
    }

    private CircuitState changeState(CircuitState newState) {
        this.state = newState;
        failedAttempts = 0;
        return state;
    }

    public boolean isInvocationPermitted() {
        return state.isInvocationPermitted();
    }

    public CircuitBreakerState getState() {
        return state.getState();
    }

    public String getScope() {
        return scope;
    }

    public CircuitBreakerMetrics getMetrics() {
        return metrics;
    }

    public CircuitBreakerTransaction openTransaction() {
        if (metrics.getFirstTransactionTime() == null) metrics.setFirstTransactionTime(Instant.now());
        metrics.setLastTransactionTime(Instant.now());
        CircuitBreakerTransaction txn = new CircuitBreakerTransaction();
        transactions.add(txn);
        return txn;
    }

    public void closeTransaction(CircuitBreakerTransaction txn) {
        txn.closeTransaction();
        transactions.remove(txn);
        healthCheck(txn);
    }

    private void healthCheck(CircuitBreakerTransaction transaction) {
        if (transaction.failed()) {
            failedAttempts ++;
            if (transaction.getException() != null) { metrics.setLastExceptionMessage(transaction.getException().getMessage());}
            metrics.incrementUnsuccessfulTransaction();
        } else {
            Long ttl = transaction.getClosedTime().toEpochMilli() - transaction.getOpenedTime().toEpochMilli();
            /* Time based check */
            if (invocationTimeout.toMillis() <= ttl) {
                failedAttempts ++;
                metrics.incrementUnsuccessfulTransaction();
            } else {
                failedAttempts = 0;
                metrics.incrementSuccessfulTransaction();
            }
        }
        if (failedAttempts == failureThreshold) {
            changeState(new OpenedState());
            metrics.incrementOpenedStateHistory();
        }
    }

    /** ------ Inner classes ------- **/
    private final class ClosedState implements CircuitState {
        @Override
        public boolean isInvocationPermitted() {
            return true;
        }
        public CircuitBreakerState getState() { return CircuitBreakerState.CLOSED; }
    }

    private final class HalfClosedState implements CircuitState {
        private Double chance = 0.5; // 50% of the time
        @Override
        public boolean isInvocationPermitted() {
            if (random.nextDouble() >= chance) {
                changeState(new ClosedState());
                return true;
            } return false;
        }

        public CircuitBreakerState getState() { return CircuitBreakerState.HALFCLOSED; }
    }

    private final class OpenedState implements CircuitState {
        Instant permittedTime = Instant.now().plus(openStateTimeout);
        @Override
        public boolean isInvocationPermitted() {
            if (!calculateInvocationPermitted()) {
                return changeState(new HalfClosedState()).isInvocationPermitted();
            } return false;
        }
        private boolean calculateInvocationPermitted() {
            log.log(Level.INFO, "Circuit breaker, time before resetting: " + (permittedTime.toEpochMilli() - Instant.now().toEpochMilli()));
            return (Instant.now().isAfter(permittedTime)) ? false : true;
        }
        public CircuitBreakerState getState() { return CircuitBreakerState.OPENED; }
    }

    private interface CircuitState {
        boolean isInvocationPermitted();
        CircuitBreakerState getState();
    }

    public enum CircuitBreakerState {
        OPENED, CLOSED, HALFCLOSED;
    }
}

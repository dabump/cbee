package com.github.dabump.cbee.circuitbreaker;

import lombok.Data;

import java.time.Instant;

@Data
public class CircuitBreakerMetrics {

    public long openedStateHistory;
    public String lastExceptionMessage;
    public long successfulTransactions;
    public long unsuccessfulTransactions;
    public Instant firstTransactionTime;
    public Instant lastTransactionTime;

    public void incrementSuccessfulTransaction() {this.successfulTransactions++;}
    public void incrementUnsuccessfulTransaction() {this.unsuccessfulTransactions++;}
    public void incrementOpenedStateHistory() {this.openedStateHistory++;}

}

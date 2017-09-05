package com.github.dabump.cbee.circuitbreaker;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Martin Coetzee (martin@martincoetzee.com)
 */
public class CircuitBreakerTransaction implements Serializable {

    private Instant openTime;
    private Instant closedTime;
    private Boolean fail = false;
    private Exception exception;

    public CircuitBreakerTransaction() {
        openTime = Instant.now();
    }

    public Instant getOpenedTime() { return openTime; }
    public Instant getClosedTime() { return closedTime; }
    public void setFailed(Exception e) {this.fail = true; this.exception=e;}
    public void setFailed() {this.fail = true;}
    public Exception getException() {return exception;}
    public Boolean failed() {return fail;}

    public void closeTransaction() { closedTime = Instant.now(); }

}

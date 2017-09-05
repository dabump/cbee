package com.github.dabump.cbee.service;

import com.github.dabump.cbee.circuitbreaker.CircuitBreakerImpl;
import com.github.dabump.cbee.circuitbreaker.CircuitBreakerRegister;
import lombok.extern.java.Log;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collection;

/**
 * @author Martin Coetzee (martin@martincoetzee.com)
 */
@Path("/circuitbreaker")
@Produces({"application/json"})
@Consumes({"application/json"})
@Log
public class CircuitBreakerAPI {

    @GET
    @Path("/")
    public Collection<CircuitBreakerImpl> getReport() {
        return CircuitBreakerRegister.getMetrics();
    }
}

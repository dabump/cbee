package com.github.dabump.cbee.circuitbreaker;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

/**
 * @author Martin Coetzee (martin@martincoetzee.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@InterceptorBinding
@Documented
public @interface CircuitBreaker {
    @Nonbinding long invocationTimeoutinMillis() default 2000;  // Default of 2 seconds for transaction time before reporting error
    @Nonbinding long openStatementResetInMillies() default 60000; // Default of one minute timeout if circuit open
    @Nonbinding int failureThershold() default 3; // Default of 3 attempts before circuit opened
    @Nonbinding String scope() default "default_scope";
    @Nonbinding Class<? extends Exception>[] ignoredExceptions() default {};
}

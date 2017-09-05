# cbee
Circuit Breaker pattern implementation for Java EE

## cbee technologies
* Circuit Breaker Pattern - ([Martin Fowler](https://martinfowler.com/bliki/CircuitBreaker.html))
* Lombok Data and Builder pattern on POJO - ([Lombok](https://projectlombok.org))
* Java CDI (Depedancy Injection & interception)

# configuration
Add the below to you maven pom.xml

        <!-- cbee -->
        <dependency>
            <groupId>com.github.dabump</groupId>
            <artifactId>cbee</artifactId>
            <version>1.0.1</version>
        </dependency>

Add the following to your beans.xml

    <interceptors>
        <class>com.github.dabump.cbee.circuitbreaker.CircuitBreakerInterceptor</class>
    </interceptors>

# usage
Around any method that you want to circuit break, add the @CircuitBreaker annotation

Simple Example with default values:
    
    @CircuitBreaker
    public void executeSomeLogic() {
        ...
    }

Advanced Example (Used if upstream system throws Exceptions you do not want to react to and open the circuit):
    
    @CircuitBreaker(scope="authentication", ignoredExceptions={MyExceptionNotToCircuitBreak.class, AnotherClass.class})
    public SecurityCredentials authenticateUser(String username, String password) {
        ...
    }
    
Custom values example

    @CircuitBreaker(invocationTimeoutinMillis = 3000, failureThershold = 5, openStatementResetInMillies = 120000)
    public boolean doLogic() {
        ...
    }

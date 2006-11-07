package org.objenesis.tck.candidates;

public class ConstructorThrowingException {

    public ConstructorThrowingException() {
        throw new IllegalArgumentException("Constructor throwing an exception");
    }
    
}

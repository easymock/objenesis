package org.objenesis.tck.candidates;

public class ConstructorWithMandatoryArguments {

    public ConstructorWithMandatoryArguments(String something) {
        if (something == null) {
            throw new IllegalArgumentException("Need arguments");
        }
    }
}

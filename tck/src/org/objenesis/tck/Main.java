package org.objenesis.tck;

import org.objenesis.NewInstanceInstantiator;
import org.objenesis.AccessibleInstantiator;
import org.objenesis.ConstructorInstantiator;
import org.objenesis.tck.candidates.*;

/**
 * Command line launcher for Technology Compatability Kit (TCK).
 * 
 * @author Joe Walnes
 * @see TCK
 */
public class Main {
    public static void main(String[] args) {
        TCK tck = new TCK();

        tck.registerInstantiator(new NewInstanceInstantiator(), "NewInstance");
        tck.registerInstantiator(new ConstructorInstantiator(), "Constructor");
        tck.registerInstantiator(new AccessibleInstantiator(), "Accessible");

        tck.registerCandiate(NoConstructor.class, "No constructor");
        tck.registerCandiate(DefaultPublicConstructor.class, "Default public constructor");
        tck.registerCandiate(DefaultProtectedConstructor.class, "Default protected constructor");
        tck.registerCandiate(DefaultPackageConstructor.class, "Default package constructor");
        tck.registerCandiate(DefaultPrivateConstructor.class, "Default private constructor");

        tck.runTests(new TextReporter(System.out, System.err));
    }
}

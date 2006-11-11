package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableWithAncestorThrowingException extends ConstructorThrowingException implements Serializable {

    public SerializableWithAncestorThrowingException() {

    }
}

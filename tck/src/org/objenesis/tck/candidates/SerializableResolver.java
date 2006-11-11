package org.objenesis.tck.candidates;

import java.io.Serializable;

public class SerializableResolver implements Serializable {
	protected Object readResolve() {
		return new SerializableReplacer();
	}
}

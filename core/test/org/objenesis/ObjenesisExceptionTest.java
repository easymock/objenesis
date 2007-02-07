package org.objenesis;

import junit.framework.TestCase;

public class ObjenesisExceptionTest extends TestCase {

	public final void testObjenesisException() {
		new ObjenesisException();		
	}

	public final void testObjenesisExceptionString() {
		Exception e = new ObjenesisException("test");
		assertEquals("test", e.getMessage());
	}

	public final void testObjenesisExceptionThrowable() {
		Exception cause = new RuntimeException("test");
		Exception e = new ObjenesisException(cause);
		assertSame(cause, e.getCause());
		assertEquals(cause.toString(), e.getMessage());
		
		// Check null case
		e = new ObjenesisException((Throwable) null);
		assertNull(e.getCause());
		assertEquals(null, e.getMessage());
	}

	public final void testObjenesisExceptionStringThrowable() {
		Exception cause = new RuntimeException("test");
		Exception e = new ObjenesisException("msg", cause);
		assertSame(cause, e.getCause());
		assertEquals("msg", e.getMessage());
		
		// Check null case
		e = new ObjenesisException("test", null);
		assertNull(e.getCause());
		assertEquals("test", e.getMessage());		
	}

}

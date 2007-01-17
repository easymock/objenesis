package org.objenesis;

import junit.framework.TestCase;

public class ObjenesisTest extends TestCase {

	public final void testObjenesis() {
		Objenesis o = new ObjenesisStd();
		assertEquals("org.objenesis.ObjenesisStd using org.objenesis.StdInstantiatorStrategy with caching", o.toString());
	}

	public final void testObjenesisBoolean() {
		Objenesis o = new ObjenesisStd(false);
		assertEquals("org.objenesis.ObjenesisStd using org.objenesis.StdInstantiatorStrategy without caching", o.toString());
	}

	public final void testObjenesisInstantiatorStrategy() {
		Objenesis o = new ObjenesisStd(false);
		assertEquals("org.objenesis.ObjenesisStd using org.objenesis.StdInstantiatorStrategy without caching", o.toString());
	}

	public final void testObjenesisInstantiatorStrategyBoolean() {
		Objenesis o = new ObjenesisStd(new MyStrategy(), false);
		assertEquals("org.objenesis.ObjenesisStd using org.objenesis.MyStrategy without caching", o.toString());
	}

	public final void testNewInstance() {
		Objenesis o = new ObjenesisStd();
		assertEquals(getClass(), o.newInstance(getClass()).getClass());
	}

	public final void testNewInstantiatorOf() {
		Objenesis o = new ObjenesisStd();
		ObjectInstantiator i1 = o.newInstantiatorOf(getClass());
		// Test instance creation
		assertEquals(getClass(), i1.newInstance().getClass());
		
		// Test caching
		ObjectInstantiator i2 = o.newInstantiatorOf(getClass());
		assertSame(i1, i2);
	}
	
	public final void testToString() {
		Objenesis o = new ObjenesisStd() {};
		assertEquals("org.objenesis.ObjenesisTest$1 using org.objenesis.StdInstantiatorStrategy with caching", o.toString());
	}
}

class MyStrategy implements InstantiatorStrategy {
	public ObjectInstantiator newInstantiatorOf(Class type) {
		return null;
	}		
}

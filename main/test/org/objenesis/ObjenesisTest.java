package org.objenesis;

import junit.framework.TestCase;

public class ObjenesisTest extends TestCase {

	public final void testObjenesis() {
		Objenesis o = new Objenesis();
		assertEquals("org.objenesis.Objenesis using org.objenesis.AutomaticInstantiatorStrategy with caching", o.toString());
	}

	public final void testObjenesisBoolean() {
		Objenesis o = new Objenesis(false);
		assertEquals("org.objenesis.Objenesis using org.objenesis.AutomaticInstantiatorStrategy without caching", o.toString());
	}

	public final void testObjenesisInstantiatorStrategy() {
		Objenesis o = new Objenesis(false);
		assertEquals("org.objenesis.Objenesis using org.objenesis.AutomaticInstantiatorStrategy without caching", o.toString());
	}

	public final void testObjenesisInstantiatorStrategyBoolean() {
		Objenesis o = new Objenesis(new MyStrategy(), false);
		assertEquals("org.objenesis.Objenesis using org.objenesis.MyStrategy without caching", o.toString());
	}

	public final void testNewInstance() {
		Objenesis o = new Objenesis();
		assertEquals(getClass(), o.newInstance(getClass()).getClass());
	}

	public final void testNewInstantiatorOf() {
		Objenesis o = new Objenesis();
		ObjectInstantiator i1 = o.newInstantiatorOf(getClass());
		// Test instance creation
		assertEquals(getClass(), i1.newInstance().getClass());
		
		// Test caching
		ObjectInstantiator i2 = o.newInstantiatorOf(getClass());
		assertSame(i1, i2);
	}
	
	public final void testToString() {
		Objenesis o = new Objenesis() {};
		assertEquals("org.objenesis.ObjenesisTest$1 using org.objenesis.AutomaticInstantiatorStrategy with caching", o.toString());
	}
}

class MyStrategy implements InstantiatorStrategy {
	public ObjectInstantiator newInstantiatorOf(Class type) {
		return null;
	}		
}

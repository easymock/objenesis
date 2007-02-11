package org.objenesis;

import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * Objenesis implementation using the {@link StdInstantiatorStrategy}.
 * 
 * @author Henri Tremblay 
 */
public class ObjenesisStd extends ObjenesisBase {

   /**
    * Default constructor using the {@link StdInstantiatorStrategy}
    */
   public ObjenesisStd() {
      super(new StdInstantiatorStrategy());
   }

   /**
    * Instance using the {@link StdInstantiatorStrategy} with or without caching
    * {@link ObjectInstantiator}s
    * 
    * @param useCache If {@link ObjectInstantiator}s should be cached
    */
   public ObjenesisStd(boolean useCache) {
      super(new StdInstantiatorStrategy(), useCache);
   }
}

import org.junit.jupiter.api.Test;
import org.mymodule.App;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppTest {

   App app = new App();

   @Test
   void testObject() {
      assertNotNull(app.newObject());
   }

   @Test
   void testSerializable() {
      assertNotNull(app.newSerializable());
   }
}

import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Test
    public void createAndRetrieveUser() {
    // Create a new user and save it
    new User("erik@gmail.com", "secret", "Erik").save();
    
    // Retrieve the user with e-mail address bob@gmail.com
    User erik = User.find("byEmail", "erik@gmail.com").first();
    
    // Test 
    assertNotNull(erik);
    assertEquals("Erik", erik.fullname);
}

}

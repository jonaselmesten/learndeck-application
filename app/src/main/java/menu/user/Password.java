package menu.user;

import java.security.SecureRandom;
import java.util.Random;


/**<h1>Password</h1>
 * Used for creating random passwords.
 * @author Jonas Elmesten
 */
public class Password {

    private Password() {}

    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvxyz" +
            "01234578";
    private static final String SPECIAL =  "_!?/";
    private static final Random RANDOM = new SecureRandom();

    /**
     * Generate a random password with 5 characters being A-Za-z0-9 and 2 being special characters like ?_!/.
     * @return Password
     */
    public static String generatePassword() {

        StringBuilder password = new StringBuilder();

        for(int i = 0; i < 5; i++)
            password.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));

        for(int i = 0; i < 2; i++)
            password.insert(RANDOM.nextInt(password.length()), SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        return password.toString();
    }
}

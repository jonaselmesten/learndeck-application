package menu.user;

import java.util.regex.Pattern;

/**<h1>User</h1>
 * Immutable class to be used when you need the user information when jumping between different windows.
 * Also has regex for ssn, name and e-mail when creating users.
 * @author Jonas Elmesten
 */
public class User {

    public static final Pattern VALID_NAME_REGEX = Pattern.compile("^[a-zA-Z ]*$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_SSN_REGEX = Pattern.compile("^\\d{4}\\d{2}\\d{2}$", Pattern.CASE_INSENSITIVE);

    private final UserType USER_TYPE;
    private final int USER_ID;

    public User(int userId, UserType userType) {
        this.USER_ID = userId;
        this.USER_TYPE = userType;
    }

    public UserType getUserType() {
        return USER_TYPE;
    }

    public int getUserId() {
        return this.USER_ID;
    }
}

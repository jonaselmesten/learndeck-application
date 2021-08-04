package menu.user;

/**
 * Class that's used when logging in. This class will provide the path to the FXML-file depending of what kind of user logged in.
 * @author Jonas Elmesten
 */
public enum UserType {

    STUDENT("/fxml/studentMainWindow.fxml"), TEACHER("/fxml/teacherMainWindow.fxml"), NO_LOG_IN("");

    private final String FXML;

    UserType(String fxml) {
        this.FXML = fxml;
    }

    public static UserType stringToUserType(String string)
    {
        switch(string) {
            case "student":
                return UserType.STUDENT;
            case "teacher":
                return UserType.TEACHER;
            default:
                return UserType.NO_LOG_IN;
        }
    }

    public String getFXMLpath() {
        return FXML;
    }
};

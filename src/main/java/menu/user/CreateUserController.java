package menu.user;

import database.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import menu.ChangeableWindow;
import menu.WindowUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

public class CreateUserController implements ChangeableWindow {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField eMail;
    @FXML private TextField birthDate;

    private final Logger logger = LogManager.getLogger(CreateUserController.class);

    public void createUserButtonPushed() {

        if(checkInputFields()) {

            String userName = null;
            String password = Password.generatePassword();

            try {

                userName = DatabaseUtil.getValidUserName(createUserName(firstName.getText(), lastName.getText()));
                DatabaseUtil.addStudentUser(firstName.getText(), lastName.getText(), userName, password, birthDate.getText(), eMail.getText());

            }catch(SQLException e) {

                //If the same e-mail already exists
                if(e.getErrorCode() == 1062) {

                    WindowUtil.createPopUpWarning(getWindow(), "A user with the same e-mail already exists");
                    eMail.clear();
                    e.printStackTrace();
                    return;
                }

                //If the connection to the database failed
                WindowUtil.createPopUpWarning(getWindow(), "Could not connect to database");
                logger.debug("SQLException occurred while trying to add student/get valid user name.", e);
                e.printStackTrace();
                return;
            }

            WindowUtil.changeWindow(getClass().getResource("/fxml/logInWindow.fxml"), getWindow());
            WindowUtil.createUserNamePasswordWindow(userName, password);
        }
    }

    private static String createUserName(String firstName, String lastName) {

        StringBuilder stringBuilder = new StringBuilder();

        //Handle exceptionally short names.
        if(firstName.length() < 3) {

            stringBuilder.append(firstName.substring(0, firstName.length()));

        }else if(lastName.length() < 3) {

            stringBuilder.append(lastName.substring(0, lastName.length()));

        }else {
            stringBuilder.append(firstName.substring(0,3)).append(lastName.substring(0,3));
        }

        return stringBuilder.toString();
    }

    private boolean checkInputFields() {

        Matcher matcherFirstName = User.VALID_NAME_REGEX.matcher(firstName.getText());
        Matcher matcherLastName = User.VALID_NAME_REGEX.matcher(lastName.getText());
        Matcher matcherMail = User.VALID_EMAIL_ADDRESS_REGEX.matcher(eMail.getText());
        Matcher matcherSSN = User.VALID_SSN_REGEX.matcher(birthDate.getText());

        boolean incorrectFormat = false;
        String message = "Invalid ";

        if(firstName.getText().trim().isEmpty() || lastName.getText().trim().isEmpty() || eMail.getText().trim().isEmpty() || birthDate.getText().trim().isEmpty()) {

            WindowUtil.createPopUpWarning(getWindow(), "You can't have empty fields");
            return false;
        }

        //Gather all the incorrect fields into one string to show the user.
        if(!matcherFirstName.find()) {

            firstName.clear();
            message = message.concat(" first name -");
            incorrectFormat = true;
        }

        if(!matcherLastName.find()) {

            lastName.clear();
            message = message.concat(" last name -");
            incorrectFormat = true;
        }

        if(!matcherMail.find()) {

            eMail.clear();
            message = message.concat(" e-mail -");
            incorrectFormat = true;
        }

        if(!matcherSSN.find()) {

            birthDate.clear();
            message = message.concat(" birth date -");
            incorrectFormat = true;
        }

        if(incorrectFormat)
            WindowUtil.createPopUpWarning(getWindow(), message.substring(0, message.length() - 2));

        return !incorrectFormat;
    }

    public void goBackButtonPushed() {
        WindowUtil.changeWindow(getClass().getResource("/fxml/logInWindow.fxml"), getWindow());
    }

    @Override
    public Window getWindow() {
        return firstName.getScene().getWindow();
    }
}

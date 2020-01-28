package menu.user;

import database.DatabaseUtil;
import deck.DeckUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import menu.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.ServerUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class LogInController implements ChangeableWindow, UserController {

    @FXML private PasswordField password;
    @FXML private TextField userName;

    private final Logger logger = LogManager.getLogger(LogInController.class);

    public void logInButtonPushed() {

        try {

            if(userName.getText().trim().length() > 0 && password.getText().trim().length() > 0) {

                Optional<User> user = DatabaseUtil.tryUserLogIn(userName.getText(), password.getText());

                if(user.isPresent())
                    changeWindow(user.get());

                else{
                    WindowUtil.createPopUpWarning(getWindow(),"Failed to log in - Try again");
                    password.clear();
                }
            }else{
                WindowUtil.createPopUpWarning(getWindow(), "Enter user name and password");
            }

        }catch(SQLException e) {

            logger.debug("SQLException occurred while user " + userName.getText() + "tried to log in.", e);
            WindowUtil.createPopUpWarning(getWindow(), "Could not connect to database");
            e.printStackTrace();
        }
    }

    public void createUserButtonPushed() {
        WindowUtil.changeWindow(getClass().getResource("/fxml/createUserWindow.fxml"), getWindow());
    }

    private void changeWindow(User loggedInUser) {

        try {
            DeckUtil.createDefaultDirectory();
            WindowUtil.changeWindowWithUser(getClass().getResource(loggedInUser.getUserType().getFXMLpath()), getWindow(), loggedInUser);

        }catch(IOException e) {

            WindowUtil.createPopUpWarning(getWindow(), "Could not create a default directory.");
            e.printStackTrace();
        }
    }

    @Override
    public Window getWindow() {
        return password.getScene().getWindow();
    }

    @Override
    public void initData(Object... obj) {
    }
}

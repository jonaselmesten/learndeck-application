package menu;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        ControlStage controlStage = WindowUtil.addWindowOnTop(getClass().getResource("/fxml/logInWindow.fxml"));
        Stage mainWindow = controlStage.getStage();
        mainWindow.getIcons().add(new Image("pictures/logo.png"));
        mainWindow.setTitle("LearnDeck");
        mainWindow.setResizable(false);
        mainWindow.show();
    }

    public static void main(String[] args) throws IOException, SQLException {

        DBFiles.setDBconfig(new URL("file:\\C:\\Users\\Anders\\IdeaProjects\\learndeck\\src\\main\\resources\\conifg\\db.properties"));

        database.ConnectionPool.initialize(DBFiles.getDBconfig());
        database.ConnectionPool.getDataSource().getConnection();

        Application.launch(args);
    }
}

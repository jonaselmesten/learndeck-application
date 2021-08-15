package menu;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        ControlStage controlStage = WindowUtil.addWindowOnTop(getClass().getResource("/fxml/logInWindow.fxml"));
        Stage mainWindow = controlStage.getStage();
        mainWindow.getIcons().add(new Image("img/logo.png"));
        mainWindow.setTitle("LearnDeck");
        mainWindow.setResizable(false);
        mainWindow.show();
    }

    public static void main(String[] args) {

        ConnectionPool.initialize();
        Application.launch(args);
    }
}

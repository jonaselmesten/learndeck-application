package menu;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import menu.user.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**<h1>Window utilities</h1>
 * Class with static utility methods to do window-related actions with ease.
 * @author Jonas Elmesten
 */
public class WindowUtil {

    /**
     * Change scene on the main window.
     * @param resource URL to the new scene you want.
     * @param window Main window.
     */
    public static void changeWindow(URL resource, Window window) {

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Scene newScene = null;

        try {
            newScene = new Scene(fxmlLoader.load());
            Stage mainWindow = (Stage) window;
            mainWindow.setScene(newScene);
            mainWindow.setOnCloseRequest(event -> mainWindowClosing());
            mainWindow.show();

        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Change scene and a the same time dependency inject the logged in user.
     * Used when you need to do actions where the user-information is needed.
     * @param resource URL to the new scene you want.
     * @param window Main window.
     * @param user Logged in user.
     */
    public static void changeWindowWithUser(URL resource, Window window, User user) {

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Scene newScene = null;

        try {
            newScene = new Scene(fxmlLoader.load());

            Stage mainWindow = (Stage) window;
            mainWindow.setScene(newScene);

            mainWindow.show();

            UserController controller = fxmlLoader.getController();
            controller.initData(user);

        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Used when you don't want to change the main window scene, but instead add another frame.
     * @param resource URL to the new scene you want in a new window.
     * @return
     */
    public static ControlStage addWindowOnTop(URL resource) {

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        UserController controller = null;
        Scene newScene = null;
        Stage stage = null;

        try {
            newScene = new Scene(fxmlLoader.load());
            controller = fxmlLoader.getController();

            stage = new Stage();
            stage.getIcons().add(new Image("img/logo.png"));
            stage.setScene(newScene);

        }catch(IOException e) {
            e.printStackTrace();
        }

        return new ControlStage(controller, stage);
    }


    /**
     * User when you want to show a popup-message over the current window.
     * @param window The window where you want to show the popup.
     * @param message Message to display.
     */
    public static void createPopUpWarning(Window window, String message) {

        Stage stage = (Stage) window;
        Tooltip tooltip = new Tooltip();
        tooltip.setText(message);
        tooltip.setMinSize(200,50);
        tooltip.setAutoHide(true);
        tooltip.setAutoFix(true);

        tooltip.setAnchorX(window.getX() + window.getWidth()/2 - 100);
        tooltip.setAnchorY(window.getY() + 210);

        tooltip.show(stage);
    }


    //TO DO: Send e-mail to user.
    /**
     * User when a new user is created, and you want to display their log in information.
     * @param userName User name to be displayed.
     * @param password Password to be displayed.
     */
    public static void createUserNamePasswordWindow(String userName, String password) {

        VBox vBox = new VBox(15);
        HBox firstRow = new HBox(0);
        HBox secondRow = new HBox(0);
        vBox.setStyle("-fx-background-color: white;");

        Text text1 = new Text("User name: ");
        Text text2 = new Text("Password: ");
        text1.setFont(Font.font("System", FontWeight.BOLD, 13));
        text2.setFont(Font.font("System", FontWeight.BOLD, 13));

        firstRow.getChildren().addAll(text1, new Text(userName));
        firstRow.setAlignment(Pos.CENTER);
        secondRow.getChildren().addAll(text2, new Text(password));
        secondRow.setAlignment(Pos.CENTER);

        vBox.getChildren().addAll(firstRow, secondRow);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefSize(300, 130);
        vBox.getChildren().add(new Text("Write this information down!"));
        //vBox.getChildren().add(new Text("An e-mail was sent with this information"));

        Stage stage = new Stage();
        stage.getIcons().add(new Image("img/logo.png"));
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        stage.show();
    }

    /**
     * Open a file-chooser window over the current window.
     * @param window Window to open it over.
     * @return Optional of Image, if you close the window the optional will be empty.
     */
    public static Optional<Image> openPictureFileChooser(Window window) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pictures", "*.jpg", "*.png"));

        File pictureFile = fileChooser.showOpenDialog(window);

        if(pictureFile == null)
            return Optional.empty();

        Optional<Image> image = fileToImage(pictureFile, window);

        return image;
    }

    private static Optional<Image> fileToImage(File file, Window window) {

        Image image = null;

        if(file != null) {

            image = new Image(file.toURI().toString());

            if(file.length() / 1024 > 1000 || image.getHeight() > 600 || image.getWidth() > 800) {

                WindowUtil.createPopUpWarning(window, "Pictures must be within 800x600 and 1MB");
                return Optional.empty();
            }
        }

        assert image != null;

        return Optional.of(image);
    }

    private static void mainWindowClosing() {
    }
}

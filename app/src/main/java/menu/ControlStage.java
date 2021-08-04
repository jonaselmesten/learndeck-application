package menu;

import javafx.stage.Stage;


public class ControlStage {

    private final UserController controller;
    private final Stage stage;

    public ControlStage(UserController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }

    public UserController getController() {
        return controller;
    }

    public Stage getStage() {
        return stage;
    }
}

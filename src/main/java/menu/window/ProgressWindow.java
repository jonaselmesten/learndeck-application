package menu.window;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import menu.UserController;

public class ProgressWindow implements UserController{

    @FXML private ProgressBar progressBar;

    @Override
    public void initData(Object... obj) {

    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}

package menu;

import javafx.stage.Window;

/**
 * Controllers for windows with methods that use the class "WindowUtil"
 * should implement this class to easily get the window instance of the current controller.
 */
public interface ChangeableWindow {

    /**
     * Simply choose one of the "@FXML" annotated JavaFX objects and
     * do as the following example:
     * <p>return javafxObj.getScene().getWindow();</p>
     * @return Window instance of the current window.
     */
    Window getWindow();
}

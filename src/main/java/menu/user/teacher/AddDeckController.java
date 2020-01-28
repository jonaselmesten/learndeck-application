package menu.user.teacher;

import database.DatabaseUtil;
import deck.Deck;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import menu.ChangeableWindow;
import menu.UserController;
import menu.WindowUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Map;

public class AddDeckController implements UserController, ChangeableWindow {

    @FXML private TextField courseName;

    private final Logger logger = LogManager.getLogger(AddDeckController.class);

    private TeacherMainController controller;
    private Map<String, Deck> courseMap;
    private int teacherId;

    @Override
    public void initData(Object... obj) {

        controller = (TeacherMainController) obj[0];
        teacherId = (int) obj[1];
        courseMap = (Map<String, Deck>) obj[2];
    }

    public void addCourseButtonPushed() {

        if(courseMap.size() >= 50) {
            WindowUtil.createPopUpWarning(getWindow(), "You can't have more than 50 courses");
            return;
        }

        if(courseName.getText().trim().length() == 0) {

            courseName.clear();
            WindowUtil.createPopUpWarning(getWindow(),"You need to enter a name");

        }else {

            String name = courseName.getText().trim().toLowerCase();
            name = name.substring(0,1).toUpperCase() + name.substring(1);

            try {
                int courseId = DatabaseUtil.addNewCourse(name, teacherId);
                controller.addRowOnGrid(name,0, 0, courseId);

            }catch(SQLException e) {

                if(e.getErrorCode() == 1062)
                    WindowUtil.createPopUpWarning(getWindow(),"A course with the same name already exists");

                WindowUtil.createPopUpWarning(getWindow(),"Could not connect to database");
                logger.debug("SQLException occurred while trying to add course with name:" + name + " to database.", e);
                e.printStackTrace();
            }
            courseName.clear();
        }
    }

    @Override
    public Window getWindow() {
        return courseName.getScene().getWindow();
    }
}

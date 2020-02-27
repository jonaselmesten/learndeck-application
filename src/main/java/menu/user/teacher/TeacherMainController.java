package menu.user.teacher;

import database.DatabaseUtil;
import database.QueryResult;
import deck.Deck;
import deck.DeckUtil;
import deck.IOStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import menu.ChangeableWindow;
import menu.ControlStage;
import menu.UserController;
import menu.WindowUtil;
import menu.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.ServerConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class TeacherMainController implements UserController, ChangeableWindow {

    @FXML private GridPane courseGrid;

    private final Logger logger = LogManager.getLogger(TeacherMainController.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Map<String, Deck> courseMap = new TreeMap();

    private short courseGridPosition = 1;
    private boolean updatingGrid = false;

    private User user;

    @Override
    public void initData(Object ...obj) {

        this.user = (User) obj[0];
        getWindow().setOnCloseRequest(event -> windowClosing());
        updateGridPane();
    }

    //TO DO: See over the update/deletion-flow.
    private void windowClosing() {

        logger.debug("Closing TeacherMainController-window.");

        try {
            DeckUtil.createDefaultDirectory();
            DeckUtil.saveCoursesToPC(courseMap.values());
            removeAndUploadCourses();

        }catch(IOException e) {

            WindowUtil.createPopUpWarning(getWindow(), "File error - Contact admin");
            logger.error("IOException occurred while trying to create default directory and save courses to it.", e);
        }
    }

    public void logOutButtonPushed() {

        windowClosing();
        WindowUtil.changeWindow(getClass().getResource("/fxml/logInWindow.fxml"), getWindow());
    }

    public void addDeckButtonPushed() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/addDeckWindow.fxml"));
        Scene addDeck = new Scene(fxmlLoader.load());

        AddDeckController controller = fxmlLoader.getController();
        controller.initData(this, user.getUserId(), courseMap);

        //Create popup window
        Stage popUp = new Stage();
        popUp.getIcons().add(new Image("pictures/logo.png"));
        popUp.initModality(Modality.APPLICATION_MODAL);
        popUp.resizableProperty().set(Boolean.FALSE);
        popUp.setScene(addDeck);
        popUp.show();
    }

    private void addEditCardsButtonPushed(MenuButton menuButton, Text cardCount) {

        Deck deck = getDeckFromGrid(menuButton);

        ControlStage ct = WindowUtil.addWindowOnTop(getClass().getResource("/fxml/editCardsWindow.fxml")); //FUNK? FINARE
        Stage stage = ct.getStage();
        ct.getController().initData(deck);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        //Set the updated value after the window has been closed.
        cardCount.setText(String.valueOf(deck.getDeckSize()));
    }

    private void addRemoveStudentButtonPushed(MenuButton menuButton, Text studentCount) {

        ObservableList<Node> children = courseGrid.getChildren();
        int column = GridPane.getColumnIndex(menuButton) - 3;
        int row = GridPane.getRowIndex(menuButton);
        Deck deck = null;

        for(Node node:children) {

            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {

                deck = courseMap.get(((Text) node).getText());
                break;
            }
        }

        ControlStage ct = WindowUtil.addWindowOnTop(getClass().getResource("/fxml/addStudentWindow.fxml"));
        Stage stage = ct.getStage();
        ct.getController().initData(deck, studentCount);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    private void removeDeckButtonPushed(MenuButton menuButton) {

        Deck deck = getDeckFromGrid(menuButton);

        try {
            DatabaseUtil.removeCourse(deck.getCourseId());
            //ServerConnection.removeCourseFile(deck);
            DeckUtil.removeCourseFile(deck);

            courseMap.remove(deck.getCourseName());
            removeCourseFromGrid(menuButton);

        }catch(SQLException e) {
            logger.debug("SQLException occurred while trying to remove course:" + deck.getCourseName() + " Id:" + deck.getCourseId() + " from database.", e);
            e.printStackTrace();
        }catch(IOException e) {
            logger.debug("IOException occurred while trying to remove course:" + deck.getCourseName() + " Id:" + deck.getCourseId() + " from PC.", e);
            e.printStackTrace();
        }
    }

    private void statsButtonPushed(MenuButton menuButton) {

        Deck deck = getDeckFromGrid(menuButton);

        ControlStage ct = WindowUtil.addWindowOnTop(getClass().getResource("/fxml/statWindow.fxml"));
        Stage stage = ct.getStage();
        ct.getController().initData(deck, getStudentCountFromGrid(menuButton));

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void downloadCoursesFromServer(List<QueryResult.TeacherCourse> list) throws InterruptedException {

        //Download courses from server if the PC doesn't have them.
        for(QueryResult.TeacherCourse course : list) {

            if(!DeckUtil.courseFileExists(course.getCourseName())) {
                //ServerConnection.downloadDeck(course.getCourseName());
            }
        }

        //ServerConnection.waitForAllDownloads();
    }

    private List<Integer> getCourseInfo(List<QueryResult.TeacherCourse> list) throws IOException, InterruptedException, ExecutionException {

        List<Callable<Integer>> taskList = new ArrayList<>();

        //Gather all the information needed for the GUI/the course-grid.
        for(QueryResult.TeacherCourse courseInfo : list) {

            String courseName = courseInfo.getCourseName();
            int courseId = courseInfo.getCourseId();

            courseMap.put(courseName, DeckUtil.fileToCourse(courseName));

            taskList.add(() -> DatabaseUtil.getCardAmount(courseId));

            taskList.add(() -> DatabaseUtil.getStudentAmount(courseId));
        }

        List<Integer> resultList = new ArrayList<>();

        //Execute all the tasks and att them to the result-list.
        for(Future<Integer> res : executor.invokeAll(taskList))
            resultList.add(res.get());

        return resultList;
    }

    private void updateGridPane() {

        try {
            updatingGrid = true;

            List<QueryResult.TeacherCourse> list = DatabaseUtil.getTeacherCourses(user.getUserId());
            downloadCoursesFromServer(list);
            ListIterator<Integer> iterator = getCourseInfo(list).listIterator();

            //Add everything to the GUI/course-grid for the user to see.
            for(Deck deck : courseMap.values())
                addRowOnGrid(deck.getCourseName(), iterator.next(), iterator.next(), deck.getCourseId());

            updatingGrid = false;

        }catch(SQLException e) {

            WindowUtil.createPopUpWarning(getWindow(), "Something went wrong while trying to update the GUI.");
            logger.debug("SQLException occurred while trying to get card/student-amount from database. UserId:" + user.getUserId(), e);
            e.printStackTrace();

        }catch(IOException e) {

            WindowUtil.createPopUpWarning(getWindow(), "Something went wrong while trying to update the GUI.");
            logger.debug("IOException occurred while trying to read course-files on the PC. UserId:" + user.getUserId(), e);
            e.printStackTrace();

        }catch(InterruptedException | ExecutionException e) {

            logger.debug("InterruptedException/ExecutionException occurred while updating gridPane.", e);
            e.printStackTrace();
        }
    }

    void addRowOnGrid(String courseName, int cardCount, int studentCount, int courseId) {

        //Set all the button-options that each row/course will have.
        MenuButton menuButton = new MenuButton("Options");
        MenuItem addRemoveStudentOption = new MenuItem("Add/Remove student");
        MenuItem editCardsOption = new MenuItem("Add/Edit cards");
        MenuItem statsOption = new MenuItem("Stats");
        MenuItem removeDeckOption = new MenuItem("Remove deck");
        menuButton.setStyle("-fx-background-color: white; -fx-border-style: solid");
        menuButton.getItems().addAll(addRemoveStudentOption,editCardsOption,statsOption,removeDeckOption);

        Text cardCountText = new Text(String.valueOf(cardCount));
        Text studentCountText = new Text(String.valueOf(studentCount));

        if(!updatingGrid) {

            Deck deck = new Deck(courseName, courseId);
            deck.setIoStatus(IOStatus.NEW);
            courseMap.put(courseName, deck);
        }

        //Set methods for all the buttons.
        editCardsOption.setOnAction(event -> addEditCardsButtonPushed(menuButton, cardCountText));
        statsOption.setOnAction(event -> statsButtonPushed(menuButton));
        removeDeckOption.setOnAction(event -> removeDeckButtonPushed(menuButton));
        addRemoveStudentOption.setOnAction(event -> addRemoveStudentButtonPushed(menuButton, studentCountText));

        //Add everything to the GUI/course-grid.
        courseGrid.add(new Text(courseName),0,courseGridPosition);
        courseGrid.add(cardCountText,1,courseGridPosition);
        courseGrid.add(studentCountText,2,courseGridPosition);
        courseGrid.add(menuButton,3,courseGridPosition);
        courseGridPosition++;
    }

    //TO DO: Make batch removal from the database(?)
    //TO DO: Don't open a server connection if course never have been uploaded.
    //To DO: Handle exceptions better.
    private void removeAndUploadCourses() {

        try{
            ServerConnection serverConnection = new ServerConnection(user);

            //Remove all empty courses from Directory, Database & Server.
            for(Deck deck : courseMap.values()) {

                IOStatus status = deck.getIoStatus();

                if(deck.getDeckSize() == 0) {

                    courseMap.remove(deck.getCourseName());

                    DatabaseUtil.removeCourse(deck.getCourseId());

                    //Deck is updated to 0 cards and is not a newly created deck - has to be removed from PC & Server.
                    if(status.equals(IOStatus.CHANGED)) {

                        DeckUtil.removeCourseFile(deck);
                        serverConnection.addCourseToBeRemoved(deck.getCourseName());
                    }
                }
                //Upload if it has 1+ card and is new/changed.
                if(status.equals(IOStatus.NEW) || status.equals(IOStatus.CHANGED))
                    serverConnection.addCourseToUpload(deck);
            }

            serverConnection.uploadCourses();
            serverConnection.removeFiles();

        }catch(SQLException e) {

            WindowUtil.createPopUpWarning(getWindow(), "Database error - You will be able to remove empty courses next log in.");
            logger.debug("SQLException occurred while trying to remove a course from database.", e);
            e.printStackTrace();

        }catch(IOException e) {

            WindowUtil.createPopUpWarning(getWindow(), "Could not remove file from PC/server.");
            logger.debug("IOException occurred while trying to remove a course from PC/server.", e);
            e.printStackTrace();
        }
    }

    private void removeCourseFromGrid(Node buttonPushed) {

        int row = GridPane.getRowIndex(buttonPushed);
        courseGrid.getChildren().removeIf(e -> GridPane.getRowIndex(e) == row);
    }

    private int getStudentCountFromGrid(Node gridNode) {

        ObservableList<Node> children = courseGrid.getChildren();
        int column = GridPane.getColumnIndex(gridNode) - 1;
        int row = GridPane.getRowIndex(gridNode);
        Text stuCount = null;

        for(Node node:children) {

            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {

                stuCount = (Text) node;
                break;
            }
        }
        assert stuCount != null;
        return Integer.parseInt(stuCount.getText());
    }

    private Deck getDeckFromGrid(Node gridNode) {

        ObservableList<Node> children = courseGrid.getChildren();
        int column = GridPane.getColumnIndex(gridNode) - 3;
        int row = GridPane.getRowIndex(gridNode);
        Deck deck = null;

        for(Node node : children) {

            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {

                deck = courseMap.get(((Text) node).getText());
                break;
            }
        }
        return deck;
    }

    @Override
    public Window getWindow() {
        return courseGrid.getScene().getWindow();
    }
}

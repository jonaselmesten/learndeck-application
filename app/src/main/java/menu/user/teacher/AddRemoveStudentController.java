package menu.user.teacher;

import database.DatabaseUtil;
import database.QueryResult;
import deck.Deck;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import menu.UserController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class AddRemoveStudentController implements UserController {

    @FXML private Text studentCount;
    @FXML private VBox mainVbox;
    @FXML private VBox resultBox;
    @FXML private Text courseName;

    private final Text noStudentsText = new Text("No students currently enrolled");
    private final Text noSearchResultText = new Text("No students found");
    private final HBox searchHbox = new HBox();
    private final TextField searchTextField = new TextField();

    private final Logger logger = LogManager.getLogger(AddRemoveStudentController.class);
    private final AtomicLong lastInputTime = new AtomicLong();
    private final Map<Integer, SearchResult> currentStudentsBox = new LinkedHashMap<>();
    private final ExecutorService searchFieldThread = Executors.newSingleThreadExecutor();

    private boolean searchBoxIsVisible = false;
    private boolean isWaiting = false;

    private enum ScreenMode {NORMAL_WINDOW, SEARCH_WINDOW;}
    private ScreenMode mode = ScreenMode.NORMAL_WINDOW;

    //From previous window
    private Text studentCountText;
    private Deck deck;

    @Override
    public void initData(Object ...obj) {

        deck = (Deck) obj[0];
        studentCountText = (Text) obj[1];

        setGUI();
        initCourseStudentInfo();
    }

    private void setGUI() {

        courseName.setText(deck.getCourseName());
        searchHbox.setAlignment(Pos.CENTER);
        searchHbox.setPrefHeight(30);
        searchHbox.setSpacing(15);
        searchTextField.setPromptText("Search for student");
        searchTextField.setMaxWidth(250);
        searchTextField.setOnKeyPressed(event -> searchTextFieldInput());
        searchHbox.getChildren().addAll(searchTextField);
    }

    private void initCourseStudentInfo() {

        try {
            List<QueryResult.StudentInfo> list = DatabaseUtil.getCourseStudents(deck.getCourseId());
            int counter = 0;

            //Add the decks' current students.
            for(QueryResult.StudentInfo student : list) {

                counter++;
                SearchResult searchResult = new SearchResult(student.getStudentId(), student.getFirstName(), student.getLastName(), String.valueOf(student.getBirthDate()));
                searchResult.addToCurrentStudents(searchResult);
                addStudentToScreen(searchResult, true);
            }

            if(currentStudentsBox.isEmpty())
                resultBox.getChildren().add(noStudentsText);

            studentCount.setText(String.valueOf(counter));

        }catch(SQLException e) {
            logger.debug("SQLException occurred while trying to get the courses' student info for deck:" + deck.getCourseName() + " Id:" + deck.getCourseId(), e);
            e.printStackTrace();
        }
    }

    //Nested class that is used when searching for students.
    //The class is represented graphically buy JavaFx buttons.
    private final class SearchResult {

        private final HBox searchResultBox = new HBox();
        private final Button addButton = new Button("Add");
        private final Button removeButton = new Button("Remove");
        private final int STUDENT_ID;

        SearchResult(int studentId, String firstName, String lastName, String birthDate) {

            //Settings for everything GUI-related----------------------
            this.STUDENT_ID = studentId;
            addButton.setStyle("-fx-background-color: white; -fx-border-style: solid");
            removeButton.setStyle("-fx-background-color: white; -fx-border-style: solid");
            searchResultBox.setAlignment(Pos.CENTER);
            searchResultBox.setPrefSize(600,25);
            searchResultBox.setSpacing(15);
            Separator separator = new Separator();
            separator.minWidth(150);
            separator.setOrientation(Orientation.HORIZONTAL);
            Text firstNameText = new Text(firstName);
            Text lastNameText = new Text(lastName);
            Text birthDateText = new Text(birthDate);
            removeButton.setPrefSize(75, 25);
            addButton.setPrefSize(75, 25);
            HBox hBox = new HBox();
            hBox.setMinWidth(200);
            hBox.setSpacing(15);
            hBox.setAlignment(Pos.BASELINE_LEFT);
            hBox.getChildren().addAll(lastNameText, firstNameText, birthDateText);
            //GUI settings end------------------------------------------

            //Add student to screen and add card reviews to the database.
            addButton.setOnAction(event -> {

                try {
                    DatabaseUtil.addStudentToCourse(studentId, deck.getCourseId());

                    int currentStuCount = Integer.parseInt(studentCount.getText());
                    studentCount.setText(String.valueOf(++currentStuCount));
                    studentCountText.setText(studentCount.getText());


                    addToCurrentStudents(this);
                    studentHasCourse(true);

                }catch(SQLException e) {
                    logger.debug("SQLException occurred while trying to add student with Id:" + studentId + " to course with name:" + deck.getCourseName() + " Id:" + deck.getCourseId(), e);
                    e.printStackTrace();
                }
            });

            //Remove student from screen and the card reviews from the database.
            removeButton.setOnAction(event -> {

                try{

                    switch(mode) {

                        case NORMAL_WINDOW:

                            removeFromCurrentStudents(studentId);
                            resultBox.getChildren().remove(searchResultBox);
                            DatabaseUtil.removeStudentFromCourse(studentId, deck.getCourseId());
                            break;

                        case SEARCH_WINDOW:

                            removeFromCurrentStudents(studentId);
                            studentHasCourse(false);
                            DatabaseUtil.removeStudentFromCourse(studentId, deck.getCourseId());
                            break;
                    }

                    //Update the student counter text.
                    int currentStuCount = Integer.parseInt(studentCount.getText());

                    if(currentStuCount == 1 && mode.equals(ScreenMode.NORMAL_WINDOW))
                        resultBox.getChildren().add(noStudentsText);

                    studentCount.setText(String.valueOf(--currentStuCount));
                    studentCountText.setText(studentCount.getText());

                }catch(SQLException e) {
                    logger.debug("SQLException occurred while trying to remove student with Id:" + studentId + " from course with name:" + deck.getCourseName() + " Id:" + courseName, e);
                    e.printStackTrace();
                }
            });

            searchResultBox.getChildren().addAll(hBox, separator, addButton, removeButton);
        }

        private void addToCurrentStudents(SearchResult searchResult) {
            currentStudentsBox.put(searchResult.getStudentId(), searchResult);
        }

        private void removeFromCurrentStudents(int studentId) {
            currentStudentsBox.remove(studentId);
        }

        private HBox getNode() {
            return searchResultBox;
        }

        private void studentHasCourse(boolean hasCourse) {

            if(hasCourse) {

                addButton.setDisable(true);
                removeButton.setDisable(false);

            }else{

                addButton.setDisable(false);
                removeButton.setDisable(true);
            }
        }

        private Integer getStudentId() {
            return STUDENT_ID;
        }
    }

    //Called each time input is entered in search field
    private void searchTextFieldInput() {

        long delayMili = 500;

        if(isWaiting) {

            lastInputTime.set(System.currentTimeMillis() + delayMili);

        }else {

            lastInputTime.set(System.currentTimeMillis() + delayMili);
            isWaiting = true;

            searchFieldThread.submit(searchExecute);
        }
    }

    public void searchStudentButtonPushed() {

        //Closing search field
        if(searchBoxIsVisible) {
            mode = ScreenMode.NORMAL_WINDOW;

            mainVbox.getChildren().remove(2);
            resultBox.getChildren().clear();
            searchTextField.clear();

            searchBoxIsVisible = false;

            //Show current students on screen
            for(SearchResult searchResult : currentStudentsBox.values()) {
                resultBox.getChildren().add(searchResult.getNode());
            }

            if(currentStudentsBox.isEmpty())
                resultBox.getChildren().add(noStudentsText);

        //Opening search field
        }else{

            mode = ScreenMode.SEARCH_WINDOW;
            searchBoxIsVisible = true;
            mainVbox.getChildren().add(2,searchHbox);
            resultBox.getChildren().clear();

            if(currentStudentsBox.isEmpty())
                searchFieldThread.submit(searchExecute);
        }
    }

    //Wait until the last time input was entered + delay - then execute the search, otherwise postpones the search.
    private final Runnable searchExecute = () -> {

        while(lastInputTime.get() > System.currentTimeMillis());

        try {
            List<QueryResult.StudentInfo> list = DatabaseUtil.searchStudent(searchTextField.getText());

            //Update GUI with the search result.
            Platform.runLater(() -> {

                resultBox.getChildren().clear();

                int counter = 0;

                for(QueryResult.StudentInfo student : list) {

                    counter++;
                    SearchResult searchResult = new SearchResult(student.getStudentId(), student.getFirstName(), student.getLastName(), String.valueOf(student.getBirthDate()));

                    if(currentStudentsBox.containsKey(searchResult.getStudentId()))
                        addStudentToScreen(searchResult, true);
                    else
                        addStudentToScreen(searchResult, false);
                }

                if(counter == 0)
                    resultBox.getChildren().add(noSearchResultText);

            });

        }catch(SQLException e) {
            logger.debug("SQLException occurred while trying to search after student with search word:" + searchTextField.getText() + " from course with name:" + deck.getCourseName() + " Id:" + courseName, e);
            e.printStackTrace();
        }

        isWaiting = false;
    };

    private void addStudentToScreen(SearchResult searchResult, boolean hasCourse) {

        searchResult.studentHasCourse(hasCourse);
        resultBox.getChildren().add(searchResult.getNode());
    }
}

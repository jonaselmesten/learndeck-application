package menu.user.student;

import database.DatabaseUtil;
import database.QueryResult;
import deck.Deck;
import deck.DeckUtil;
import deck.card.Card;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class StudentMainController implements ChangeableWindow, UserController {

    @FXML private GridPane courseGrid;

    private final Logger logger = LogManager.getLogger(StudentMainController.class);
    private final Map<String, Deck> courseMap = new TreeMap();
    private short courseGridPosition = 1;

    private User user;

    @Override
    public void initData(Object... obj) {

        this.user = (User) obj[0];
        updateGridPane();
        loadReviews();
        getWindow().setOnCloseRequest(event -> onWindowClose());
    }

    private void loadReviews() {

        //Always try to get reviews from the PC first.
        List<LocalDate> storedReviews = getStoredReviews();

        if(storedReviews.isEmpty()) {

            //No reviews stored on PC, so we need to get them from the database.
            for(Deck deck : courseMap.values()) {

                try {
                    List<QueryResult.CardReview> list = DatabaseUtil.getReviews(user.getUserId(), deck.getCourseId());

                    System.out.println(list.size());
                    System.out.println(deck.getDeckSize());

                    deck.sortCardsAfterId();
                    ListIterator<Card> iterator = deck.getCardIterator();

                    if(list.size() != deck.getDeckSize())
                        throw new IndexOutOfBoundsException();

                    //Goes through all the cards and sets the review dates.
                    for(QueryResult.CardReview review : list) {

                        Card card = iterator.next();
                        card.setNextReview(review.getNextReview());

                        if(!card.isFirstReview())
                            card.setReviewStats(review.getReviewStats());
                    }

                    deck.sortCardsAfterReviewDate();

                }catch(SQLException e) {

                    logger.debug("SQLException while trying to get review data from deck:" + deck.getCourseName() + " Id:" + deck.getCourseId(), e);
                    e.printStackTrace();
                }
            }
        }
    }

    private List<LocalDate> getStoredReviews() {
        return Collections.emptyList(); //This will be added in another update of the program.
    }

    public void logOutButtonPushed() {
        WindowUtil.changeWindow(getClass().getResource("/fxml/logInWindow.fxml"), getWindow());
    }

    private void onWindowClose() {}

    //Adds everything GUI-related to the gridPane.
    private void addRowOnGrid(String courseName, int cardAmount, int dueCardAmount, int newCardAmount) {

        //All the JavaFX-nodes for each row.
        Button studyButton = new Button("Study");
        studyButton.setStyle("-fx-background-color: white; -fx-border-style: solid");
        Text dueCards = new Text(String.valueOf(dueCardAmount));
        Text newCards = new Text(String.valueOf(newCardAmount));
        Text course = new Text(courseName);

        if(dueCards.getText().equals("0"))
            studyButton.setDisable(true);

        studyButton.setOnAction(event -> studyCards(dueCards, newCards, studyButton));

        courseGrid.add(course,0,courseGridPosition);
        courseGrid.add(new Text(String.valueOf(cardAmount)),1,courseGridPosition);
        courseGrid.add(dueCards,2,courseGridPosition);
        courseGrid.add(newCards,3,courseGridPosition);
        courseGrid.add(studyButton,4,courseGridPosition);
        courseGridPosition++;
    }

    //Goes to the study screen of a course.
    private void studyCards(Text dueCardAmount, Text newCardAmount, Button studyButton) {

        ControlStage ct = WindowUtil.addWindowOnTop(getClass().getResource("/fxml/studyCardsWindow.fxml"));
        Stage stage = ct.getStage();
        Deck deck = getDeckFromGrid(studyButton);

        assert deck != null;
        ct.getController().initData(deck, dueCardAmount, newCardAmount, studyButton, user);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    //Gets a certain deck by going through all the rows int the grid pane.
    private Deck getDeckFromGrid(Node gridNode) {

        ObservableList<Node> children = courseGrid.getChildren();
        int column = GridPane.getColumnIndex(gridNode) - 4;
        int row = GridPane.getRowIndex(gridNode);
        Deck deck = null;

        for(Node node:children) {

            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {

                deck = courseMap.get(((Text) node).getText());
                break;
            }
        }
        return deck;
    }

    private void updateGridPane() {

        try {
            List<QueryResult.StudentCourseInfo> list = DatabaseUtil.getStudentCourseInfo(user.getUserId());

            for(QueryResult.StudentCourseInfo info : list) {

                String courseName = info.getCourseName();
                int cardAmount = info.getTotalCards();
                int dueCardAmount = info.getDueCards();
                int newCardAmount = info.getNewCards();

                addRowOnGrid(courseName, cardAmount, dueCardAmount, newCardAmount);
                courseMap.put(courseName, DeckUtil.fileToCourse(courseName));
            }

        }catch(SQLException | IOException e) {
            logger.debug("SQLException while trying to get student course data in updateGridPane-method:", e);
            e.printStackTrace();
        }
    }

    @Override
    public Window getWindow() {
        return courseGrid.getScene().getWindow();
    }
}

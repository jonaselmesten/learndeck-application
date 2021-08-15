package menu.user.student;

import deck.Deck;
import deck.card.Card;
import deck.card.CardButtons;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import menu.ChangeableWindow;
import menu.UserController;
import menu.user.User;

import java.time.LocalDate;
import java.util.*;

public class StudyCardsController implements UserController, ChangeableWindow {

    @FXML private Button hardButton;
    @FXML private Button mediumButton;
    @FXML private Button easyButton;
    @FXML private Button veryEasyButton;
    @FXML private Button searchButton;
    @FXML private VBox vBox;
    @FXML private Text cardsToStudyCount;

    private Text dueCountMain;
    private Text newCountMain;
    private Button studyButton;

    private enum ButtonMode {ENABLE, DISABLE;};
    private boolean answerIsShowing = false;
    private boolean hasCardsLeft = true;

    private Deck deck;
    private Card currentCard;
    private Queue<Card> cardQueue;

    private User user;
    private DatabaseUtil.ReviewUpdateBatch updateBatch;

    @Override
    public void initData(Object... obj) {

        getWindow().setOnCloseRequest(event -> onWindowClose());

        deck = (Deck) obj[0];
        dueCountMain = (Text) obj[1];
        newCountMain = (Text) obj[2];
        studyButton = (Button) obj[3];
        user = (User) obj[4];

        updateBatch = new DatabaseUtil.ReviewUpdateBatch(user.getUserId(), deck.getCourseId());
        cardsToStudyCount.setText(dueCountMain.getText());

        deck.sortCardsAfterReviewDate();
        cardQueue = new LinkedList<>();
        cardQueue.addAll(deck.getImmutableList());

        goToNextCard();
    }

    private void onWindowClose() {
        new Thread(updateBatch).start();
    }

    private void goToNextCard() {

        vBox.getChildren().clear();
        setButtons(ButtonMode.DISABLE);
        answerIsShowing = false;

        //Continue to show new cards and remove the ones that has already been reviewed from the queue.
        while(true) {

            currentCard = cardQueue.peek();

            if(currentCard.getNextReview().isAfter(LocalDate.now())) {

                cardQueue.remove();

                if(cardQueue.isEmpty()) {
                    noMoreDueCards();
                    return;
                }

            }else
                break;
        }

        System.out.println(currentCard.getNextReview().toString());//-------------------------------------------------------------------------------

        vBox.getChildren().add(currentCard.getUpperVbox());
    }

    private void showAnswer() {
        vBox.getChildren().addAll(new Separator(),currentCard.getLowerVbox());
    }

    private void decreaseNewCardCount() {

        int updatedValue = Integer.parseInt(newCountMain.getText());
        newCountMain.setText(String.valueOf(--updatedValue));
    }

    private void decreaseDueCardCount() {

        int updatedValue = Integer.parseInt(dueCountMain.getText());
        dueCountMain.setText(String.valueOf(--updatedValue));
        cardsToStudyCount.setText(String.valueOf(updatedValue));
    }

    private void noMoreDueCards() {

        hasCardsLeft = false;
        studyButton.setDisable(true);

        vBox.getChildren().clear();
        vBox.getChildren().add(new Text("No more cards for today."));
    }

    public void difficultyButtonPushed(ActionEvent actionEvent) {

        Button button = (Button) actionEvent.getSource();

        if(currentCard.getNextReview().isEqual(LocalDate.of(1111,11,11))) //New cards will always have 11111111 as review date.
            decreaseNewCardCount();

        switch(button.getId()) {

            case "hardButton":
                currentCard.setNextReview(CardButtons.HARD);
                break;

            case "mediumButton":
                currentCard.setNextReview(CardButtons.MEDIUM);
                break;

            case "easyButton":
                currentCard.setNextReview(CardButtons.EASY);
                break;

            case "veryEasyButton":
                currentCard.setNextReview(CardButtons.VERY_EASY);
                break;
        }

        //Place card last if the review date is today or earlier.
        cardQueue.offer(cardQueue.poll());

        if(currentCard.getNextReview().isAfter(LocalDate.now()))
            decreaseDueCardCount();

        updateBatch.addReviewUpdate(currentCard);
        goToNextCard();
    }

    public void searchButtonPushed() {

    }

    //Show answer when enter is pushed.
    public void keyPushed(KeyEvent keyEvent) {

        if(hasCardsLeft) {
            if(keyEvent.getCode().equals(KeyCode.ENTER))
                if(!answerIsShowing) {

                    showAnswer();
                    answerIsShowing = true;
                    setButtons(ButtonMode.ENABLE);
                }
        }
    }

    //Show answer when mouse is double-clicked.
    public void mouseClicked(MouseEvent event) {

        if(hasCardsLeft) {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    if(!answerIsShowing) {

                        showAnswer();
                        answerIsShowing = true;
                        setButtons(ButtonMode.ENABLE);
                    }
                }
            }
        }
    }


    private void setButtons(ButtonMode buttonMode) {

        switch(buttonMode) {

            case ENABLE:
                hardButton.setDisable(false);
                mediumButton.setDisable(false);
                easyButton.setDisable(false);
                veryEasyButton.setDisable(false);
                break;

            case DISABLE:
                hardButton.setDisable(true);
                mediumButton.setDisable(true);
                easyButton.setDisable(true);
                veryEasyButton.setDisable(true);
                break;
        }

    }

    @Override
    public Window getWindow() {
        return hardButton.getScene().getWindow();
    }
}

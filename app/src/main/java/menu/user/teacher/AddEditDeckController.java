package menu.user.teacher;

import deck.Deck;
import deck.card.Card;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import menu.ChangeableWindow;
import menu.ControlStage;
import menu.UserController;
import menu.WindowUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class AddEditDeckController implements UserController, ChangeableWindow {

    @FXML private VBox mainVbox;
    @FXML private Button searchButton;
    @FXML private Button removeButton;
    @FXML private Button nextButton;
    @FXML private Button previousButton;
    @FXML private VBox vBoxDefault;

    private TextField searchTextField = new TextField();
    private HBox searchHbox = new HBox();

    private final ExecutorService searchFieldThread = Executors.newSingleThreadExecutor();
    private final AtomicLong lastInputTime = new AtomicLong();

    private Deck deck;
    private ListIterator<Card> iterator;
    private ListIterator<Card> iteratorSearch;
    private List<Card> searchResult = new ArrayList<>();
    private Card latestViewed;

    private enum CardAmount {NONE, ONE, MANY;};
    private enum CurrentlyShowing {SEARCH_RESULT, NORMAL;};
    private CurrentlyShowing currentlyShowing;
    private CardAmount cardAmount;

    private boolean isWaiting = false;
    private boolean cardRemoved = false;
    private boolean nextPushedLast;
    private boolean previousPushedLast;

    @Override
    public void initData(Object ...obj) {

        this.deck = (Deck) obj[0];
        this.iterator = deck.getCardIterator();
        setCardAmount(deck.getDeckSize());

        setGUI();
    }

    private void setGUI() {

        this.currentlyShowing = CurrentlyShowing.NORMAL;
        vBoxDefault.setAlignment(Pos.CENTER);
        previousButton.setDisable(true);
        searchHbox.setAlignment(Pos.CENTER);
        searchHbox.setPrefHeight(30);
        searchHbox.setSpacing(15);
        searchTextField = new TextField();
        searchTextField.setPromptText("Search for text or picture names");
        searchTextField.setMaxWidth(250);
        searchTextField.setOnKeyPressed(event -> searchTextFieldInput());
        searchHbox.getChildren().addAll(searchTextField);
        if(cardAmount != CardAmount.NONE) {nextButtonPushed();}
    }

    @FXML
    private void addButtonPushed(){

        searchTextField.clear();

        if(currentlyShowing == CurrentlyShowing.SEARCH_RESULT) {

            mainVbox.getChildren().remove(3);
            currentlyShowing = CurrentlyShowing.NORMAL;
        }

        ControlStage ct = WindowUtil.addWindowOnTop(getClass().getResource("/fxml/addCardWindow.fxml"));
        ct.getController().initData(deck);
        Stage stage = ct.getStage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        afterAddButton();
    }

    private void afterAddButton() {

        previousPushedLast = false;

        setCardAmount(deck.getDeckSize());
        iterator = deck.getCardIterator();

        switch(cardAmount){

            case ONE:
                nextButtonPushed();
                break;

            case MANY:

                if(latestViewed == null){
                    nextButtonPushed();
                    previousButton.setDisable(true);

                }else{

                    while(latestViewed != iterator.next());
                    iterator.previous();
                    nextButtonPushed();

                    if(iterator.previousIndex() == 0)
                        previousButton.setDisable(true);
                }
                break;
        }
    }

    public void searchButtonPushed() {

        if(currentlyShowing == (CurrentlyShowing.SEARCH_RESULT)) {

            currentlyShowing = CurrentlyShowing.NORMAL;

            mainVbox.getChildren().remove(2);
            searchTextField.clear();
            searchResult.clear();
            previousPushedLast = false;
            setCardAmount(deck.getDeckSize());

            iterator = deck.getCardIterator();

            if(cardRemoved && cardAmount != CardAmount.NONE) {

                cardRemoved = false;
                nextButtonPushed();
                previousButton.setDisable(true);
                return;
            }

            switch(cardAmount) {

                case ONE:
                    nextButtonPushed();
                    break;

                case MANY:
                    while(latestViewed != iterator.next());
                    iterator.previous();
                    nextButtonPushed();

                    if(iterator.previousIndex() == 0)
                        previousButton.setDisable(true);

                    break;
            }
            return;
        }

        currentlyShowing = CurrentlyShowing.SEARCH_RESULT;
        vBoxDefault.getChildren().clear();
        setCardAmount(0);
        searchButton.setDisable(false);
        mainVbox.getChildren().add(2,searchHbox);
    }

    //Updates the GUI after searchExecute.
    private final Runnable updateGUI = () -> {

        setCardAmount(searchResult.size());

        if(cardAmount != CardAmount.NONE) {

            previousPushedLast = false;
            nextButtonPushed();
        }

        searchButton.setDisable(false);
        previousButton.setDisable(true);
    };

    //Wait until the last time input was entered + delay - then execute the search, otherwise postpones the search.
    private final Runnable searchExecute = () -> {

        while(lastInputTime.get() > System.currentTimeMillis());

        searchResult.clear();
        searchResult.addAll(DeckUtil.searchCardText(deck, searchTextField.getText()));
        iteratorSearch = searchResult.listIterator();
        isWaiting = false;

        Platform.runLater(updateGUI);
    };

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

    @FXML
    private void nextButtonPushed() {

        Card card;

        if(currentlyShowing == CurrentlyShowing.SEARCH_RESULT){
            if(previousPushedLast)
                iteratorSearch.next();
            card = iteratorSearch.next();
        }else{
            if(previousPushedLast)
                iterator.next();
            card = iterator.next();
        }
        latestViewed = card;

        vBoxDefault.getChildren().clear();
        vBoxDefault.getChildren().addAll(card.getWholeVbox());

        nextPushedLast = true;
        previousPushedLast = false;

        setButtons();
    }

    @FXML
    private void previousButtonPushed() {

        Card card;

        if(currentlyShowing == CurrentlyShowing.SEARCH_RESULT) {

            if(nextPushedLast)
                iteratorSearch.previous();
            card = iteratorSearch.previous();
        }else {

            if(nextPushedLast)
                iterator.previous();
            card = iterator.previous();
        }

        latestViewed = card;
        vBoxDefault.getChildren().clear();
        vBoxDefault.getChildren().addAll(card.getWholeVbox());

        nextPushedLast = false;
        previousPushedLast = true;

        setButtons();
    }

    public void removeButtonPushed() {

        try {
            DatabaseUtil.removeCard(latestViewed.getCardId(), deck.getCourseId());
            deck.setIoStatus(IOStatus.CHANGED);

            switch(currentlyShowing) {

                case NORMAL:
                    removeCard();
                    break;

                case SEARCH_RESULT:
                    removeCardFromSearch();
                    break;
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //Remove card from the deck
    private void removeCard() {

        switch(cardAmount){

            case ONE:
                vBoxDefault.getChildren().clear();
                iterator.remove();
                cardAmount = CardAmount.NONE;
                setButtons();
                break;

            case MANY:
                iterator.remove();
                previousPushedLast = false;

                setCardAmount(deck.getDeckSize());

                if(iterator.hasNext()){
                    nextButtonPushed();
                    if(iterator.previousIndex() == 0)
                        previousButton.setDisable(true);
                }else{
                    previousButtonPushed();
                    if(iterator.nextIndex() == deck.getDeckSize()-1)
                        nextButton.setDisable(true);
                }
        }
    }

    //Remove cards from search result and from the real deck
    private void removeCardFromSearch() throws SQLException {

        switch(cardAmount){

            case ONE:
                deck.removeCard(latestViewed);
                iteratorSearch.remove();
                setCardAmount(0);
                searchButton.setDisable(false);
                cardRemoved = true;
                break;

            case MANY:
                deck.removeCard(latestViewed);
                iteratorSearch.remove();
                setCardAmount(searchResult.size());
                searchButton.setDisable(false);
                cardRemoved = true;
                break;
        }
    }

    //Show buttons according to deck size & view mode
    private void setButtons() {

        switch(cardAmount) {

            case NONE:
                removeButton.setDisable(true);
                nextButton.setDisable(true);
                previousButton.setDisable(true);
                searchButton.setDisable(true);
                return;

            case ONE:
                removeButton.setDisable(false);
                nextButton.setDisable(true);
                previousButton.setDisable(true);
                searchButton.setDisable(true);
                return;

            case MANY:
                removeButton.setDisable(false);
                searchButton.setDisable(false);
                break;
        }

        nextButton.setDisable(true);
        previousButton.setDisable(true);

        if(nextPushedLast) {

            switch(currentlyShowing){

                case SEARCH_RESULT:
                    if(iteratorSearch.hasNext())
                        nextButton.setDisable(false);
                    else
                        nextButton.setDisable(true);


                    if(iteratorSearch.hasPrevious())
                        previousButton.setDisable(false);
                    else
                        previousButton.setDisable(true);
                    break;

                case NORMAL:
                    if(iterator.hasNext())
                        nextButton.setDisable(false);
                    else
                        nextButton.setDisable(true);


                    if(iterator.hasPrevious())
                        previousButton.setDisable(false);
                    else
                        previousButton.setDisable(true);
                    break;
            }

        }else if(previousPushedLast) {

            switch(currentlyShowing){

                case SEARCH_RESULT:
                    if(iteratorSearch.hasNext())
                        nextButton.setDisable(false);
                    else
                        nextButton.setDisable(true);


                    if(iteratorSearch.hasPrevious())
                        previousButton.setDisable(false);
                    else
                        previousButton.setDisable(true);
                    break;

                case NORMAL:
                    if(iterator.hasNext())
                        nextButton.setDisable(false);
                    else
                        nextButton.setDisable(true);


                    if(iterator.hasPrevious())
                        previousButton.setDisable(false);
                    else
                        previousButton.setDisable(true);
                    break;
            }
        }
    }

    //Set card amount enum according to deck/list size
    private void setCardAmount(int size) {

        if(size == 0) {

            cardAmount = CardAmount.NONE;
            vBoxDefault.getChildren().clear();
            setButtons();

        }else if(size == 1) {
            cardAmount = CardAmount.ONE;

        }else if(size > 1) {
            cardAmount = CardAmount.MANY;
        }
    }

    @Override
    public Window getWindow() {
        return mainVbox.getScene().getWindow();
    }
}

package menu.user.teacher;

import deck.Deck;
import deck.card.Card;
import deck.card.component.ImageViewComponent;
import deck.card.component.SeparatorComponent;
import deck.card.component.TextAreaComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import menu.ChangeableWindow;
import menu.UserController;
import menu.WindowUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class AddCardController implements UserController, ChangeableWindow {

    @FXML private Button addCardButton;
    @FXML private ComboBox<String> frontComboBox;
    @FXML private ComboBox<String> backComboBox;
    @FXML private VBox vBoxCard;

    private TextArea frontTextArea = new TextArea();
    private TextArea backTextArea = new TextArea();

    private String front;
    private String back;

    private Image frontImage;
    private Image backImage;

    private enum CardSide {FRONT, BACK};
    private Deck deck;

    @Override
    public void initData(Object ...obj) {

        this.deck = (Deck) obj[0];
        setGUI();
    }

    private void setGUI() {

        addCardButton.setDisable(true);
        frontComboBox.setValue("Text");
        frontComboBox.getItems().add("Text");
        frontComboBox.getItems().add("Picture");
        frontComboBox.getItems().add("Picture + Text");
        backComboBox.setValue("Text");
        backComboBox.getItems().add("Text");
        backComboBox.getItems().add("Picture");
        backComboBox.getItems().add("Picture + Text");
        frontTextArea.setWrapText(true);
        frontTextArea.setMinSize(400,300);
        backTextArea.setWrapText(true);
        backTextArea.setMinSize(400,300);
    }

    @FXML
    private void createFrameButtonPushed() {

        vBoxCard.getChildren().clear();
        addCardButton.setDisable(false);

        //Get the current options chosen by the user
        front = frontComboBox.getSelectionModel().getSelectedItem();
        back = backComboBox.getSelectionModel().getSelectedItem();

        frontTextArea.clear();
        backTextArea.clear();


        //Automatically add new text areas after a card has been added.
        //If a file chooser was opened and no picture was chosen - return.
        if(!createCardSide(front, CardSide.FRONT)) {
            vBoxCard.getChildren().clear();
            return;
        }

        vBoxCard.getChildren().add(new SeparatorComponent().convertToNode());

        //Same as above but for the "back" of the card.
        if(!createCardSide(back, CardSide.BACK))
            vBoxCard.getChildren().clear();
    }

    //Adds the chosen JavaFX nodes to GUI.
    private boolean createCardSide(String option, CardSide side) {

        Image image = null;
        TextArea textArea = new TextArea();

        //Open a file chooser if the option "Picture" was chosen.
        if(option.contains("Picture")) {

            Optional<Image> imageOpt = WindowUtil.openPictureFileChooser(getWindow());

            //Return false if the user didn't chose an image in the file chooser.
            if(imageOpt.isEmpty()) {return false;}
            else {

                image = imageOpt.get();
                ImageView imageView = new ImageView(image);

                if(image.getWidth() > 400 || image.getHeight() > 400) {

                    vBoxCard.getChildren().add(new Text("The picture will become bigger when you exit edit-mode"));
                    imageView.setPreserveRatio(true);
                    imageView.setFitHeight(400);
                    imageView.setFitWidth(400);
                }

                vBoxCard.getChildren().add(imageView);
            }
        }

        if(option.contains("Text"))
            vBoxCard.getChildren().add(textArea);

        switch(side) {

            case FRONT:
                frontTextArea = textArea;
                frontImage = image;
                break;

            case BACK:
                backTextArea = textArea;
                backImage = image;
                break;
        }

        return true;
    }

    private boolean textAreasAreEmpty(TextArea ...texts) {

        for(TextArea text : texts)
            if(text.getText().trim().isEmpty())
                return true;

        return false;
    }

    private boolean picturesAreEmpty(Image ...images) {

        for(Image image : images)
            if(Objects.isNull(image) || image.isError())
                return true;

        return false;
    }
    @FXML
    private void addButtonPushed() throws IOException, SQLException {

        if(deck.getDeckSize() >= 200) {
            WindowUtil.createPopUpWarning(getWindow(), "You can't have more than 200 cards");
            return;
        }

        Card card = null;
        String cards = front + " " + back;
        int courseId = deck.getCourseId();

        //Builds a card with all the different combinations of card-components.
        switch(cards) {

            case "Text Text":
                if(textAreasAreEmpty(frontTextArea, backTextArea)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new TextAreaComponent(frontTextArea.getText())).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new TextAreaComponent(backTextArea.getText())).build();
                break;

            case "Text Picture":
                if(textAreasAreEmpty(frontTextArea) || picturesAreEmpty(backImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new TextAreaComponent(frontTextArea.getText())).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new ImageViewComponent(backImage)).build();

                break;

            case "Text Picture + Text":
                if(textAreasAreEmpty(frontTextArea, backTextArea) || picturesAreEmpty(frontImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new TextAreaComponent(frontTextArea.getText())).
                        withCardComponent(new ImageViewComponent(frontImage)).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new TextAreaComponent(backTextArea.getText())).build();
                break;

            case "Picture Text":
                if(textAreasAreEmpty(backTextArea) || picturesAreEmpty(frontImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new ImageViewComponent(frontImage)).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new TextAreaComponent(backTextArea.getText())).build();
                break;

            case "Picture Picture":
                if(picturesAreEmpty(frontImage,backImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new ImageViewComponent(frontImage)).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new ImageViewComponent(backImage)).build();
                break;

            case "Picture Picture + Text":
                if(textAreasAreEmpty(backTextArea) || picturesAreEmpty(frontImage,backImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new ImageViewComponent(frontImage)).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new ImageViewComponent(backImage)).
                        withCardComponent(new TextAreaComponent(backTextArea.getText())).build();
                break;

            case "Picture + Text Text":
                if(textAreasAreEmpty(frontTextArea, backTextArea) || picturesAreEmpty(frontImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new ImageViewComponent(frontImage)).
                        withCardComponent(new TextAreaComponent(frontTextArea.getText())).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new TextAreaComponent(backTextArea.getText())).build();
                break;

            case "Picture + Text Picture":
                if(textAreasAreEmpty(frontTextArea) || picturesAreEmpty(frontImage, backImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new ImageViewComponent(frontImage)).
                        withCardComponent(new TextAreaComponent(frontTextArea.getText())).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new ImageViewComponent(backImage)).build();
                break;

            case "Picture + Text Picture + Text":
                if(textAreasAreEmpty(frontTextArea, backTextArea) || picturesAreEmpty(frontImage, backImage)) {
                    WindowUtil.createPopUpWarning(getWindow(), "You cant add cards with empty fields");
                    return;
                }

                card = new Card.CardBuilder(DatabaseUtil.addNewCard(courseId)).
                        withCardComponent(new ImageViewComponent(frontImage)).
                        withCardComponent(new TextAreaComponent(frontTextArea.getText())).
                        withCardComponent(new SeparatorComponent()).
                        withCardComponent(new ImageViewComponent(backImage)).
                        withCardComponent(new TextAreaComponent(backTextArea.getText())).build();
                break;
        }

        assert card != null;
        card.setIoStatus(IOStatus.NEW);

        //A new card was added, so the deck has to change IO-status.
        deck.addCard(card);
        deck.setIoStatus(IOStatus.CHANGED);

        clearAllFields();
    }

    //Clears all the fields for a new clean card.
    private void clearAllFields() {

        boolean hadImageView = vBoxCard.getChildren().removeIf(node -> node instanceof ImageView);
        vBoxCard.getChildren().removeIf(node -> node instanceof Text);

        frontImage = null;
        backImage = null;
        frontTextArea.clear();
        backTextArea.clear();

        //To make it faster to create another card frame with an image in it.
        if(hadImageView) {

            vBoxCard.getChildren().clear();

            Button addPicture = new Button("Create a card with the same frame");

            addPicture.setPrefSize(400,200);
            addPicture.setStyle("-fx-background-color: white; -fx-border-style: solid");
            addPicture.setOnAction(event -> createFrameButtonPushed());

            vBoxCard.getChildren().add(addPicture);
        }
    }

    @Override
    public Window getWindow() {
        return addCardButton.getScene().getWindow();
    }
}

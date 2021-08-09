package deck.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import deck.IOObject;
import deck.IOStatus;
import deck.card.component.CardComponents;
import deck.card.component.SeparatorComponent;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.*;

/**<h1>Card</h1>
 *
 * Cards are created by it's builder class "CardBuilder". A card must consist of 3 card components of which one must be a separator component.
 * All the components are stored in a list.
 * <p>All cards has a general difficulty which is modifier for when the next review date will be, the range is 2.0-0.1 - 2.0 Easy - 0.1 Hard.</p>
 * <p>The io-status shows if any changes are made to the card. If a decks' cards all are unchanged there is no need to be save and overwrite to the server/PC -
 * unless the status of the card is "new" or "changed".
 * </p>
 * <p>Cards are naturally sorted by their card ID's.</p>
 * @author Jonas Elmesten
 */
@JsonDeserialize(builder = Card.CardBuilder.class)
public class Card implements Comparable<Card>, IOObject {

    private final static Logger logger = LogManager.getLogger(Card.class);
    private final ReviewInfo reviewInfo = new ReviewInfo();
    private final List<CardComponent> cardComponentList;
    private final int CARD_ID;

    private IOStatus ioStatus = IOStatus.UNCHANGED;
    private LocalDate nextReview;
    private double generalDifficulty = 1.0;


    private Card(CardBuilder builder) {
        this.CARD_ID = builder.CARD_ID;
        this.cardComponentList = builder.cardComponentList;
    }

    public static class CardBuilder {

        private final int CARD_ID;
        @JsonProperty("cardComponents")
        private ArrayList<CardComponent> cardComponentList = new ArrayList<>();

        public CardBuilder(@JsonProperty("cardId") int CARD_ID) {

            logger.debug("Starting to build card - CardId:" + CARD_ID);

            this.CARD_ID = CARD_ID;
        }

        public CardBuilder withCardComponent(CardComponent cardComponent) {

            logger.debug("Adding component - " + cardComponent.getComponentEnum().toString() + " - CardId:" + CARD_ID);

            this.cardComponentList.add(cardComponent);
            return this;
        }

        public Card build() {

            Card card = new Card(this);

            if(card.cardComponentList.size() < 3 || !(card.containsSeparator()))
                throw new IncorrectCardFormatException("A card must consist of 3 or more card components, separator component included");

            logger.debug("Building card - CardId:" + CARD_ID);
            return card;
        }
    }

    private boolean containsSeparator() {
       return cardComponentList.stream().anyMatch(element -> element.getComponentEnum().equals(CardComponents.SEPARATOR));
    }

    //A card that has yet to be reviewed will always have nextReview set to 11111111.
    public boolean isFirstReview() {
        return nextReview.isEqual(LocalDate.of(1111,11,11));
    }

    /**Used to show the whole card. Answer & question.*/
    public VBox getWholeVbox() {

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        for(CardComponent c: cardComponentList)
            vBox.getChildren().add(c.convertToNode());

        return vBox;
    }

    /**Used to show just the question of the card.*/
    public VBox getUpperVbox() {

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        for(CardComponent c: cardComponentList) {

            if(c instanceof SeparatorComponent) {break;}

            vBox.getChildren().add(c.convertToNode());
        }

        return vBox;
    }

    /**Used to show just the answer of the card.*/
    public VBox getLowerVbox() {

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        boolean isUnderSeparator = false;

        for(CardComponent c: cardComponentList) {

            if(isUnderSeparator)
                vBox.getChildren().add(c.convertToNode());
            else {
                if(c instanceof SeparatorComponent)
                    isUnderSeparator = true;
            }
        }

        return vBox;
    }

    public int getCardId() {
        return CARD_ID;
    }

    public double getGeneralDifficulty() {
        return generalDifficulty;
    }

    public LocalDate getNextReview() {
        return nextReview;
    }

    @Override
    public IOStatus getIoStatus() {
        return ioStatus;
    }

    public List<CardComponent> getComponentList() {
        return Collections.unmodifiableList(cardComponentList);
    }

    public void setGeneralDifficulty(double generalDifficulty) {

        logger.debug("Setting difficulty to:" + generalDifficulty + " CardId:" + CARD_ID);
        this.generalDifficulty = generalDifficulty;
    }

    public void setReviewStats(int[] reviewStats) {

        logger.debug("Setting review stats to:" + Arrays.toString(reviewStats) + " CardId:" + CARD_ID);

        assert reviewStats.length == 4;
        reviewInfo.setPushCount(reviewStats);
    }

    public void setNextReview(String date) {

        logger.debug("Setting next review to date:" + date + " CardId:" + CARD_ID);
        nextReview = LocalDate.parse(date);
    }

    public int[] getCardReviewStats() {
        return reviewInfo.getButtonValues();
    }

    public void setNextReview(CardButtons buttonPushed) {

        logger.debug("Setting next review - Button pushed:" + buttonPushed.toString() + " CardId:" + CARD_ID);
        nextReview = reviewInfo.incrementReviewInfo(buttonPushed, nextReview, generalDifficulty);
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(CARD_ID, o.getCardId());
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == this) return true;
        if(!(obj instanceof Card)) return false;
        Card card = (Card) obj;

        //All cards will be uniquely identifiable just by their card ID.
        return this.CARD_ID == card.getCardId();
    }

    @Override
    public int hashCode() {
        //All cards will be uniquely identifiable just by their card ID.
        return Objects.hash(CARD_ID);
    }

    @Override
    public void setIoStatus(IOStatus ioStatus) {

        logger.debug("Setting Io status to " + ioStatus.toString() + " CardId:" + CARD_ID);
        this.ioStatus = ioStatus;
    }

    @Override
    public String toString() {

        String string = "Card id: " + getCardId() + "Difficulty: " + generalDifficulty + " Components: ";

        for(CardComponent c: cardComponentList){
            string = string.concat(c.getComponentEnum().toString() + " - ");
            string = string.concat(c.getRawObject() + " - ");
        }
        string = string.concat(nextReview.toString());

        return string;
    }
}


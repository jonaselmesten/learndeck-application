package deck.card;

import deck.card.component.CardComponents;

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
 */
public class Card implements Comparable<Card> {

    private final ReviewInfo reviewInfo = new ReviewInfo();
    private final List<CardComponent> cardComponentList;
    private final int CARD_ID;

    private LocalDate nextReview;
    private double generalDifficulty = 1.0;


    private Card(CardBuilder builder) {
        this.CARD_ID = builder.CARD_ID;
        this.cardComponentList = builder.cardComponentList;
    }

    public static class CardBuilder {

        private final int CARD_ID;

        private ArrayList<CardComponent> cardComponentList = new ArrayList<>();

        public CardBuilder(int CARD_ID) {
            this.CARD_ID = CARD_ID;
        }

        public CardBuilder withCardComponent(CardComponent cardComponent) {
            this.cardComponentList.add(cardComponent);
            return this;
        }

        public Card build() {

            Card card = new Card(this);

            if(card.cardComponentList.size() < 3 || !(card.containsSeparator()))
                throw new IncorrectCardFormatException("A card must consist of 3 or more card components, separator component included");

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


    public int getCardId() {
        return CARD_ID;
    }

    public double getGeneralDifficulty() {
        return generalDifficulty;
    }

    public LocalDate getNextReview() {
        return nextReview;
    }

    public List<CardComponent> getComponentList() {
        return Collections.unmodifiableList(cardComponentList);
    }

    public void setGeneralDifficulty(double generalDifficulty) {
        this.generalDifficulty = generalDifficulty;
    }

    public void setReviewStats(int[] reviewStats) {
        assert reviewStats.length == 4;
        reviewInfo.setPushCount(reviewStats);
    }

    public int[] getCardReviewStats() {
        return reviewInfo.getButtonValues();
    }

    public void setNextReview(CardButtons buttonPushed) {
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


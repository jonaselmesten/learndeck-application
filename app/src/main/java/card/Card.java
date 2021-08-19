package card;

import android.view.View;

import java.util.*;

/**<h1>Card</h1>
 *
 * Cards are created by it's builder class "CardBuilder". A card must consist of 3 card components of which one must be a separator component.
 * All the components are stored in a list.
 * All cards has a general difficulty which is modifier for when the next review date will be, the range is 2.0-0.1 - 2.0 Easy - 0.1 Hard.
 * <p>Cards are naturally sorted by their card ID's.</p>
 */
public class Card implements Comparable<Card> {

    private final ReviewInfo reviewInfo = new ReviewInfo();
    private final List<CardComponent> cardComponentList;
    private final int CARD_ID;

    private double generalDifficulty = 1.0;

    private Card(CardBuilder builder) {
        this.CARD_ID = builder.CARD_ID;
        this.cardComponentList = builder.cardComponentList;
    }

    public View loadQuestionGui() {
        return null;
    }

    public View loadAnswerGui() {
        return null;
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

            if(card.cardComponentList.size() < 3)
                throw new IncorrectCardFormatException("A card must consist of 3 or more card components, separator component included");

            return card;
        }
    }

    //A card that has yet to be reviewed will always have nextReview set to 11111111.
    public boolean isFirstReview() {
        return false;
    }

    public int getCardId() {
        return CARD_ID;
    }

    public double getGeneralDifficulty() {
        return generalDifficulty;
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
            string = string.concat(c.getRawObject() + " - ");
        }

        return string;
    }
}


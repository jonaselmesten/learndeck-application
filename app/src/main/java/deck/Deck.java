package deck;


import deck.card.Card;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**<h1>Deck</h1>
 *
 * Deck holds all of the cards and can
 * <p>The io-status shows if any changes are made to the deck. If a deck and all of it's cards are unchanged there is no need overwrite the file already saved on the server/PC -
 * unless the status of a card or the deck is "new" or "changed".
 * </p>
 * <p>Decks are naturally sorted by their course names.</p>
 * @author Jonas Elmesten
 */
public class Deck implements Comparable<Deck>, IOObject {

    private final static Logger logger = LogManager.getLogger(Deck.class);
    private final List<Card> cardList = new ArrayList<>();
    private final String COURSE_NAME;

    private IOStatus ioStatus = IOStatus.UNCHANGED;
    private final int COURSE_ID;

    public Deck(String COURSE_NAME, int COURSE_ID) {
        this.COURSE_NAME = COURSE_NAME;
        this.COURSE_ID = COURSE_ID;
    }

    public void addCard(Card card) {

        logger.debug("Method call:addCard CourseName:" + COURSE_NAME +" Id:" + COURSE_ID + "CardId:" + card.getCardId());
        cardList.add(card);
    }

    public void removeCard(Card card) {

        logger.debug("Method call:removeCard CourseName:" + COURSE_NAME +" Id:" + COURSE_ID + "CardId:" + card.getCardId());

        ioStatus = IOStatus.CHANGED;
        cardList.remove(card);
    }

    public void sortCardsAfterId() {

        logger.debug("Method call:sortCardsAfterId CourseName:" + COURSE_NAME + " Id:" + COURSE_ID + " - Sorting deck with Collections.sort()");

        Collections.sort(cardList);
    }

    public void sortCardsAfterReviewDate() {

        logger.debug("Method call:sortAfterReviews CourseNameId" + COURSE_NAME + COURSE_ID + " - Sorting deck with Comparator");

        cardList.sort(new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
               return o1.getNextReview().compareTo(o2.getNextReview());
            }
        });
    }

    @Override
    public void setIoStatus(IOStatus ioStatus) {

        logger.debug("Method call:setIoStatus CourseNameId:" + COURSE_NAME + COURSE_ID + "Setting Io-status to:" +ioStatus.toString());
        this.ioStatus = ioStatus;
    }

    @Override
    public IOStatus getIoStatus() {
        return ioStatus;
    }

    public List<Card> getImmutableList() {
        return Collections.unmodifiableList(cardList);
    }

    public ListIterator<Card> getCardIterator() {
        return cardList.listIterator();
    }

    public String getCourseName() {
        return COURSE_NAME;
    }

    public int getCourseId() {
        return COURSE_ID;
    }

    public int getDeckSize() {
        return cardList.size();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == this) return true;
        if(!(obj instanceof Deck)) return false;
        Deck deck = (Deck) obj;

        //All decks will be uniquely identifiable just by their course name.
        return this.COURSE_NAME.equals(deck.getCourseName());
    }

    @Override
    public int hashCode() {
        //All decks will be uniquely identifiable just by their course name.
        return Objects.hash(COURSE_NAME);
    }

    @Override
    public int compareTo(Deck deck) {
        return this.COURSE_NAME.compareTo(deck.getCourseName());
    }

    @Override
    public String toString() {
        return "Course name:" + COURSE_NAME + " Id:" + COURSE_ID + " Size:" + getDeckSize() + " IoStatus:" + getIoStatus();
    }
}

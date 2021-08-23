package model;

import com.google.gson.annotations.SerializedName;
import card.Card;

import java.util.*;

/**<h1>Deck</h1>
 *
 */
public class Deck implements Comparable<Deck> {

    private final List<Card> cardList = new ArrayList<>();

    @SerializedName("courseId")
    private final Long courseId;
    @SerializedName("courseName")
    private String courseName;
    @SerializedName("dueCount")
    private int dueCount;

    public Deck(String courseName, long courseId, int dueCount) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.dueCount = dueCount;
    }

    public void addCard(Card card) {
        cardList.add(card);
    }

    public void removeCard(Card card) {
        cardList.remove(card);
    }

    public void sortCardsAfterId() {}

    public void sortCardsAfterReviewDate() {}

    public List<Card> getImmutableList() {
        return Collections.unmodifiableList(cardList);
    }

    public ListIterator<Card> getCardIterator() {
        return cardList.listIterator();
    }

    public int getDeckSize() {
        return cardList.size();
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == this) return true;
        if(!(obj instanceof Deck)) return false;
        Deck deck = (Deck) obj;

        return this.courseId.equals(deck.getCourseId());
    }

    @Override
    public int hashCode() { return courseId.intValue(); }

    @Override
    public int compareTo(Deck deck) {
        return this.courseId.compareTo(deck.courseId);
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getDueCount() { return dueCount; }

    public Card getNextReview() {
        return cardList.get(0);
    }

    public void fillDeck(Deck deck) {

    }
}


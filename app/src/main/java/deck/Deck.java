package deck;

import card.Card;
import model.DeckResponse;

import java.util.*;

/**<h1>Deck</h1>
 *
 */
public class Deck implements Comparable<Deck> {

    private final TreeSet<Card> cardSet = new TreeSet<>();

    private final Long courseId;
    private String courseName;
    private int dueCount;

    public Deck(String courseName, long courseId, int dueCount) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.dueCount = dueCount;
    }

    public static Deck fromResponse(DeckResponse deck) {
        return new Deck(deck.getCourseName(), deck.getCourseId(), deck.getDueCount());
    }

    public void addCard(Card card) {
        cardSet.add(card);
    }

    public void removeCard(Card card) {
        cardSet.remove(card);
    }

    public int getDeckSize() {
        return cardSet.size();
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

    public Iterator<Card> getIterator() {
        return cardSet.iterator();
    }
}


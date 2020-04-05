package deck.serializer;

import java.util.*;

class CardReviewHolder {

    private final Map<Integer, Map<Integer, TempReview>> reviewMap = new TreeMap<>();

    TempReview getReview(int courseId, int cardId) {
        return reviewMap.get(courseId).get(cardId);
    }

    void addCourse(int courseId, Map<Integer, TempReview> cardReviews) {
        reviewMap.put(courseId, cardReviews);
    }
}

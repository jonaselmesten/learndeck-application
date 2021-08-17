package connection;

import card.Card;
import exceptions.ConnectionException;

import java.util.List;

interface CardDao {

    void updateReview(int reviewId);

    void resetReviews() throws ConnectionException;

    List<Card> getCards(int courseId);

}

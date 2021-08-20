package connection;

import card.Card;
import exceptions.ConnectionException;

import java.io.IOException;
import java.util.List;

interface CardDao {

    void updateReview(int reviewId);

    void resetReviews() throws ConnectionException;

    List<Card> getCards(int courseId) throws ConnectionException, IOException;

}

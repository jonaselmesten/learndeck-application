package connection;

import activity.DeckActivity;
import card.Card;
import exceptions.ConnectionException;

import java.util.List;

import static activity.DeckActivity.USER_ID;

public class CardConnection implements CardDao {

    private final WebserviceConnection connection = new WebserviceConnection();

    @Override
    public void updateReview(int reviewId) {

    }

    @Override
    public void resetReviews() throws ConnectionException {

    }

    @Override
    public List<Card> getCards(int courseId) throws ConnectionException {
        return connection.getDeckCards(courseId, USER_ID);
    }
}

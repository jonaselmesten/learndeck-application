package connection;

import card.Card;
import exceptions.ConnectionException;

import java.util.List;

public class CardConnection implements CardDao {

    private final WebserviceConnection connection = new WebserviceConnection();

    @Override
    public void updateReview(int reviewId) {

    }

    @Override
    public void resetReviews() throws ConnectionException {

    }

    @Override
    public List<Card> getCards(int courseId) {
        return null;
    }
}

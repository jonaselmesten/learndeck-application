package connection;

import deck.card.Card;

import java.util.List;
import java.util.Optional;

public class CardDao implements Dao<Card> {

    @Override
    public Optional<Card> get(long id) {
        return Optional.empty();
    }

    @Override
    public List<Card> getAll() {
        return null;
    }

    @Override
    public List<Card> getAllFromUser(long userId) throws ConnectionException {
        return null;
    }

    @Override
    public void update(Card card, String[] params) {

    }

    @Override
    public void delete(Card card) {

    }

    public void resetAllReviews(int userId) throws ConnectionException {
    }
}

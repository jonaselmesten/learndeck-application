package connection;

import model.Deck;

import java.util.List;
import java.util.Optional;

public class DeckDao implements Dao<Deck> {

    WebserviceConnection connection = new WebserviceConnection();

    @Override
    public Optional<Deck> get(long id) {
        return Optional.empty();
    }

    @Override
    public List<Deck> getAll() {
        return null;
    }

    @Override
    public List<Deck> getAllFromUser(long userId) throws ConnectionException {
        return connection.getUserDecks(userId);
    }

    @Override
    public void update(Deck deck, String[] params) {

    }

    @Override
    public void delete(Deck deck) {

    }
}

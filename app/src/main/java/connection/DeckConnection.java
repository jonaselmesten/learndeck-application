package connection;

import deck.Deck;
import exceptions.ConnectionException;

import java.util.List;

public class DeckConnection implements DeckDao {

    private final WebserviceConnection connection = new WebserviceConnection();

    @Override
    public List<Deck> getAll() throws ConnectionException {
        return connection.getUserDecks();
    }

    @Override
    public void deleteFromUser(long id) throws ConnectionException {

    }
}

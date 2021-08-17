package connection;

import exceptions.ConnectionException;
import model.Deck;

import java.util.List;

import static activity.DeckActivity.USER_ID;

public class DeckConnection implements DeckDao {

    private final WebserviceConnection connection = new WebserviceConnection();

    @Override
    public List<Deck> getAll() throws ConnectionException {
        return connection.getUserDecks(USER_ID);
    }

    @Override
    public void deleteFromUser(long id) throws ConnectionException {

    }
}

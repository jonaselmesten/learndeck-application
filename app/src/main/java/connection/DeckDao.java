package connection;

import deck.Deck;
import exceptions.ConnectionException;

import java.util.List;

interface DeckDao {

    List<Deck> getAll() throws ConnectionException;

    void deleteFromUser(long id) throws ConnectionException;

}

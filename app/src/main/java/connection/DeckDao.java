package connection;

import exceptions.ConnectionException;
import model.Deck;

import java.util.List;
import java.util.Optional;

interface DeckDao {

    List<Deck> getAll() throws ConnectionException;

    void deleteFromUser(long id) throws ConnectionException;

}

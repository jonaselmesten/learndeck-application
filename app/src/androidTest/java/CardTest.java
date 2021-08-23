import card.Card;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import connection.CardConnection;
import exceptions.ConnectionException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class CardTest {

    @Test
    public void fetchAllCardFromDeck() throws IOException {

        CardConnection cardDao = new CardConnection();
        List<Card> cards = cardDao.getCards(1);
        
    }

}

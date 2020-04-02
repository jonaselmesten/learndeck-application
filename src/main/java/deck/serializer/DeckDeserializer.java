package deck.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import deck.Deck;
import deck.card.Card;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**Deserialize decks and all cards from JSON-format to usable deck objects.
 * @author Jonas Elmesten
 */
public class DeckDeserializer extends StdDeserializer<Deck> {

    private final static Logger logger = LogManager.getLogger(DeckDeserializer.class);

    public DeckDeserializer(Class<Deck> vc) {
        super(vc);
    }
    public DeckDeserializer() {
        this(null);
    }

    @Override
    public Deck deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        logger.info("Method call:deserialize");
        logger.debug("Method call:deserialize JsonParser:" + jsonParser.toString() + "DeserializationContext" + deserializationContext.toString());

        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        //Create the deck that will hold all the cards
        String courseName = node.get("courseName").asText();
        int courseId = (int) node.get("courseId").numberValue();
        Deck deck = new Deck(courseName, courseId);

        //Read all the cards and add them to the deck created above.
        JsonNode cards = node.get("cards");
        for(JsonNode nodeCard : cards) {

            logger.debug("Object mapping card:" + nodeCard.toString());

            Card card = objectMapper.readValue(nodeCard.toString(), Card.class);
            card.setGeneralDifficulty(nodeCard.get("difficulty").asDouble());

            deck.addCard(card);
        }

        return deck;
    }
}

package deck.serializer.review;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import deck.Deck;
import deck.card.Card;

import java.io.IOException;

public class CardReviewSerializer extends StdSerializer<Deck> {

    public CardReviewSerializer() {
        this(Deck.class);
    }
    public CardReviewSerializer(Class<Deck> t) {
        super(t);
    }

    @Override
    public void serialize(Deck deck, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        deck.sortCardsAfterId();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("courseId", deck.getCourseId());

        jsonGenerator.writeFieldName("cards");
        jsonGenerator.writeStartArray();

        //Go through all the cards in the deck.
        for(Card card : deck.getImmutableList()) {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("cardId", card.getCardId());
            jsonGenerator.writeStringField("nextRev", card.getNextReview().toString());

            jsonGenerator.writeNumberField("h", card.getCardReviewStats()[0]);
            jsonGenerator.writeNumberField("m", card.getCardReviewStats()[1]);
            jsonGenerator.writeNumberField("e", card.getCardReviewStats()[2]);
            jsonGenerator.writeNumberField("ve", card.getCardReviewStats()[3]);

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}

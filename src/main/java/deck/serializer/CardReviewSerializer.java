package deck.card;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class CardReviewSerializer extends StdSerializer<Card> {

    public CardReviewSerializer() {
        this(Card.class);
    }
    public CardReviewSerializer(Class<Card> t) {
        super(t);
    }

    @Override
    public void serialize(Card card, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {




    }
}

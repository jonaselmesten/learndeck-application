package deck;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import deck.card.Card;
import deck.card.CardComponent;
import deck.card.component.ImageViewComponent;
import deck.card.component.SeparatorComponent;
import deck.card.component.TextAreaComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**Serialize decks and all cards to JSON-format for simple storage in files etc.
 * @author Jonas Elmesten
 */
public class DeckSerializer extends StdSerializer<Deck> {

    private final static Logger logger = LogManager.getLogger(DeckSerializer.class);

    public DeckSerializer() {
        this(Deck.class);
    }
    public DeckSerializer(Class<Deck> t) {
        super(t);
    }

    @Override
    public void serialize(Deck deck, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        logger.info("Method call:serialize Deck:" + deck.getCourseName() + " - Id:" + deck.getCourseId());
        logger.debug("Method call:deserialize JsonParser:" + jsonGenerator.toString() + "DeserializationContext" + serializerProvider.toString());

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("courseName", deck.getCourseName());
        jsonGenerator.writeNumberField("courseId", deck.getCourseId());

        jsonGenerator.writeFieldName("cards");
        jsonGenerator.writeStartArray();

        //Go through all the cards in the deck.
        for(Card card : deck.getImmutableList()) {

            logger.debug("Writing JSON - Card frame - CardId:" + card.getCardId());

            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("cardId", card.getCardId());
            jsonGenerator.writeNumberField("difficulty", card.getGeneralDifficulty());

            jsonGenerator.writeFieldName("cardComponents");
            jsonGenerator.writeStartArray();

            //Go through all the components in the card.
            for(CardComponent component : card.getComponentList()) {

                logger.debug("Writing JSON - Card Component - " + component.getComponentEnum().toString());

                jsonGenerator.writeStartObject();

                switch(component.getComponentEnum()) {

                    case IMAGE_VIEW:
                        ImageViewComponent imageView = (ImageViewComponent) component;
                        jsonGenerator.writeStringField("imageView", imageView.getRawObject());
                        break;

                    case SEPARATOR:
                        SeparatorComponent separator = (SeparatorComponent) component;
                        jsonGenerator.writeNumberField("separator", separator.getRawObject());
                        break;

                    case TEXT_AREA:
                        TextAreaComponent textArea = (TextAreaComponent) component;
                        jsonGenerator.writeStringField("textArea", textArea.getRawObject());
                        break;
                }
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}

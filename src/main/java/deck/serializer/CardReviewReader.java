package deck.serializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import deck.Deck;
import deck.card.Card;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*
Current JSON-structure for a students' stored review data:
{
    courseId: 99
    cards: [
        {cardId:01, nextRev:2020-01-01, h:0:, m:0, e:0, ve:0},
        {cardId:02, nextRev:2020-01-01, h:0:, m:0, e:0, ve:0},
        {cardId:03, nextRev:2020-01-01, h:0:, m:0, e:0, ve:0},
    ]
}
 */


public class CardReviewReader {

    private final CardReviewHolder cardReviewHolder = new CardReviewHolder();
    private final Path filePath;

    public CardReviewReader(Path filePath) throws IOException {
        this.filePath = filePath;
        readAllReviews();
    }

    //Read all the reviews from a specific file and then adds them to the review holder for easy retrieval.
    private void readAllReviews() throws IOException {

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {

            ObjectMapper mapper = new ObjectMapper();
            String json;

            //One line of JSON contains one course worth of reviews
            while((json = reader.readLine()) != null) {

                JsonFactory factory = new JsonFactory();
                JsonParser parser = factory.createParser(json);

                while(parser.nextToken() != JsonToken.END_OBJECT) {

                    JsonToken courseId = parser.nextToken();
                    int value = parser.nextIntValue(0);
                    parser.nextToken();
                    parser.nextToken();

                    ArrayNode arrayNode = mapper.readTree(parser);

                    Map<Integer, TempReview> reviewMap = new TreeMap<>();

                    //Read all elements from each card review
                    for(Iterator<JsonNode> it = arrayNode.elements(); it.hasNext(); ) {

                        JsonNode node = it.next();

                        int cardId = node.get("cardId").asInt();
                        String nextRev = node.get("nextRev").asText();
                        int h = node.get("h").asInt();
                        int m = node.get("m").asInt();
                        int e = node.get("e").asInt();
                        int ve = node.get("ve").asInt();


                        TempReview review = new TempReview(nextRev, new int[]{h,m,e,ve});
                        reviewMap.put(cardId, review);
                    }

                    cardReviewHolder.addCourse(value, reviewMap);
                }
                parser.close();
            }
        }
    }

    /**
     * Add all the reviews stored in this object to a specific deck.
     * If review data already exists - all data will be overwritten.
     * @param deck Course to add review data to.
     */
    public void addReviewsToDeck(Deck deck) {

        for(Card card : deck.getImmutableList()) {

            TempReview review = cardReviewHolder.getReview(deck.getCourseId(), card.getCardId());
            card.setNextReview(review.getReviewDate());
            card.setReviewStats(review.getReviewStats());
        }
    }
}

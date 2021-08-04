package deck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import database.CustomThreadFactory;
import deck.card.Card;
import deck.card.CardComponent;
import deck.card.component.CardComponents;
import deck.card.component.TextAreaComponent;
import deck.search.CardTextSearcher;
import deck.serializer.*;
import folder.FileUtil;
import menu.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**<h1>Deck utilities</h1>
 *Utility methods for deck objects.
 * @author Jonas Elmesten
 */
public class DeckUtil {

    private final static Logger logger = LogManager.getLogger(DeckUtil.class);

    /**
     * Searches in all cards' text area-components after the search word.
     * @param deck Deck to be searched.
     * @param word Word to search after.
     * @return A ist with all the cards where a match was found.
     */
    public static List<Card> searchCardText(Deck deck, String word) {

        CardTextSearcher searcher = new CardTextSearcher(deck, 3);

        return searcher.searchAfterWord(word);
    }

    public static void saveReviewsAsFile(User user, Collection<Deck> courses) {

        Path path = FileUtil.getUserReviewFile(user);

        try(CardReviewWriter writer = new CardReviewWriter(path)) {

            for(Deck deck : courses)
                writer.writeReviewToFile(deck);

        }catch(IOException e) {
            e.printStackTrace();
        }

    }

    public static void readReviewsFromFile(User user, Map<String, Deck> courses) throws IOException {

        Path path = FileUtil.getUserReviewFile(user);

        CardReviewReader reader = new CardReviewReader(path);

        for(Deck deck : courses.values())
            reader.addReviewsToDeck(deck);
    }
}

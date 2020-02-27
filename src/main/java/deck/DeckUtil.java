package deck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import database.CustomThreadFactory;
import deck.card.Card;
import deck.card.CardComponent;
import deck.card.component.CardComponents;
import deck.card.component.TextAreaComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
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

    private final static int THREAD_COUNT = 3;
    private final static ExecutorService searchThreads = Executors.newFixedThreadPool(THREAD_COUNT, new CustomThreadFactory("Search-thread"));

    private static CountDownLatch latch;
    private static String searchWord;
    private static Path deckPath;

    //Used for text searches.
    private static class SearchThread implements Runnable {

        private final List<Card> searchResultList;
        private final List<Card> subList;

        SearchThread(List<Card> subList, List<Card> searchResultList) {
            this.searchResultList = searchResultList;
            this.subList = subList;
        }

        @Override
        public void run() {

            //Searches in all cards' text-area components after the search word.
            for(Card card : subList) {
                logger.debug("Thread searching in card:" + card.getCardId());

                for(CardComponent component : card.getComponentList()) {

                    if(CardComponents.TEXT_AREA == component.getComponentEnum()) {

                        TextAreaComponent text = (TextAreaComponent) component;
                        int index = text.getRawObject().toLowerCase().indexOf(searchWord.toString().toLowerCase());

                        if(index != -1) {
                            searchResultList.add(card);
                            break;
                        }
                    }
                }
            }
            latch.countDown();
        }
    }

    /**
     * Searches in all cards' text area-components after the search word.
     * @param deck Deck to be searched.
     * @param word Word to search after.
     * @return A ist with all the cards where a match was found.
     */
    public static List<Card> searchCardText(Deck deck, String word) {

        logger.info("Method call:searchCardText Deck:" + deck.getCourseName() + " Word:" + word);

        List<Card> searchResultList = new ArrayList<>();
        searchWord = word.trim().toLowerCase();

        if(searchWord.length() == 0) {return Collections.emptyList();}

        latch = new CountDownLatch(THREAD_COUNT);

        //Gather information to distribute the search over all of the search threads.
        List list = deck.getImmutableList();
        int listSize = list.size();
        int listSizeDivided = listSize / THREAD_COUNT;
        int excess = listSize % THREAD_COUNT;
        int fromIndex = 0;
        int toIndex = listSizeDivided - 1;
        boolean excessLeft = false;

        //Even out the load for each thread and then submit to the executor service.
        if(excess > 0){excessLeft = true;}
        for(int i = 1; i < THREAD_COUNT + 1; i++) {

            if(excessLeft) {

                toIndex++;
                searchThreads.submit(new SearchThread(list.subList(fromIndex, toIndex + 1), searchResultList));
                excess--;
                fromIndex = toIndex + 1;
                toIndex += listSizeDivided;

                if(excess == 0){excessLeft = false;}
                continue;
            }

            searchThreads.submit(new SearchThread(list.subList(fromIndex, toIndex +1), searchResultList));
            fromIndex = toIndex + 1;
            toIndex += listSizeDivided;
        }


        //Wait for each thread to finish
        try{
            logger.debug("Waiting for search - 3000 MS");
            latch.await(3000, TimeUnit.MILLISECONDS);

        }catch(InterruptedException e) {

            logger.debug("InterruptedException occurred while waiting for search thread to finish." + e);
            e.printStackTrace();
        }

        return searchResultList;
    }


    public static void searchPicture(Deck deck) {
    }


    /**
     * Creates the default directory where the decks will be stored as files on the PC.
     * @throws IOException When the directory couldn't be created.
     */
    public static void createDefaultDirectory() throws IOException {

        logger.debug("Method call:createDefaultDirectory");

        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Learndeck\\decks";
        Files.createDirectories(Paths.get(path));
        deckPath = Paths.get(path);
    }


    /**
     * Creates the default directory where the decks will be stored as files on the PC.
     * @throws IOException When the directory couldn't be created.
     */
    public static void removeCourseFile(Deck deck) throws IOException {

        logger.info("Method exit:removeCourseFile Deck:" + deck.getCourseName());

        if(!deck.getIoStatus().equals(IOStatus.NEW)) {

            logger.debug("Not a new deck - Trying to delete file:" + deck.getCourseName() + " ID:" + deck.getCourseId());

            if((!Files.deleteIfExists(Paths.get(deckPath + "\\" + deck.getCourseName() + ".txt")) && Files.exists(deckPath)))
                throw new IOException("'Learndeck' directory couldn't be found");
        }
    }


    /**See if a certain course exists at the set deck path. Case-sensitive.*/
    public static boolean courseFileExists(String courseName) {
        return Files.exists(Paths.get(deckPath + "\\" + courseName + ".txt"));
    }


    /**
     * Reads a deck from a file, de-serializing it.
     * @param courseName Name of the file to be read.
     * @return Deck created from the file.
     * @throws IOException When trying to read file.
     */
    public static Deck fileToCourse(String courseName) throws IOException {

        logger.debug("Method call:fileToDeck CourseName:" + courseName);

        String json = new String(Files.readAllBytes(Paths.get(deckPath + "\\" +courseName + ".txt")));

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Deck.class, new DeckDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(json, Deck.class);
    }



    public static Path getCoursePath(String courseName) {

        logger.info("Method call:getCourseFile");

        return Paths.get(deckPath + "\\" + courseName + ".txt");
    }

    /**
     * Saves decks as files on the PC, serializing it. Decks with the IOStatus "unchanged" will not be saved.
     * @param decks Decks to be saved.
     * @return Number of decks that was saved.
     * @throws IOException When trying to write.
     */
    public static int saveCoursesToPC(Collection<Deck> decks) throws IOException {

        logger.info("Method call:saveCoursesToPC");

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Deck.class, new DeckSerializer());
        objectMapper.registerModule(module);

        int savedDecksCount = 0;

        for(Deck deck : decks) {

            if((deck.getIoStatus().equals(IOStatus.NEW) || deck.getIoStatus().equals(IOStatus.CHANGED)) && deck.getDeckSize() != 0) {

                logger.debug("Saving deck:" + deck.getCourseName() + " - ID:" + deck.getCourseId() + " to file.");
                Files.writeString(Paths.get(deckPath + "\\" +  deck.getCourseName() + ".txt"), objectMapper.writeValueAsString(deck));
                savedDecksCount++;
            }
        }

        return savedDecksCount;
    }

    /**
     * Change the path to directory where files will be read and created etc.
     * @param path Path to the new deck directory.
     */
    public static void setDeckDirectoryPath(Path path) {
        logger.info("Method call:setDeckDirectoryPath Path:" + path.toString());
        DeckUtil.deckPath = path;
    }

    public static Path getDeckPath() {
        return deckPath;
    }

}

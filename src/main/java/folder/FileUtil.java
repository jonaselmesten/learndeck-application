package folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import deck.Deck;
import deck.IOStatus;
import deck.serializer.DeckDeserializer;
import deck.serializer.DeckSerializer;
import menu.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Collection;

public class FileUtil {

    private final static Logger logger = LogManager.getLogger(FileUtil.class);
    private static Path deckFolderPath;


    /**
     * Creates the default directory where the decks will be stored as files on the PC.
     * @throws IOException When the directory couldn't be created.
     */
    public static void createDefaultDirectory() throws IOException {

        logger.debug("Method call:createDefaultDirectory");

        String path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "\\Learndeck\\decks";
        Files.createDirectories(Paths.get(path));
        deckFolderPath = Paths.get(path);
    }

    /**
     * Delete a course file.
     * @throws IOException When the file couldn't be deleted.
     */
    public static void removeCourseFile(Deck deck) throws IOException {

        if(!deck.getIoStatus().equals(IOStatus.NEW)) {

            logger.debug("Not a new deck - Trying to delete file:" + deck.getCourseName() + " ID:" + deck.getCourseId());

            if((!Files.deleteIfExists(Paths.get(deckFolderPath + "\\" + deck.getCourseName() + ".txt")) && Files.exists(deckFolderPath)))
                throw new IOException("'Learndeck' directory couldn't be found");
        }
    }

    /**See if a certain course exists at the set deck path. Case-sensitive.*/
    public static boolean courseFileExists(String courseName) {
        return Files.exists(Paths.get(deckFolderPath + "\\" + courseName + ".txt"));
    }

    /**
     * Reads a deck from a file, de-serializing it.
     * @param courseName Name of the file to be read.
     * @return Deck created from the file.
     * @throws IOException When trying to read file.
     */
    public static Deck fileToCourse(String courseName) throws IOException {

        logger.debug("Method call:fileToDeck CourseName:" + courseName);

        String json = new String(Files.readAllBytes(Paths.get(deckFolderPath + "\\" +courseName + ".txt")));

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Deck.class, new DeckDeserializer());
        mapper.registerModule(module);

        return mapper.readValue(json, Deck.class);
    }

    public static Path getCoursePath(String courseName) {

        logger.info("Method call:getCourseFile");

        return Paths.get(deckFolderPath + "\\" + courseName + ".txt");
    }

    /**
     * Saves decks as files on the PC, serializing it. Decks with the IOStatus "unchanged" will not be saved.
     * @param decks Decks to be saved.
     * @return Number of decks that was saved.
     * @throws IOException When trying to write.
     */
    public static int saveCoursesToPC(Collection<Deck> decks) throws IOException {

        logger.info("Method call:saveCoursesToPC");

        createDefaultDirectory();

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Deck.class, new DeckSerializer());
        objectMapper.registerModule(module);

        int savedDecksCount = 0;

        for(Deck deck : decks) {

            if((deck.getIoStatus().equals(IOStatus.NEW) || deck.getIoStatus().equals(IOStatus.CHANGED)) && deck.getDeckSize() != 0) {

                logger.debug("Saving deck:" + deck.getCourseName() + " - ID:" + deck.getCourseId() + " to file.");
                Files.writeString(Paths.get(deckFolderPath + "\\" +  deck.getCourseName() + ".txt"), objectMapper.writeValueAsString(deck));
                savedDecksCount++;
            }
        }

        return savedDecksCount;
    }

    public static Path getDeckFolderPath() {
        return deckFolderPath;
    }

    public static Instant getLastModificationDate(String courseName) throws IOException {

        return Files.getLastModifiedTime(Paths.get(deckFolderPath + "\\" + courseName + ".txt")).toInstant();
    }



    public static boolean hasStoredReviews(User user) {


        return true;
    }

    public static Path getUserReviewFile(User user) {




        return null;
    }
}

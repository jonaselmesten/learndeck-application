package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import database.CustomThreadFactory;
import deck.Deck;
import deck.DeckSerializer;
import deck.DeckUtil;
import deck.IOStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


//THIS CLASS IS JUST USED AS A "SERVER-DUMMY" AT THE MOMENT.
//Just for testing some logic.
public class ServerUtil {

    private final static int threadCount = 10;
    private final static Path deckDirectoryPath = Path.of("C:\\Users\\Anders\\IdeaProjects\\learndeck\\src\\main\\resources\\server");
    private final static ExecutorService executor = Executors.newFixedThreadPool(threadCount, new CustomThreadFactory("serverThread"));
    private final static AtomicInteger activeThreads = new AtomicInteger(0);

    public static void uploadDeck(Deck deck) {

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Deck.class, new DeckSerializer());
        objectMapper.registerModule(module);

        if(deck.getIoStatus().equals(IOStatus.NEW) || deck.getIoStatus().equals(IOStatus.CHANGED)) {

            executor.submit(() -> {

                try {
                    activeThreads.incrementAndGet();
                    Thread.sleep(new Random().nextInt(500));
                    Files.writeString(Paths.get(deckDirectoryPath + "\\" +  deck.getCourseName() + ".txt"), objectMapper.writeValueAsString(deck));

                }catch(IOException | InterruptedException e) {

                    //Add course name to a file for upload during next log in
                    //Code...
                    //Code...

                    e.printStackTrace();
                }
                activeThreads.decrementAndGet();
            });
        }
    }

    public static void downloadDeck(String courseName) {

        executor.submit(() -> {

            try {
                activeThreads.incrementAndGet();
                Thread.sleep(new Random().nextInt(500));
                Files.copy(Paths.get(deckDirectoryPath + "\\" + courseName + ".txt"), Paths.get(DeckUtil.getDeckPath() + "\\" + courseName + ".txt"));

            }catch(IOException | InterruptedException e) {

                //Add course name to a file for upload during next log in etc
                //Code...
                //Code...

                e.printStackTrace();
            }
            activeThreads.decrementAndGet();
        });
    }

    public static void removeCourseFile(Deck deck) {

        if(deck.getIoStatus().equals(IOStatus.NEW))
            return;

        try {
            Files.deleteIfExists(Paths.get(deckDirectoryPath.toString() + "\\" +  deck.getCourseName() + ".txt"));
        }catch(IOException e) { //Could not delete

            //Add course name to a file for deletion during next log in etc.
            //Code...
            //Code...

            e.printStackTrace();
        }
    }

    public static void waitForAllDownloads() throws InterruptedException {
        while(activeThreads.get() != 0);
    }
}

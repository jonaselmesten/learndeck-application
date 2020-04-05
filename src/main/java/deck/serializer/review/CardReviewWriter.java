package deck.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import deck.Deck;

import java.io.*;
import java.nio.file.Path;

public class CardReviewWriter implements Closeable {

    private final Path path;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleModule module = new SimpleModule();
    private final Writer fileWriter;

    public CardReviewWriter(Path path) throws IOException {

        this.path = path;
        module.addSerializer(Deck.class, new CardReviewSerializer());
        objectMapper.registerModule(module);

        fileWriter = new BufferedWriter(new FileWriter(path.toFile(), false));
    }

    public void writeReviewToFile(Deck deck) throws IOException {
        fileWriter.append(objectMapper.writeValueAsString(deck)).append(System.lineSeparator());
    }

    @Override
    public void close() throws IOException {
        fileWriter.close();
    }
}

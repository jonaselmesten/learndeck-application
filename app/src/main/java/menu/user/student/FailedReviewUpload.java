package menu.user.student;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FailedReviewUpload {

    private final Map<Integer, List<Integer>> pendingUploads = new TreeMap<>();
    private final Path path = Path.of("../LearnDeck/src/main/resources/conifg/failed");

    public FailedReviewUpload() {
        readFile();
    }

    private void readFile() {

        try {
            String json = Files.readString(path);

            if(json.isEmpty())
                return;

            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(json);

            while(parser.nextToken() != JsonToken.END_OBJECT) {

                int userId = 0;

                if(parser.getCurrentName() != null)
                    userId = Integer.parseInt(parser.getCurrentName());

                if(parser.getCurrentToken() == JsonToken.START_ARRAY) {

                    while(parser.nextToken() != JsonToken.END_ARRAY)
                        addFailedUpload(userId, parser.getIntValue());
                }
            }
            parser.close();

        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void addFailedUpload(int userId, int courseId) {

        if(Objects.isNull(pendingUploads.get(userId))) {

            List<Integer> list = new ArrayList<>();
            list.add(courseId);

            pendingUploads.put(userId, list);

        }else {

            if(!pendingUploads.get(userId).contains(courseId))
                pendingUploads.get(userId).add(courseId);
        }
    }

    public void removeUser(int userId) {
        pendingUploads.remove(userId);
    }

    public void removeCourse(int userId, int courseId) {

        List<Integer> list = pendingUploads.get(userId);

        if(!list.isEmpty()) {

            if(list.remove(Integer.valueOf(courseId)) && list.isEmpty()) {
                removeUser(userId);
            }
        }
    }

    public void save() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(pendingUploads);
        Files.writeString(path, json);
    }
}



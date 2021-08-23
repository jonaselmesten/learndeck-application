package card;

import android.content.Context;
import android.view.View;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exceptions.ResourceException;
import model.CardResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class Card {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private final long courseId;
    private final int dateModifier;
    private final Date nextReview;
    private final CardPart question;
    private final CardPart answer;
    private final ReviewInfo buttonStats;


    private Card(long courseId, Date nextReview, int dateModifier, CardPart question, CardPart answer, ReviewInfo buttonStats) {
        this.courseId = courseId;
        this.nextReview = nextReview;
        this.dateModifier = dateModifier;
        this.question = question;
        this.answer = answer;
        this.buttonStats = buttonStats;
    }

    /**
     * Creates a card from the CardResponse object.
     * @param card Response object.
     * @return Instance of card.
     * @throws IncorrectCardFormatException If something fails due to format error.
     */
    public static Card fromResponse(CardResponse card) throws IncorrectCardFormatException, ParseException, ResourceException {

        Date date = formatter.parse(card.getNextReview());
        ReviewInfo reviewInfo = new ReviewInfo(stringToArray(card.getButtonStats()));
        CardPart question = createPart(card.getQuestion(), CardComponent.from(card.getQuestionType()));
        CardPart answer = createPart(card.getAnswer(), CardComponent.from(card.getAnswerType()));

        return new Card(card.getCourseId(), date, card.getDateModifier(), question, answer, reviewInfo);
    }

    /**
     * Creates an instance of CardPart from JSON.
     * @param json JSON string.
     * @param component Card component.
     * @return Instance of CardPart.
     */
    private static CardPart createPart(String json, CardComponent component) throws CardComponentException, ResourceException {

        switch (component) {
            case TEXT:
                return new TextPart(json);
            case IMG_TEXT:
                return new ImgTextPart(json);
        }

        throw new CardComponentException("Non existing component:" + component.name());
    }

    /**
     * Creates an array from a JSON string of the button stats.
     * @param buttonStats JSON string with button stats.
     * @return Int array.
     * @throws IncorrectCardFormatException If the string couldn't be parsed to int[].
     */
    private static int[] stringToArray(String buttonStats) throws IncorrectCardFormatException {

        JsonElement jsonTree = JsonParser.parseString(buttonStats);

        if(jsonTree.isJsonObject()){
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            JsonElement hard = jsonObject.get("hard");
            JsonElement medium = jsonObject.get("medium");
            JsonElement easy = jsonObject.get("easy");
            JsonElement veryEasy = jsonObject.get("very_easy");

            return new int[]{hard.getAsInt(), medium.getAsInt(), easy.getAsInt(), veryEasy.getAsInt()};

        }else throw new IncorrectCardFormatException("Button stats string is malformed");
    }

    public View loadQuestionGui(Context context) {
        return question.convertToView(context);
    }

    public View loadAnswerGui(Context context) {
        return answer.convertToView(context);
    }

    @Override
    public String toString() {
        return "Card{" +
                "courseId=" + courseId +
                ", dateModifier=" + dateModifier +
                ", nextReview=" + nextReview +
                ", question=" + question +
                ", answer=" + answer +
                ", buttonStats=" + buttonStats +
                '}';
    }
}


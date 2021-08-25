package card;

import android.content.Context;
import android.view.View;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exceptions.CardComponentException;
import exceptions.IncorrectCardFormatException;
import exceptions.ResourceException;
import model.CardResponse;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;

public class Card implements Comparable<Card> {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private final long reviewId;
    private int dateModifier;
    private Date nextReview;
    private final CardPart question;
    private final CardPart answer;
    private final int[] buttonStats;


    private Card(long reviewId, Date nextReview, int dateModifier, CardPart question, CardPart answer, int[] buttonStats) {
        this.reviewId = reviewId;
        this.nextReview = nextReview;
        this.dateModifier = dateModifier;
        this.question = question;
        this.answer = answer;
        this.buttonStats = buttonStats;
    }

    /**
     * Calculates the next review date for this card.
     * @param pushedButton Difficulty button that was pushed.
     * @return Review object holding the new review data.
     */
    public Review updateReview(Difficulty pushedButton) {

        Instant date = nextReview.toInstant();

        if(Instant.now().isAfter(date))
            date = Instant.now();

        System.out.println("FIRST:" + date);

        System.out.println(date.toString());

        switch (pushedButton) {
            case HARD:
                dateModifier -= 30;
                date = Instant.now();
                buttonStats[0]++;
                break;
            case MEDIUM:
                dateModifier -= 5;
                date = date.plus(dateModifier, ChronoUnit.DAYS);
                buttonStats[1]++;
                break;
            case EASY:
                dateModifier += 2;
                date = date.plus(dateModifier, ChronoUnit.DAYS);
                buttonStats[2]++;
                break;
            case VERY_EASY:
                dateModifier +=4;
                date = date.plus(dateModifier, ChronoUnit.DAYS);
                buttonStats[3]++;
                break;
        }

        if(dateModifier < 0)
            dateModifier = 0;

        if(Instant.now().isAfter(date))
            date = Instant.now();

        nextReview = Date.from(date);

        System.out.println("AFTERT:" + date.toString().substring(0,10));

        return new Review(reviewId, buttonStats, dateModifier, date.toString().substring(0,10));
    }


    public boolean reviewToday() {
        return Instant.now().isAfter(nextReview.toInstant());
    }

    /**
     * Creates a card from the CardResponse object.
     * @param card Response object.
     * @return Instance of card.
     * @throws IncorrectCardFormatException If something fails due to format error.
     */
    public static Card fromResponse(CardResponse card) throws IncorrectCardFormatException, ParseException, ResourceException {

        Date date = formatter.parse(card.getNextReview());
        int[] buttonStats = stringToArray(card.getButtonStats());
        CardPart question = createPart(card.getQuestion(), CardComponent.from(card.getQuestionType()));
        CardPart answer = createPart(card.getAnswer(), CardComponent.from(card.getAnswerType()));

        return new Card(card.getReviewId(), date, card.getDateModifier(), question, answer, buttonStats);
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
                "reviewId=" + reviewId +
                ", dateModifier=" + dateModifier +
                ", nextReview=" + nextReview +
                ", question=" + question +
                ", answer=" + answer +
                ", buttonStats=" + Arrays.toString(buttonStats) +
                '}';
    }

    @Override
    public int compareTo(Card card) {
        return nextReview.compareTo(card.nextReview);
    }
}


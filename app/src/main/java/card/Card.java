package card;

import android.view.View;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.*;

/**<h1>Card</h1>
 *
 * Cards are created by it's builder class "CardBuilder". A card must consist of 3 card components of which one must be a separator component.
 * All the components are stored in a list.
 * All cards has a general difficulty which is modifier for when the next review date will be, the range is 2.0-0.1 - 2.0 Easy - 0.1 Hard.
 * <p>Cards are naturally sorted by their card ID's.</p>
 */
public class Card {

    private final ReviewInfo reviewInfo = new ReviewInfo();

    @SerializedName("reviewId")
    long courseId;
    @SerializedName("nextReview")
    String nextReview;
    @SerializedName("dateModifier")
    int dateModifier;
    @SerializedName("buttonStats")
    String buttonStats;
    @SerializedName("questionType")
    String questionType;
    @SerializedName("question")
    String question;
    @SerializedName("answerType")
    String answerType;
    @SerializedName("answer")
    String answer;

    public Card(long courseId, String nextReview, int dateModifier, String buttonStats, String questionType, String question, String answerType, String answer) {
        this.courseId = courseId;
        this.nextReview = nextReview;
        this.dateModifier = dateModifier;
        this.buttonStats = buttonStats;
        this.questionType = questionType;
        this.question = question;
        this.answerType = answerType;
        this.answer = answer;
    }

    public View loadQuestionGui() {
        return null;
    }

    public View loadAnswerGui() {
        return null;
    }

    public void setReviewStats(int[] reviewStats) {
        assert reviewStats.length == 4;
        reviewInfo.setPushCount(reviewStats);
    }

    public int[] getCardReviewStats() {
        return reviewInfo.getButtonValues();
    }

    public void setNextReview(CardButtons buttonPushed) {
    }

    @Override
    public String toString() {
        return "Card{" +
                "reviewInfo=" + reviewInfo +
                ", courseId=" + courseId +
                ", nextReview='" + nextReview + '\'' +
                ", dateModifier=" + dateModifier +
                ", buttonStats='" + buttonStats + '\'' +
                ", questionType='" + questionType + '\'' +
                ", question='" + question + '\'' +
                ", answerType='" + answerType + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}


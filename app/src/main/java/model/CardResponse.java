package model;

import com.google.gson.annotations.SerializedName;

public class CardResponse {

    @SerializedName("reviewId")
    private final long reviewId;
    @SerializedName("nextReview")
    private final String nextReview;
    @SerializedName("dateModifier")
    private final int dateModifier;
    @SerializedName("questionType")
    private final String questionType;
    @SerializedName("question")
    private final String question;
    @SerializedName("answerType")
    private final String answerType;
    @SerializedName("answer")
    private final String answer;
    @SerializedName("buttonStats")
    private final String buttonStats;

    public CardResponse(long reviewId,
                        String nextReview,
                        int dateModifier,
                        String buttonStats,
                        String questionType,
                        String question,
                        String answerType,
                        String answer) {

        this.reviewId = reviewId;
        this.nextReview = nextReview;
        this.dateModifier = dateModifier;
        this.buttonStats = buttonStats;
        this.questionType = questionType;
        this.question = question;
        this.answerType = answerType;
        this.answer = answer;
    }

    public String getButtonStats() {
        return buttonStats;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getAnswerType() {
        return answerType;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public int getDateModifier() {
        return dateModifier;
    }

    public long getReviewId() {
        return reviewId;
    }

    public String getNextReview() {
        return nextReview;
    }
}

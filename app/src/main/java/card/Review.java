package card;

import com.google.gson.JsonObject;

/**
 * <h1>Review</h1>
 * Class that holds all data relevant to card review updates.
 */
public class Review {

    private final long reviewId;
    private final int[] buttonStats;
    private final int dateModifier;
    private final String nextReview;

    Review(long reviewId, int[] buttonStats, int dateModifier, String nextReview) {
        this.reviewId = reviewId;
        this.buttonStats = buttonStats;
        this.dateModifier = dateModifier;
        this.nextReview = nextReview;
    }

    public String toJson() {

        JsonObject object = new JsonObject();

        String stats = "{hard:"
                + buttonStats[0] + ", medium:"
                + buttonStats[1] + ", easy:"
                + buttonStats[2] + ", very_easy:"
                + buttonStats[3] + "}";

        object.addProperty("id", reviewId);
        object.addProperty("buttonStats", stats);
        object.addProperty("dateModifier", dateModifier);
        object.addProperty("nextReview", nextReview);

        return object.toString();
    }

    public String getNextReview() {
        return nextReview;
    }
}
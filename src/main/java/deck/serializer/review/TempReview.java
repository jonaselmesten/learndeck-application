package deck.serializer;

class TempReview {

    private final String reviewDate;
    private final int[] reviewStats;

    TempReview(String reviewDate, int[] reviewStats) {
        this.reviewDate = reviewDate;
        this.reviewStats = reviewStats;
    }

    String getReviewDate() {
        return reviewDate;
    }

    int[] getReviewStats() {
        return reviewStats;
    }
}

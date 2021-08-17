package card;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.Arrays;

/**<h1>Review Info</h1>
 *
 * Used to hold the information about a students stats for a card.
 * It can then calculate & decided the next review-date for the card.
 * <br/>
 * The information it decides upon is the amount of times each button has been pushed for this card by the student and the general difficulty - which is based on all students performance.<br/>
 * Hard - Medium - Easy - Very easy
 * It stores the information in an array: [Hard, Medium, Easy, Very easy];
 */
class ReviewInfo {

    private final int[] buttonValues = {0,0,0,0};
    private int mediumPushCount = 0;
    private int easyPushCount = 0;

    /**
     * This method will increment one of the 4 elements in the array with 1.
     * <p>[H, M, E, VE]</p>
     * Then it will use the "calculateNextReview" method and calculate the new updated review date with
     * the updated information.
     * @param buttonPushed Button that was pushed.
     * @param currentNextReview The current review date.
     * @param generalDifficulty Difficulty of the card.
     * @return LocalDate The updated review date.
     */
    LocalDate incrementReviewInfo(CardButtons buttonPushed, LocalDate currentNextReview, double generalDifficulty) {

        switch(buttonPushed) {

            case HARD:
                buttonValues[0]++;
                break;
            case MEDIUM:
                buttonValues[1]++;
                break;
            case EASY:
                buttonValues[2]++;
                break;
            case VERY_EASY:
                buttonValues[3]++;
                break;
        }

        return calculateNextReview(buttonPushed ,currentNextReview, generalDifficulty);
    }


    void setPushCount(int[] buttonValues) throws IndexOutOfBoundsException {

        if(buttonValues.length != 4)
            throw new IndexOutOfBoundsException("This array must be exactly 4 in length.");

        this.buttonValues[0] = buttonValues[0];
        this.buttonValues[1] = buttonValues[1];
        this.buttonValues[2] = buttonValues[2];
        this.buttonValues[3] = buttonValues[3];
    }


    private long calculateDaysToAdd(double generalDifficulty) {

        long daysToAdd = (long) (Math.pow(0.2, buttonValues[0]) * Math.pow(0.8, buttonValues[1]) * Math.pow(1.3, buttonValues[2]) * Math.pow(1.8, buttonValues[3]) * generalDifficulty);

        if(daysToAdd > 180)
            daysToAdd = 180;

        return daysToAdd;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate calculateNextReview(CardButtons buttonPushed, LocalDate currentNextReview, double generalDifficulty) {

        LocalDate nextReview = currentNextReview;
        long daysToAdd = calculateDaysToAdd(generalDifficulty);

        //Turn un-reviewed cards date from 1111-11-11 (start date for new cards) to a "normal" date.
        if(currentNextReview.isEqual(LocalDate.of(1111,11,11)))
            nextReview = LocalDate.now();

        switch(buttonPushed) {

            case HARD:
                //Will never move forward as long as hard is pushed.
                nextReview = LocalDate.now();
                break;
            case MEDIUM:
                mediumPushCount++;

                //Set the review date to the next day if medium can be pushed more than 3 times without setting the date to the next day itself.
                if(mediumPushCount == 3) {
                    nextReview = LocalDate.now().plusDays(1);
                    break;
                }
                nextReview = nextReview.plusDays(daysToAdd);
                break;
            case EASY:
                easyPushCount++;

                //Set the review date to the next day if easy can be pushed more than 3 times without setting the date to the next day itself.
                if(easyPushCount == 2) {
                    nextReview = LocalDate.now().plusDays(1);
                    break;
                }
                nextReview = nextReview.plusDays(daysToAdd);
                break;
            case VERY_EASY:
                nextReview = nextReview.plusDays(daysToAdd);

                //Cards will always move forward at least 1 one day.
                if(LocalDate.now().plusDays(1).isAfter(nextReview))
                    nextReview = LocalDate.now().plusDays(1);

                break;
        }

        return nextReview;
    }

    public int[] getButtonValues() {
        return Arrays.copyOf(buttonValues, 4);
    }
}

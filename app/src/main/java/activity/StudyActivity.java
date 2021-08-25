package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import card.Card;
import card.Difficulty;
import card.Review;
import com.example.learndeck.R;
import deck.Deck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StudyActivity extends AppCompatActivity {

    private Deck deck;
    private Card currentCard;
    private Iterator<Card> iterator;
    private final List<Review> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        int courseId = getIntent().getExtras().getInt("courseId");
        deck = DeckActivity.getDeck(courseId);
        iterator = deck.getIterator();
        currentCard = iterator.next();

        if(!currentCard.reviewToday()) {
            stopStudy();
            return;
        }

        guiSetup();
    }

    @Override
    public void finish() {
        super.finish();
        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.removeAllViews();
        questionLayout.removeAllViews();
    }

    private void guiSetup() {

        ImageButton showAnswerButton = findViewById(R.id.showAnswerButton);
        showAnswerButton.setOnClickListener(view -> {
            LinearLayout buttonRow = findViewById(R.id.buttonRow);
            buttonRow.setVisibility(View.VISIBLE);
            LinearLayout answerLayout = findViewById(R.id.answerLayout);
            answerLayout.setVisibility(View.VISIBLE);
            showAnswerButton.setVisibility(View.INVISIBLE);
        });

        Button hardButton = findViewById(R.id.hardButton);
        hardButton.setOnClickListener(view -> {
            buttonPushed(Difficulty.HARD);
        });

        Button mediumButton = findViewById(R.id.mediumButton);
        mediumButton.setOnClickListener(view -> {
            buttonPushed(Difficulty.MEDIUM);
        });

        Button easyButton = findViewById(R.id.easyButton);
        easyButton.setOnClickListener(view -> {
            buttonPushed(Difficulty.EASY);
        });

        Button veryEasyButton = findViewById(R.id.veryEasyButton);
        veryEasyButton.setOnClickListener(view -> {
            buttonPushed(Difficulty.VERY_EASY);
        });

        //Load first review.
        View question = currentCard.loadQuestionGui(getApplicationContext());
        View answer = currentCard.loadAnswerGui(getApplicationContext());

        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.addView(answer);
        questionLayout.addView(question);

        answerLayout.setVisibility(View.GONE);
    }


    /**
     * Resets the GUI for a new card to review.
     * The button row is hidden and only the question part is loaded.
     */
    private void nextCard() {

        ImageButton showAnswerButton = findViewById(R.id.showAnswerButton);
        LinearLayout buttonRow = findViewById(R.id.buttonRow);
        showAnswerButton.setVisibility(View.VISIBLE);
        buttonRow.setVisibility(View.INVISIBLE);

        //Remove old question & answer.
        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.removeAllViews();
        questionLayout.removeAllViews();

        currentCard = iterator.next();

        if(!currentCard.reviewToday()) {
            stopStudy();
            return;
        }

        View question = currentCard.loadQuestionGui(getApplicationContext());
        View answer = currentCard.loadAnswerGui(getApplicationContext());

        questionLayout.addView(question);
        answerLayout.addView(answer);

        answerLayout.setVisibility(View.GONE);
    }

    /**
     * Removes the study view and states that there are not more reviews left.
     */
    private void stopStudy() {

        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.removeAllViews();
        questionLayout.removeAllViews();
        answerLayout.setVisibility(View.GONE);
        questionLayout.setVisibility(View.GONE);

        findViewById(R.id.separator).setVisibility(View.GONE);
        findViewById(R.id.showAnswerButton).setVisibility(View.GONE);

        TextView textView = new TextView(getApplicationContext());
        LinearLayout layout = findViewById(R.id.questionAnswerLayout);
        textView.setText(R.string.stopStudy);
        layout.addView(textView);
    }


    /**
     * Handles all logic for updating all the review data for the current card.
     * Will calculate the next review data given the current one.
     * @param pushedButton Button pushed by the user.
     */
    private void buttonPushed(Difficulty pushedButton) {

        Review review = currentCard.updateReview(pushedButton);
        reviewList.add(review);

        UiUtil.showToastMessage(getApplicationContext(), "Next review: " + review.getNextReview());
        nextCard();
    }

}
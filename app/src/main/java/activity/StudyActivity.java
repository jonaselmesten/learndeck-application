package activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import card.Card;
import com.example.learndeck.R;
import deck.Deck;

public class StudyActivity extends AppCompatActivity {

    private enum Buttons {HARD, MEDIUM, EASY, VERY_EASY}
    private Deck deck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        int courseId = getIntent().getExtras().getInt("courseId");

        deck = DeckActivity.getDeck(courseId);
        guiSetup(deck.getNextReview());
    }

    /**
     * Simply sets up the gui for study mode.
     * @param firstCard Card to load into gui.
     */
    private void guiSetup(Card firstCard) {

        ImageButton showAnswerButton = findViewById(R.id.imageButton);
        showAnswerButton.setOnClickListener(view -> {
            LinearLayout buttonRow = findViewById(R.id.buttonRow);
            buttonRow.setVisibility(View.VISIBLE);
            LinearLayout answerLayout = findViewById(R.id.answerLayout);
            answerLayout.setVisibility(View.VISIBLE);
            showAnswerButton.setVisibility(View.INVISIBLE);
        });

        Button hardButton = findViewById(R.id.hardButton);
        hardButton.setOnClickListener(view -> {
            buttonPushed(Buttons.HARD);
        });

        Button mediumButton = findViewById(R.id.mediumButton);
        mediumButton.setOnClickListener(view -> {
            buttonPushed(Buttons.MEDIUM);
        });

        Button easyButton = findViewById(R.id.easyButton);
        easyButton.setOnClickListener(view -> {
            buttonPushed(Buttons.EASY);
        });

        Button veryEasyButton = findViewById(R.id.veryEasyButton);
        veryEasyButton.setOnClickListener(view -> {
            buttonPushed(Buttons.VERY_EASY);
        });

        //Load first review.
        View question = firstCard.loadQuestionGui(getApplicationContext());
        View answer = firstCard.loadAnswerGui(getApplicationContext());

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

        ImageButton showAnswerButton = findViewById(R.id.imageButton);
        LinearLayout buttonRow = findViewById(R.id.buttonRow);
        showAnswerButton.setVisibility(View.VISIBLE);
        buttonRow.setVisibility(View.INVISIBLE);

        //Remove old question & answer.
        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.removeAllViews();
        questionLayout.removeAllViews();

        Card card = deck.getNextReview();

        View question = card.loadQuestionGui(getApplicationContext());
        View answer = card.loadAnswerGui(getApplicationContext());

        questionLayout.addView(question);
        answerLayout.addView(answer);

        answerLayout.setVisibility(View.GONE);
    }

    /**
     * Handles all logic for updating all the review data for the current card.
     * Will calculate the next review data given the current one.
     * @param button Button pushed by the user.
     */
    private void buttonPushed(Buttons button) {

        switch (button) {
            case HARD:
                break;
            case MEDIUM:
                break;
            case EASY:
                break;
            case VERY_EASY:
                break;
        }

        UiUtil.showToastMessage(getApplicationContext(), "Next review: xxxx-xx-xx");
        nextCard();
    }

}
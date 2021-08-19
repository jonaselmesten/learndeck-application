package activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import card.Card;
import com.example.learndeck.R;
import model.Deck;

import java.util.Random;

public class StudyActivity extends AppCompatActivity {

    private enum Buttons {HARD, MEDIUM, EASY, VERY_EASY}
    private Deck deck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        int courseId = getIntent().getExtras().getInt("courseId");

        deck = loadDeck(courseId);
        guiSetup(deck.getNextReview());
    }

    private Deck loadDeck(int courseId) {

        Deck deck = DeckActivity.getDeck(courseId);

        return deck;
    }

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
        View question = firstCard.loadQuestionGui();
        View answer = firstCard.loadAnswerGui();

        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.addView(question);
        questionLayout.addView(answer);

        answerLayout.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Cleans up the old card.
     * Then hides the answer part and makes the button row invisible.
     */
    private void nextCardGui() {

        ImageButton showAnswerButton = findViewById(R.id.imageButton);
        LinearLayout buttonRow = findViewById(R.id.buttonRow);
        LinearLayout questionAnswer = findViewById(R.id.questionAnswerLayout);
        showAnswerButton.setVisibility(View.VISIBLE);
        buttonRow.setVisibility(View.INVISIBLE);

        //Remove old question & answer.
        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.removeAllViews();
        questionLayout.removeAllViews();
        answerLayout.setVisibility(View.GONE);
    }

    /**
     * Resets the GUI for a new card to review.
     * The button row is hidden and only the question part is loaded.
     */
    private void nextCard() {

        nextCardGui();

        //Load next review.
        Card card = deck.getNextReview();

        View question = card.loadQuestionGui();
        View answer = card.loadAnswerGui();

        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.addView(answer);
        questionLayout.addView(question);
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

        showToast("Next review: xxxx-xx-xx");
        nextCard();
    }

}
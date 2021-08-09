package activity;

import android.app.ActionBar;
import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.learndeck.R;

import java.util.Random;

public class StudyActivity extends AppCompatActivity {

    private enum Buttons {HARD, MEDIUM, EASY, VERY_EASY}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        String courseId = getIntent().getExtras().getString("courseId");

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

        //Add next card.
        TextView question = new TextView(this);
        question.setText("QUESTION TEST");
        TextView answer = new TextView(this);
        answer.setText("ANSWER TEST");

        LinearLayout answerLayout = findViewById(R.id.answerLayout);
        LinearLayout questionLayout = findViewById(R.id.questionLayout);
        answerLayout.addView(question);
        questionLayout.addView(answer);
        answerLayout.setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    private void nextCard() {

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

        //Add next card.
        TextView question = new TextView(this);
        question.setText("QUESTION" + Integer.toString(new Random().nextInt(234234)));
        TextView answer = new TextView(this);
        answer.setText("AANSWER" + Integer.toString(new Random().nextInt(234234)));

        answerLayout.addView(answer);
        questionLayout.addView(question);
    }

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
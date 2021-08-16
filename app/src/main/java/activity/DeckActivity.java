package activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.learndeck.R;

import java.util.HashMap;
import java.util.Map;

public class DeckActivity extends AppCompatActivity {

    final static Map<Integer, LinearLayout> deckMap = new HashMap<>();
    static LinearLayout deckList;

    class DeckLine {

        private final String name;
        private final int cardCount;
        private final int dueCount;
        private int courseId;

        DeckLine(String name, int cardCount, int dueCount, int courseId) {
            this.name = name;
            this.cardCount = cardCount;
            this.dueCount = dueCount;
            this.courseId = courseId;
        }

        LinearLayout getLayout() {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    -1,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
            params.setMargins(30,10,30,0);

            //Texts.
            TextView deckName = new TextView(getApplicationContext());
            deckName.setText(name);
            deckName.setTypeface(deckName.getTypeface(), Typeface.BOLD);
            deckName.setTextSize(15);

            TextView dueCountText = new TextView(getApplicationContext());
            dueCountText.setText("Due:" + dueCount);
            dueCountText.setTextSize(15);

            LinearLayout layout = new LinearLayout(getApplicationContext());

            //Buttons.
            Button studyButton = new Button(getApplicationContext());
            studyButton.setText("Study");
            studyButton.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), StudyActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            });

            ImageButton optionButton = new ImageButton(getApplicationContext());
            optionButton.setImageResource(R.drawable.logo);
            optionButton.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            });

            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);

            dueCountText.setLayoutParams(params);
            dueCountText.setLayoutParams(params);

            layout.addView(deckName);
            layout.addView(dueCountText);
            layout.addView(studyButton);
            layout.addView(optionButton);

            deckMap.put(courseId, layout);

            return layout;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        deckList =findViewById(R.id.deckList);

        loadDecks();
    }

    private void loadDecks() {
        deckList.addView(new DeckLine("Japanese - Words", 7568,32, 1).getLayout());
        deckList.addView(new DeckLine("Japanese - Kanji", 2013,55, 2).getLayout());
        deckList.addView(new DeckLine("Japanese - Grammar", 1,26, 3).getLayout());
        deckList.addView(new DeckLine("Programming - Java", 15,3, 4).getLayout());
        deckList.addView(new DeckLine("Programming - Python", 55,44, 5).getLayout());
    }

}
package activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.learndeck.R;

public class DeckActivity extends AppCompatActivity {

    public class DeckLine {

        private final String name;
        private final int cardCount;
        private final int dueCount;

        DeckLine(String name, int cardCount, int dueCount) {
            this.name = name;
            this.cardCount = cardCount;
            this.dueCount = dueCount;
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

            //Buttons.
            Button studyButton = new Button(getApplicationContext());
            studyButton.setText("Study");
            studyButton.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), StudyActivity.class);
                intent.putExtra("courseId", name);
                startActivity(intent);
            });

            ImageButton optionButton = new ImageButton(getApplicationContext());
            optionButton.setImageResource(R.drawable.logo);
            optionButton.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                intent.putExtra("courseId", name);
                startActivity(intent);
            });

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);

            dueCountText.setLayoutParams(params);

            layout.addView(deckName);
            layout.addView(dueCountText);
            layout.addView(studyButton);
            layout.addView(optionButton);

            return layout;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deck);
        LinearLayout deckList = findViewById(R.id.deckList);

        //https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate/

        deckList.addView(new DeckLine("Japanese - Words", 7568,32).getLayout());
        deckList.addView(new DeckLine("Japanese - Kanji", 2013,55).getLayout());
        deckList.addView(new DeckLine("Japanese - Grammar", 1,26).getLayout());
        deckList.addView(new DeckLine("Programming - Java", 15,3).getLayout());
        deckList.addView(new DeckLine("Programming - Python", 55,44).getLayout());

    }
}
package activity;

import android.content.Intent;
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
            params.setMargins(30,0,30,0);

            //Texts.
            TextView deckName = new TextView(getApplicationContext());
            deckName.setText(name);

            TextView cardCount = new TextView(getApplicationContext());
            String cardString = "Cards:" + cardCount.toString();
            cardCount.setText("Cards: 343");

            TextView dueCount = new TextView(getApplicationContext());
            dueCount.setText("Due: 22");

            //Buttons.
            Button studyButton = new Button(getApplicationContext());
            studyButton.setText("Study");
            studyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), StudyActivity.class);
                    intent.putExtra("courseId", name);
                    startActivity(intent);
                }
            });

            ImageButton optionButton = new ImageButton(getApplicationContext());
            optionButton.setImageResource(R.drawable.logo);

            LinearLayout layout = new LinearLayout(getApplicationContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);

            deckName.setLayoutParams(params);
            cardCount.setLayoutParams(params);
            dueCount.setLayoutParams(params);

            layout.addView(deckName);
            layout.addView(cardCount);
            layout.addView(dueCount);
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

        deckList.addView(new DeckLine("Math ABCaaaaa", 1,2).getLayout());
        deckList.addView(new DeckLine("Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine( "Math ABC", 1,2).getLayout());

    }
}
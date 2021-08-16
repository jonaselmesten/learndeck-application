package activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.learndeck.R;
import connection.ConnectionException;
import connection.DeckDao;
import connection.UserDao;
import model.Deck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        UserDao userDao = new UserDao();
        DeckDao deckDao = new DeckDao();

        List<Deck> decks = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    decks.addAll(deckDao.getAllFromUser(1));

                    System.out.println("_:_::__:_:_:_:");
                    System.out.println("SIZE:" + decks.size());

                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        loadDecks(decks);
    }

    private void loadDecks(List<Deck> decks) {

        for(Deck deck : decks) {
            deckList.addView(new DeckLine(deck.getCourseName(), 7568,32, 1).getLayout());
            System.out.println("________________________________");
            System.out.println(deck.getCourseName());
        }

        deckList.addView(new DeckLine("Japanese - Words", 7568,32, 1).getLayout());
        deckList.addView(new DeckLine("Japanese - Kanji", 2013,55, 2).getLayout());
        deckList.addView(new DeckLine("Japanese - Grammar", 1,26, 3).getLayout());
        deckList.addView(new DeckLine("Programming - Java", 15,3, 4).getLayout());
        deckList.addView(new DeckLine("Programming - Python", 55,44, 5).getLayout());
    }

}
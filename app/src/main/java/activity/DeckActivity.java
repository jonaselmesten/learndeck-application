package activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.learndeck.R;
import connection.ConnectionException;
import connection.DeckDao;
import model.Deck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class DeckActivity extends AppCompatActivity {

    final static Map<Integer, LinearLayout> deckMap = new HashMap<>();
    final static ExecutorService executor = Executors.newSingleThreadExecutor();
    static LinearLayout deckList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        deckList =findViewById(R.id.deckList);

        loadDecks();
    }

    /**
     * Fetches all decks from the webservice and adds them to the GUI.
     */
    private void loadDecks() {

        DeckDao deckDao = new DeckDao();
        List<Deck> decks = new ArrayList<>();

        //Fetch all decks.
        executor.execute(() -> {
            try {
                decks.addAll(deckDao.getAllFromUser(1));
            } catch (ConnectionException e) {
                Toast toast = Toast.makeText(getApplicationContext(),"Connection error",Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        });

        //Wait for thread to finish before adding decks to GUI.
        try {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            for(Deck deck : decks)
                deckList.addView(createDeckLine(deck.getCourseName(), 7568, deck.getCourseId().intValue()));

        } catch (InterruptedException e) {
            Toast toast = Toast.makeText(getApplicationContext(),"Connection error",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        deckList.addView(createDeckLine("Japanese - Words", 7568, 1));
        deckList.addView(createDeckLine("Japanese - Words", 7568, 2));
        deckList.addView(createDeckLine("Japanese - Words", 7568, 3));
        deckList.addView(createDeckLine("Japanese - Words", 7568, 4));
        deckList.addView(createDeckLine("Japanese - Words", 7568, 5));
    }


    /**
     * Creates a GUI representation of a deck.
     * @param name Deck name.
     * @param dueCount Cards due for today.
     * @param courseId Course id.
     * @return LinearLayout with name, count and study & settings button.
     */
    private LinearLayout createDeckLine(String name, int dueCount, int courseId) {

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

        //Study button.
        Button studyButton = new Button(getApplicationContext());
        studyButton.setText("Study");
        studyButton.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), StudyActivity.class);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });

        // Settings button.
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
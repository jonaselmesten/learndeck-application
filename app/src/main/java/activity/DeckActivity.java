package activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import card.Card;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.learndeck.R;
import connection.CardConnection;
import connection.DeckConnection;
import deck.Deck;
import exceptions.ConnectionException;
import file.FileSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeckActivity extends AppCompatActivity {

    private final static ExecutorService executor = Executors.newSingleThreadExecutor();
    private final static Map<Integer, Deck> deckMap = new HashMap<Integer, Deck>();
    private final static Map<Integer, LinearLayout> deckGuiMap = new HashMap<>();

    private LinearLayout deckList;
    //TODO: Temporary.
    public static final int USER_ID = 1;


    public static Deck getDeck(int courseId) {
        Deck deck = deckMap.get(courseId);
        return deck;
    }

    public static LinearLayout getDeckLayout(int courseId) {
        return deckGuiMap.get(courseId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);
        deckList = findViewById(R.id.deckList);
        FileSystem.setDirectories(getApplicationContext());

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i("Start up", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("Start up", "Could not initialize Amplify", error);
        }

        //Fetch & load all relevant data for all the decks.
        executor.execute(this::loadDecks);
    }

    /**
     * Removes a deck line from the GUI.
     * @param layout Layout to remove.
     */
    public void removeDeckLine(LinearLayout layout) {
        deckList.removeView(layout);
    }


    /**
     * Fetches all decks from the webservice and adds them to the GUI.
     */
    private void loadDecks() {

        DeckConnection deckDao = new DeckConnection();

        //TODO: Fetch from disk first.
        //Fetch all decks.
        List<Deck> decks = null;
        try {
            decks = new ArrayList<>(deckDao.getAll());
        } catch (ConnectionException e) {
            UiUtil.showToastMessage(getApplicationContext(), "Couldn't get deck data.");
            e.printStackTrace();
            return;
        }

        boolean deckFillError = false;

        for(Deck deck : decks) {
            try {
                fillDeck(deck);
                addDeckToGUI(deck);
            } catch (IOException e) {
                deckFillError = true;
                e.printStackTrace();
            }
        }

        if(deckFillError)
            UiUtil.showToastMessage(getApplicationContext(), "Some decks couldn't be filled.");
    }

    /**
     * Fetches all the cards from the webservice.
     * @param deck Deck to fill.
     * @throws IOException If connection error occurs or if some cards have the wrong format.
     */
    private void fillDeck(Deck deck) throws IOException {

        CardConnection cardDao = new CardConnection();

        List<Card> cards = cardDao.getCards(deck.getCourseId().intValue());

        for(Card card : cards)
            deck.addCard(card);
    }


    private void addDeckToGUI(Deck deck) {
        runOnUiThread(() -> {
            deckList.addView(createDeckLine(
                    deck.getCourseName(),
                    deck.getDueCount(),
                    deck.getCourseId().intValue()));
        });
        deckMap.put(deck.getCourseId().intValue(), deck);
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

        deckGuiMap.put(courseId, layout);

        return layout;
    }

}
package activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.VolumeShaper;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.amplifyframework.core.async.AmplifyOperation;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.storage.StorageItem;
import com.amplifyframework.storage.options.StorageDownloadFileOptions;
import com.amplifyframework.storage.options.StorageGetUrlOptions;
import com.amplifyframework.storage.result.StorageGetUrlResult;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.example.learndeck.R;
import exceptions.ConnectionException;
import connection.DeckConnection;
import model.Deck;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class DeckActivity extends AppCompatActivity {

    final static Map<Integer, LinearLayout> deckGuiMap = new HashMap<>();
    private final static Map<Integer, Deck> deckMap = new HashMap<>();
    final static ExecutorService executor = Executors.newSingleThreadExecutor();
    public static final int USER_ID = 1;

    static LinearLayout deckList;

    public static Deck getDeck(int courseId) {

        Deck deck = deckMap.get(courseId);



        return deck;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        deckList =findViewById(R.id.deckList);

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("MyAmplifyApp", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error);
        }

        loadDecks();
    }

    private File downloadResource(String key) {





        return null;
    }


    /**
     * Fetches all decks from the webservice and adds them to the GUI.
     */
    private void loadDecks() {

        DeckConnection deckDao = new DeckConnection();
        List<Deck> decks = new ArrayList<>();

        //Fetch all decks.
        executor.execute(() -> {
            try {
                decks.addAll(deckDao.getAll());
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

            for(Deck deck : decks) {
                deckList.addView(createDeckLine(deck.getCourseName(), deck.getDueCount(), deck.getCourseId().intValue()));
                deckMap.put(deck.getCourseId().intValue(), deck);
            }

        } catch (InterruptedException e) {
            Toast toast = Toast.makeText(getApplicationContext(),"Connection error",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
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
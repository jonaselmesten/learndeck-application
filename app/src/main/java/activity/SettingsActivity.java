package activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.learndeck.R;
import connection.CardDao;
import connection.ConnectionException;
import connection.DeckDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static activity.DeckActivity.USER_ID;


public class SettingsActivity extends AppCompatActivity {

    final static ExecutorService executor = Executors.newSingleThreadExecutor();
    private enum Actions {DELETE_DECK, RESET_REVIEWS}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.removeDeckButton).setOnClickListener(view -> removeDeckButtonPushed());
        findViewById(R.id.resetViewsButton).setOnClickListener(view -> {resetReviewsButtonPushed();});
    }

    /**
     * Resets or removes a deck for the user.
     * @throws Exception If the update couldn't be executed.
     */
    private void executeDeckAction(Actions action) throws Exception {

        CardDao cardDao = new CardDao();
        DeckDao deckDao = new DeckDao();
        AtomicBoolean taskFailed = new AtomicBoolean(false);

        executor.execute(() -> {
            try {

                switch (action) {
                    case DELETE_DECK:
                        deckDao.deleteDeckFromUser(USER_ID);
                        break;
                    case RESET_REVIEWS:
                        cardDao.resetAllReviews(USER_ID);
                        break;
                }

            } catch (ConnectionException e) {
                taskFailed.set(true);
            }
        });

        //Wait for thread to finish.
        if (taskFailed.get() == false) {
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } else {
            throw new ConnectionException("Connection error.");
        }
    }

    private void resetReviewsButtonPushed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("All your study data will be removed. Are you sure?");

        //Set Yes and No actions.
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", (dialog, id) -> {

            try {
                executeDeckAction(Actions.RESET_REVIEWS);
                finish();

            } catch (Exception e) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Connection error - Couldn't reset.",
                        Toast.LENGTH_SHORT);
                toast.show();
                dialog.cancel();
            }

        }).setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void removeDeckButtonPushed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("You'll no longer be able to study this deck. Are you sure?");

        //Set Yes and No actions.
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", (dialog, id) -> {

            try {
                executeDeckAction(Actions.DELETE_DECK);

                //Remove the deck from the GUI in DeckActivity.
                int courseId = getIntent().getExtras().getInt("courseId");
                LinearLayout layout = DeckActivity.deckMap.get(courseId);
                DeckActivity.deckList.removeView(layout);

                finish();

            } catch (Exception e) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Connection error - Couldn't delete.",
                        Toast.LENGTH_SHORT);
                toast.show();
                dialog.cancel();
            }

        }).setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
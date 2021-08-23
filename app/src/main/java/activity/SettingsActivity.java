package activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.androidplot.pie.PieChart;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.PixelUtils;
import com.example.learndeck.R;
import connection.CardConnection;
import connection.DeckConnection;
import exceptions.ConnectionException;

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

        pieChartSetup(10,10,10,10);

        findViewById(R.id.removeDeckButton).setOnClickListener(view -> removeDeckButtonPushed());
        findViewById(R.id.resetViewsButton).setOnClickListener(view -> {resetReviewsButtonPushed();});
    }

    private void pieChartSetup(int hard, int medium, int easy, int veryEasy) {

        PieChart pie = findViewById(R.id.chart);

        Segment hardSeg = new Segment("Hard", hard);
        Segment mediumSeg = new Segment("Medium", medium);
        Segment easySeg = new Segment("Easy", easy);
        Segment veryEasySeg = new Segment("Very easy", veryEasy);

        final float padding = PixelUtils.dpToPix(30);
        pie.getPie().setPadding(padding, padding, padding, padding);

        SegmentFormatter formatHard = new SegmentFormatter(Color.RED);
        SegmentFormatter formatMedium = new SegmentFormatter(Color.YELLOW);
        SegmentFormatter formatEasy = new SegmentFormatter(Color.rgb(1,195,6));
        SegmentFormatter formatVeryEasy = new SegmentFormatter(Color.GREEN);

        pie.addSegment(hardSeg, formatHard);
        pie.addSegment(mediumSeg, formatMedium);
        pie.addSegment(easySeg, formatEasy);
        pie.addSegment(veryEasySeg, formatVeryEasy);

        pie.getBorderPaint().setColor(Color.TRANSPARENT);
        pie.getBackgroundPaint().setColor(Color.TRANSPARENT);
    }

    /**
     * Resets or removes a deck for the user.
     * @throws Exception If the update couldn't be executed.
     */
    private void executeDeckAction(Actions action) throws Exception {

        CardConnection cardDao = new CardConnection();
        DeckConnection deckDao = new DeckConnection();
        AtomicBoolean taskFailed = new AtomicBoolean(false);

        executor.execute(() -> {
            try {

                switch (action) {
                    case DELETE_DECK:
                        deckDao.deleteFromUser(USER_ID);
                        break;
                    case RESET_REVIEWS:
                        cardDao.resetReviews();
                        break;
                }

            } catch (ConnectionException e) {
                taskFailed.set(true);
            }
        });

        //Wait for thread to finish.
        if (!taskFailed.get()) {
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
                UiUtil.showToastMessage(getApplicationContext(), "Connection error - Couldn't reset.");
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
                LinearLayout layout = DeckActivity.getDeckLayout(courseId);
                DeckActivity activity = (DeckActivity) getParent();
                activity.removeDeckLine(layout);

                finish();

            } catch (Exception e) {
                UiUtil.showToastMessage(getApplicationContext(), "Connection error - Couldn't delete.");
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
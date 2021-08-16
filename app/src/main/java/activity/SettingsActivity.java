package activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.learndeck.R;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button removeDeckButton = findViewById(R.id.removeDeckButton);
        removeDeckButton.setOnClickListener(view -> {
            removeDeck();
        });

        Button resetReviewsButton = findViewById(R.id.resetViewsButton);
        resetReviewsButton.setOnClickListener(view -> {
            resetReviews();
        });
    }

    private void resetReviews() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("All your study data will be removed. Are you sure?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            finish();
                        })

                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void removeDeck() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("You'll no longer be able to study this deck. Are you sure?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            finish();
                        })

                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
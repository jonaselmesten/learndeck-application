package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DeckActivity extends AppCompatActivity {

    public static class DeckLine {

        private Context context;
        private final String name;
        private final int cardCount;
        private final int dueCount;

        DeckLine(Context context, String name, int cardCount, int dueCount) {
            this.context = context;
            this.name = name;
            this.cardCount = cardCount;
            this.dueCount = dueCount;
        }

        @SuppressLint("SetTextI18n")
        LinearLayout getLayout() {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    -1,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
            params.setMargins(30,0,30,0);

            TextView deckName = new TextView(context);
            deckName.setText(name);

            TextView cardCount = new TextView(context);
            String cardString = "Cards:" + cardCount.toString();
            cardCount.setText("Cards: 3434");

            TextView dueCount = new TextView(context);
            dueCount.setText("Due: 22");

            Button button = new Button(context);
            button.setText("Study");

            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(params);
            deckName.setLayoutParams(params);
            cardCount.setLayoutParams(params);
            dueCount.setLayoutParams(params);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.addView(deckName);
            layout.addView(cardCount);
            layout.addView(dueCount);
            layout.addView(button);

            return layout;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck);

        LinearLayout deckList = findViewById(R.id.deckList);

        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
        deckList.addView(new DeckLine(this, "Math ABC", 1,2).getLayout());
    }
}
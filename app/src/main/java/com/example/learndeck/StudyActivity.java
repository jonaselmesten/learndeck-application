package com.example.learndeck;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class StudyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        String test = getIntent().getExtras().getString("courseId");

        Toast.makeText(this, "New Skill level " + String.valueOf(test),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void finish() {

        //Runs when we press back-button.

        super.finish();
    }
}
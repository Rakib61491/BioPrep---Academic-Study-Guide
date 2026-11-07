package com.example.bioprep.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

/**
 * Dedicated Help & Support Activity implementing Requirement 12:
 * Covers About the app, Flashcards usage, Exam Practice MCQs, Study time & accuracy tracking,
 * Daily goals and streak rules, Personal notes, Resources & PDFs, FAQs, and Contact info.
 */
public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        ImageButton btnBack = findViewById(R.id.btnHelpBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}


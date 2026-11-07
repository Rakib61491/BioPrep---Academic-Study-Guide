package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;

/**
 * Exact activity class referenced in Lab 08 Topic 11:
 * "new Intent(this, AfterNotification.class)"
 * Opened when user taps the custom notification.
 */
public class AfterNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Notification opened (AfterNotification)", Toast.LENGTH_SHORT).show();

        // Redirect directly to the Stitch Daily Goal & Analytics screen
        Intent intent = new Intent(this, AnalyticsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

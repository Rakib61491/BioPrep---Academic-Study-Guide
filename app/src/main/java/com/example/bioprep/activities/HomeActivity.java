package com.example.bioprep.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bioprep.R;
import com.example.bioprep.data.SampleBiologyData;
import com.example.bioprep.receivers.AirplaneModeReceiver;
import com.example.bioprep.services.BackgroundAudioService;
import com.example.bioprep.services.NewService;
import com.example.bioprep.utils.NotificationHelper;

/**
 * Main Home Dashboard Activity implementing Stitch UI Design (15814936439009237240)
 * and demonstrating:
 * - Topic 2: Activity Lifecycle callbacks & Toast notifications
 * - Topic 3: Explicit and Implicit Intents, startService, sendBroadcast
 * - Topic 4: Click event handling (XML onClick, OnClickListener interface, anonymous listener)
 * - Topic 6: ConstraintLayout structure
 * - Topic 8: Background Services (NewService, MediaPlayer, startService, stopService)
 * - Topic 9: BroadcastReceiver registration for Airplane Mode changes
 * - Topic 11: NotificationCompat & NotificationChannel
 * - Topic 12: Runtime permission checks and requests
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_PAPER_ID = "extra_paper_id";
    private static final String TAG = "BIOPREP_LIFECYCLE";
    private static final int PERMISSION_REQUEST_CODE = 2001;

    private TextView textAfter;
    private AirplaneModeReceiver airplaneModeReceiver;
    private boolean isReceiverRegistered = false;

    // =========================================================================
    // Topic 1 & 2: onCreate and Activity Lifecycle callbacks
    // =========================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HomeActivity: onCreate");
        setContentView(R.layout.activity_home);

        // Topic 2: Toast notification showing callback
        Toast.makeText(getApplicationContext(), "HomeActivity: onCreate() called", Toast.LENGTH_SHORT).show();

        // Topic 11: Notification channel initialization
        NotificationHelper.createNotificationChannel(this);

        // Receiver instance for Topic 9
        airplaneModeReceiver = new AirplaneModeReceiver();

        initViewsAndListeners();
    }

    private void initViewsAndListeners() {
        // Top Left Small Back Button
        View btnHomeBack = findViewById(R.id.btnHomeBack);
        if (btnHomeBack != null) {
            btnHomeBack.setOnClickListener(v -> finish());
        }

        // Top Left Avatar -> Opens My Profile Screen
        View.OnClickListener profileOpener = v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        };
        View ivHeaderAvatar = findViewById(R.id.ivHeaderAvatar);
        if (ivHeaderAvatar != null) {
            ivHeaderAvatar.setOnClickListener(profileOpener);
        }
        View imgStudentAvatar = findViewById(R.id.imgStudentAvatar);
        if (imgStudentAvatar != null) {
            imgStudentAvatar.setOnClickListener(profileOpener);
        }

        // Top Right Goal Option -> Opens Daily Goal & Analytics Screen
        View.OnClickListener goalOpener = v -> {
            Intent intent = new Intent(getApplicationContext(), AnalyticsActivity.class);
            startActivity(intent);
        };
        View btnHeaderGoal = findViewById(R.id.btnHeaderGoal);
        if (btnHeaderGoal != null) {
            btnHeaderGoal.setOnClickListener(goalOpener);
        }
        View layoutGoalRing = findViewById(R.id.layoutGoalRing);
        if (layoutGoalRing != null) {
            layoutGoalRing.setOnClickListener(goalOpener);
        }

        // Topic 4: findViewById(R.id.text_after)
        textAfter = findViewById(R.id.text_after);

        // Topic 4: Interface click listener (setOnClickListener(this))
        Button btnInterfaceClick = findViewById(R.id.btnInterfaceClick);
        if (btnInterfaceClick != null) {
            btnInterfaceClick.setOnClickListener(this);
        }

        // Topic 4: Anonymous class OnClickListener (new View.OnClickListener() {...})
        View cardActiveRecall = findViewById(R.id.cardActiveRecall);
        if (cardActiveRecall != null) {
            cardActiveRecall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Topic 3: Explicit Intent
                    Intent intent = new Intent(getApplicationContext(), ActiveRecallActivity.class);
                    startActivity(intent);
                }
            });
        }

        Button btnStartRecall = findViewById(R.id.btnStartRecall);
        if (btnStartRecall != null) {
            btnStartRecall.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ActiveRecallActivity.class);
                startActivity(intent);
            });
        }

        // Card 2: Interactive Flashcards (Stitch Screen 6 & Lab Manual Experiments)
        View cardLabExperiments = findViewById(R.id.cardLabExperiments);
        if (cardLabExperiments != null) {
            cardLabExperiments.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), FlashcardsActivity.class);
                startActivity(intent);
            });
        }

        // Card 3: Notes Notebook (Topic 10: Content Provider CRUD)
        View cardNotes = findViewById(R.id.cardNotes);
        if (cardNotes != null) {
            cardNotes.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(intent);
            });
        }

        // Card 4: Bangladeshi Resources Library (Topic 3: Implicit Intents)
        View cardResources = findViewById(R.id.cardResources);
        if (cardResources != null) {
            cardResources.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ResourcesActivity.class);
                startActivity(intent);
            });
        }

        View btnSavedLibrary = findViewById(R.id.btnSavedLibrary);
        if (btnSavedLibrary != null) {
            btnSavedLibrary.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), SavedItemsActivity.class);
                startActivity(intent);
            });
        }

        // Horizontal Chapter Chips -> Opens ChapterActivity with selected chapter
        findViewById(R.id.chipChapter1).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
            intent.putExtra(EXTRA_PAPER_ID, SampleBiologyData.PAPER_BIOLOGY_1);
            intent.putExtra("EXTRA_CHAPTER_ID", "c1_p1");
            startActivity(intent);
        });
        findViewById(R.id.chipChapter2).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
            intent.putExtra(EXTRA_PAPER_ID, SampleBiologyData.PAPER_BIOLOGY_1);
            intent.putExtra("EXTRA_CHAPTER_ID", "c2_p1");
            startActivity(intent);
        });
        findViewById(R.id.chipChapter3).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
            intent.putExtra(EXTRA_PAPER_ID, SampleBiologyData.PAPER_BIOLOGY_2);
            intent.putExtra("EXTRA_CHAPTER_ID", "c1_p2");
            startActivity(intent);
        });
        findViewById(R.id.chipChapter4).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
            intent.putExtra(EXTRA_PAPER_ID, SampleBiologyData.PAPER_BIOLOGY_2);
            intent.putExtra("EXTRA_CHAPTER_ID", "c4_p2");
            startActivity(intent);
        });
        findViewById(R.id.btnViewAllChapters).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
            startActivity(intent);
        });

        // =========================================================================
        // Topic 8: Services in Android with Example
        // Demonstrates: findViewById(R.id.startButton), setOnClickListener(this),
        // new Intent(this, NewService.class), startService, stopService
        // =========================================================================
        Button startButton = findViewById(R.id.startButton);
        if (startButton != null) {
            startButton.setOnClickListener(this);
        }

        Button stopButton = findViewById(R.id.stopButton);
        if (stopButton != null) {
            stopButton.setOnClickListener(v -> {
                // Topic 8: stopService(new Intent(this, NewService.class))
                Intent serviceIntent = new Intent(HomeActivity.this, NewService.class);
                stopService(serviceIntent);
            });
        }

        // =========================================================================
        // Topic 11: Notification button (findViewById(R.id.btn))
        // Demonstrates: NotificationCompat.Builder, RemoteViews custom layout, NotificationManagerCompat
        // =========================================================================
        Button btnNotification = findViewById(R.id.btn);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                NotificationHelper.sendNotification(HomeActivity.this, true);
                Toast.makeText(this, "Notification posted (RemoteViews custom layout)!", Toast.LENGTH_SHORT).show();
            });
        }

        // =========================================================================
        // Topic 12: Request Permissions in Android Application
        // Demonstrates: ContextCompat.checkSelfPermission, ActivityCompat.requestPermissions,
        // setOnClickListener(v -> requestPermissions())
        // =========================================================================
        Button btnRequestPerm = findViewById(R.id.btnRequestPerm);
        if (btnRequestPerm != null) {
            btnRequestPerm.setOnClickListener(v -> requestApplicationPermissions());
        }

        // Bottom Navigation Bar items
        findViewById(R.id.navTabHome).setOnClickListener(v ->
                Toast.makeText(this, "Already on Home Dashboard", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navTabStudy).setOnClickListener(v ->
                startActivity(new Intent(this, ChapterActivity.class)));
        findViewById(R.id.navTabSaved).setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));
        findViewById(R.id.navTabProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }

    // =========================================================================
    // Topic 4: XML attribute android:onClick="sendMessage"
    // =========================================================================
    public void sendMessage(View view) {
        if (textAfter != null) {
            textAfter.setText("Handled via XML onClick attribute: sendMessage(View view)");
        }
        Toast.makeText(this, "sendMessage() invoked via XML onClick attribute", Toast.LENGTH_SHORT).show();
    }

    // =========================================================================
    // Topic 4 & 8: View.OnClickListener interface implementation
    // onClick(View view), view.getId(), new Intent(this, NewService.class)
    // =========================================================================
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnInterfaceClick) {
            if (textAfter != null) {
                textAfter.setText("Handled via View.OnClickListener interface: onClick(View view)");
            }
            Toast.makeText(this, "Clicked via View.OnClickListener (view.getId())", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.startButton) {
            // Topic 8: new Intent(this, NewService.class) and startService()
            Intent serviceIntent = new Intent(this, NewService.class);
            startService(serviceIntent);
        }
    }

    // =========================================================================
    // Topic 12: Request Permissions Implementation
    // checkSelfPermission(), requestPermissions(), onRequestPermissionsResult()
    // =========================================================================
    private void requestApplicationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE
                );
            } else {
                Toast.makeText(this, "POST_NOTIFICATIONS permission already granted!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Permissions automatically granted on this Android version.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission GRANTED!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission DENIED.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // =========================================================================
    // Topic 2: Activity Lifecycle Callbacks
    // =========================================================================
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "HomeActivity: onStart");
        Toast.makeText(getApplicationContext(), "HomeActivity: onStart()", Toast.LENGTH_SHORT).show();

        // Topic 9: Register Broadcast Receiver dynamically in onStart()
        if (!isReceiverRegistered && airplaneModeReceiver != null) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            registerReceiver(airplaneModeReceiver, filter);
            isReceiverRegistered = true;
            Log.d(TAG, "AirplaneModeReceiver registered.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HomeActivity: onResume");
        Toast.makeText(getApplicationContext(), "HomeActivity: onResume()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "HomeActivity: onPause");
        Toast.makeText(getApplicationContext(), "HomeActivity: onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "HomeActivity: onStop");
        Toast.makeText(getApplicationContext(), "HomeActivity: onStop()", Toast.LENGTH_SHORT).show();

        // Topic 9: Unregister Broadcast Receiver in onStop()
        if (isReceiverRegistered && airplaneModeReceiver != null) {
            try {
                unregisterReceiver(airplaneModeReceiver);
            } catch (Exception e) {
                Log.e(TAG, "Error unregistering receiver: " + e.getMessage());
            }
            isReceiverRegistered = false;
            Log.d(TAG, "AirplaneModeReceiver unregistered.");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "HomeActivity: onRestart");
        Toast.makeText(getApplicationContext(), "HomeActivity: onRestart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "HomeActivity: onDestroy. isFinishing() = " + isFinishing());
        Toast.makeText(getApplicationContext(), "HomeActivity: onDestroy()", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}

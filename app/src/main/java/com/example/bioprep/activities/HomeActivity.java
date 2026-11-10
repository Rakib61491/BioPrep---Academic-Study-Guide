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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bioprep.R;
import com.example.bioprep.data.SampleBiologyData;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.RecentActivityEntity;
import com.example.bioprep.receivers.AirplaneModeReceiver;
import com.example.bioprep.services.BackgroundAudioService;
import com.example.bioprep.services.NewService;
import com.example.bioprep.utils.NotificationHelper;
import com.example.bioprep.utils.RecentActivityHelper;
import com.example.bioprep.utils.UserPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import android.widget.ProgressBar;

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

        // Authentication guard: users cannot access main application without authenticating
        if (!com.example.bioprep.auth.FirebaseAuthRepository.getInstance(this).isUserSignedIn()) {
            Intent authIntent = new Intent(this, SignInActivity.class);
            authIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(authIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        // Topic 11: Notification channel initialization
        NotificationHelper.createNotificationChannel(this);

        // Receiver instance for Topic 9
        airplaneModeReceiver = new AirplaneModeReceiver();

        initViewsAndListeners();
    }

    private void initViewsAndListeners() {
        // Top Left Avatar -> Opens My Profile Screen
        View.OnClickListener profileOpener = v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        };
        View frameHomeAvatar = findViewById(R.id.frameHomeAvatar);
        if (frameHomeAvatar != null) {
            frameHomeAvatar.setOnClickListener(profileOpener);
        }
        View ivHeaderAvatar = findViewById(R.id.ivHeaderAvatar);
        if (ivHeaderAvatar != null) {
            ivHeaderAvatar.setOnClickListener(profileOpener);
        }

        // Top Right Goal Option -> Opens Daily Goal & Analytics Screen
        View.OnClickListener goalOpener = v -> {
            Intent intent = new Intent(getApplicationContext(), AnalyticsActivity.class);
            startActivity(intent);
        };
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

        // Card 1: Active Recall (Opens Chapter Selection with 12 Botany & 12 Zoology chapters)
        View cardActiveRecall = findViewById(R.id.cardActiveRecall);
        if (cardActiveRecall != null) {
            cardActiveRecall.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
                intent.putExtra(ChapterActivity.EXTRA_MODE, ChapterActivity.MODE_ACTIVE_RECALL);
                startActivity(intent);
            });
        }

        // Card 2: Interactive Flashcards (Opens Chapter Selection for Flashcards)
        View cardLabExperiments = findViewById(R.id.cardLabExperiments);
        if (cardLabExperiments != null) {
            cardLabExperiments.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
                intent.putExtra(ChapterActivity.EXTRA_MODE, ChapterActivity.MODE_FLASHCARDS);
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

        // Card 4: Exam Practice Card in Row 2
        View cardExamPractice = findViewById(R.id.cardExamPractice);
        if (cardExamPractice != null) {
            cardExamPractice.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ChapterActivity.class);
                startActivity(intent);
            });
        }

        // Card 5: Resources Library (Topic 3: Implicit Intents)
        View cardResources = findViewById(R.id.cardResources);
        if (cardResources != null) {
            cardResources.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ResourcesActivity.class);
                startActivity(intent);
            });
        }

        // Card 6: Bio Gallery (Zoology Specimens from iNaturalist)
        View cardBioGallery = findViewById(R.id.cardBioGallery);
        if (cardBioGallery != null) {
            cardBioGallery.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), BioGalleryActivity.class);
                startActivity(intent);
            });
        }

        // Daily Quiz Challenge: "Learn More" button
        View btnLearnMoreQuiz = findViewById(R.id.btnLearnMoreQuiz);
        if (btnLearnMoreQuiz != null) {
            btnLearnMoreQuiz.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_IS_DAILY_CHALLENGE, true);
                startActivity(intent);
            });
        }
        View bannerDailyQuiz = findViewById(R.id.bannerDailyQuiz);
        if (bannerDailyQuiz != null) {
            bannerDailyQuiz.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_IS_DAILY_CHALLENGE, true);
                startActivity(intent);
            });
        }

        // Recent Activity Card
        View cardRecentActivity = findViewById(R.id.cardRecentActivity);
        if (cardRecentActivity != null) {
            cardRecentActivity.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), ActiveRecallActivity.class);
                startActivity(intent);
            });
        }

        View btnSeeAllRecent = findViewById(R.id.btnSeeAllRecent);
        if (btnSeeAllRecent != null) {
            btnSeeAllRecent.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RecentActivitiesActivity.class);
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
        findViewById(R.id.navTabHome).setOnClickListener(v -> {});
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
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // =========================================================================
    // Topic 2: Activity Lifecycle Callbacks
    // =========================================================================
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "HomeActivity: onStart");

        // Topic 9: Register Broadcast Receiver dynamically in onStart()
        if (!isReceiverRegistered && airplaneModeReceiver != null) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.registerReceiver(this, airplaneModeReceiver, filter, ContextCompat.RECEIVER_EXPORTED);
            } else {
                registerReceiver(airplaneModeReceiver, filter);
            }
            isReceiverRegistered = true;
            Log.d(TAG, "AirplaneModeReceiver registered.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HomeActivity: onResume");

        if (!com.example.bioprep.auth.FirebaseAuthRepository.getInstance(this).isUserSignedIn()) {
            Intent authIntent = new Intent(this, SignInActivity.class);
            authIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(authIntent);
            finish();
            return;
        }

        updateUserAvatar();
        updateDailyGoalPercent();
        updateRecentActivity();
        NotificationHelper.showStudyStatsLockscreenNotification(this);
    }

    private void updateDailyGoalPercent() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            int todaySeconds = db.studySessionDao().getStudySecondsForDate(todayDate);
            int goalMinutes = UserPreferences.getDailyGoalMinutes(this);
            int goalSeconds = goalMinutes * 60;
            int dailyProgress = 0;
            if (goalSeconds > 0) {
                dailyProgress = Math.min(100, (int) Math.round((todaySeconds / (double) goalSeconds) * 100.0));
            }
            final int finalPercent = dailyProgress;
            runOnUiThread(() -> {
                TextView tvHeaderGoalPercent = findViewById(R.id.tvHeaderGoalPercent);
                if (tvHeaderGoalPercent != null) {
                    tvHeaderGoalPercent.setText(finalPercent + "%");
                }
            });
        });
    }

    private void updateRecentActivity() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            RecentActivityEntity recent = db.recentActivityDao().getMostRecent();
            runOnUiThread(() -> {
                View cardRecentActivity = findViewById(R.id.cardRecentActivity);
                TextView tvRecentTitle = findViewById(R.id.tvRecentTitle);
                TextView tvRecentTime = findViewById(R.id.tvRecentTime);
                TextView tvRecentIcon = findViewById(R.id.tvRecentIcon);
                ProgressBar pbRecentProgress = findViewById(R.id.pbRecentProgress);

                if (cardRecentActivity == null || tvRecentTitle == null) return;

                if (recent == null) {
                    tvRecentTitle.setText("Explore: Cell and its Structure");
                    if (tvRecentTime != null) tvRecentTime.setText("Tap to begin learning");
                    if (tvRecentIcon != null) tvRecentIcon.setText("🌱");
                    if (pbRecentProgress != null) pbRecentProgress.setProgress(0);
                    cardRecentActivity.setOnClickListener(v -> {
                        Intent intent = new Intent(HomeActivity.this, ChapterActivity.class);
                        startActivity(intent);
                    });
                    return;
                }

                String actionName;
                String icon;
                Class<?> targetClass;

                if ("ACTIVE_RECALL".equals(recent.getActivityType())) {
                    actionName = "Active Recall";
                    icon = "🧠";
                    targetClass = ActiveRecallActivity.class;
                } else if ("FLASHCARD".equals(recent.getActivityType())) {
                    actionName = "Flashcard";
                    icon = "🗂️";
                    targetClass = FlashcardsActivity.class;
                } else {
                    actionName = "Exam Practice";
                    icon = "📝";
                    targetClass = QuizActivity.class;
                }

                if (tvRecentIcon != null) tvRecentIcon.setText(icon);
                tvRecentTitle.setText(actionName + ": " + recent.getChapterTitle());

                if (tvRecentTime != null) {
                    tvRecentTime.setText(RecentActivityHelper.formatRelativeTime(recent.getTimestamp()));
                }

                if (pbRecentProgress != null) {
                    pbRecentProgress.setProgress(70);
                }

                cardRecentActivity.setOnClickListener(v -> {
                    Intent intent = new Intent(HomeActivity.this, targetClass);
                    intent.putExtra(ChapterActivity.EXTRA_CHAPTER_ID, recent.getChapterId());
                    intent.putExtra(ChapterActivity.EXTRA_CHAPTER_TITLE, recent.getChapterTitle());
                    intent.putExtra(ChapterActivity.EXTRA_PAPER_ID, recent.getPaperId());
                    startActivity(intent);
                });
            });
        });
    }

    private void updateUserAvatar() {
        try {
            String path = com.example.bioprep.utils.UserPreferences.getAvatarPath(this);
            android.widget.ImageView ivImg = findViewById(R.id.ivHomeAvatarImage);
            android.widget.TextView tvEmoji = findViewById(R.id.ivHeaderAvatar);
            if (ivImg != null) {
                if (path != null && !path.isEmpty() && new java.io.File(path).exists()) {
                    ivImg.setImageURI(android.net.Uri.fromFile(new java.io.File(path)));
                } else {
                    ivImg.setImageResource(R.drawable.app_logo);
                }
                ivImg.setVisibility(View.VISIBLE);
                if (tvEmoji != null) tvEmoji.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating avatar in Home: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "HomeActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "HomeActivity: onStop");

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
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "HomeActivity: onDestroy. isFinishing() = " + isFinishing());
        super.onDestroy();
    }
}

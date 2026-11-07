package com.example.bioprep.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.PracticeAnswerEntity;
import com.example.bioprep.database.PracticeAttemptEntity;
import com.example.bioprep.database.QuestionEntity;
import com.example.bioprep.database.StudySessionEntity;
import com.example.bioprep.receivers.TimerUpdateReceiver;
import com.example.bioprep.services.StudyTimerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Exam Practice Activity implementing Requirement 9 & 10:
 * - 20–25 randomly selected MCQs per chapter from Room DB.
 * - Questions appear in random order for each attempt.
 * - 4 options for each question appear in random order.
 * - 10-minute countdown timer with automatic submission on expiry.
 * - Option to submit manually at any time.
 * - Option selection and changing supported without answer revelation or toasts during test.
 * - No duplicate question list below.
 * - Elapsed study time persists in Room DB (StudySessionEntity).
 * - Saves attempt and answer details to Room DB and launches QuizResultActivity.
 */
public class QuizActivity extends AppCompatActivity implements TimerUpdateReceiver.TimerListener {

    public static final String EXTRA_PAPER_ID = "extra_paper_id";
    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
    public static final String EXTRA_CHAPTER_TITLE = "extra_chapter_title";

    public static final String EXTRA_TOTAL = "extra_total";
    public static final String EXTRA_CORRECT = "extra_correct";
    public static final String EXTRA_INCORRECT = "extra_incorrect";
    public static final String EXTRA_UNANSWERED = "extra_unanswered";
    public static final String EXTRA_PERCENTAGE = "extra_percentage";

    private static final String TAG = "BIOPREP_EXAM";
    private static final long EXAM_DURATION_MILLIS = 10 * 60 * 1000L; // 10 minutes

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private String paperId;
    private String chapterId;
    private String chapterTitle;

    // UI elements
    private TextView tvChapterTitle;
    private TextView tvProgress;
    private TextView tvTimerRemaining;
    private ProgressBar pbQuizProgress;
    private TextView tvProgressPercentText;

    private TextView tvQuizQuestion;
    private View btnOptionA, btnOptionB, btnOptionC, btnOptionD;
    private TextView tvOptionAText, tvOptionBText, tvOptionCText, tvOptionDText;
    private View ivCheckA, ivCheckB, ivCheckC, ivCheckD;
    private Button btnQuizSkip, btnQuizSubmit;

    // Backwards compatibility legacy hooks
    private RecyclerView rvQuestions;
    private TextView tvEmptyQuiz;
    private Button btnPrev, btnNext, btnSubmit;
    private Button btnTimerStart, btnTimerPause, btnTimerResume, btnTimerStop;

    // State
    private final List<ExamItem> examItems = new ArrayList<>();
    private int currentPosition = 0;
    private boolean isSubmitted = false;

    // Countdown Timer
    private CountDownTimer countDownTimer;
    private long remainingTimeMillis = EXAM_DURATION_MILLIS;

    // Bound Service for background timer compatibility
    private StudyTimerService timerService;
    private boolean isServiceBound = false;
    private TimerUpdateReceiver timerReceiver;
    private boolean isReceiverRegistered = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {});

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StudyTimerService.LocalBinder binder = (StudyTimerService.LocalBinder) service;
            timerService = binder.getService();
            isServiceBound = true;
            if (timerService != null) {
                timerService.startTimer(remainingTimeMillis);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
            isServiceBound = false;
        }
    };

    /**
     * Internal data structure representing an individual exam question
     * with randomized options and tracked student choice.
     */
    private static class ExamItem {
        final QuestionEntity entity;
        final List<String> shuffledOptions;
        final int correctOptionIndex;
        int selectedOption = -1; // -1 if not answered, 0..3

        ExamItem(QuestionEntity entity) {
            this.entity = entity;
            List<String> rawOptions = new ArrayList<>();
            rawOptions.add(entity.getOptionA() != null ? entity.getOptionA() : "");
            rawOptions.add(entity.getOptionB() != null ? entity.getOptionB() : "");
            rawOptions.add(entity.getOptionC() != null ? entity.getOptionC() : "");
            rawOptions.add(entity.getOptionD() != null ? entity.getOptionD() : "");

            int origCorrect = entity.getCorrectOptionIndex();
            if (origCorrect < 0 || origCorrect >= rawOptions.size()) {
                origCorrect = 0;
            }
            String correctString = rawOptions.get(origCorrect);

            List<String> shuffled = new ArrayList<>(rawOptions);
            Collections.shuffle(shuffled);
            this.shuffledOptions = shuffled;
            this.correctOptionIndex = Math.max(0, shuffled.indexOf(correctString));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        paperId = getIntent().getStringExtra(EXTRA_PAPER_ID);
        chapterId = getIntent().getStringExtra(EXTRA_CHAPTER_ID);
        chapterTitle = getIntent().getStringExtra(EXTRA_CHAPTER_TITLE);

        if (paperId == null) paperId = "botany";
        if (chapterId == null) chapterId = "bot_ch01";
        if (chapterTitle == null) chapterTitle = "Chapter 1: Cell and its Structure";

        initViews();
        loadQuestionsAndStartExam();
    }

    private void initViews() {
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvProgress = findViewById(R.id.tvProgress);
        tvTimerRemaining = findViewById(R.id.tvTimerRemaining);
        pbQuizProgress = findViewById(R.id.pbQuizProgress);
        tvProgressPercentText = findViewById(R.id.tvProgressPercentText);

        tvQuizQuestion = findViewById(R.id.tvQuizQuestion);
        btnOptionA = findViewById(R.id.btnOptionA);
        btnOptionB = findViewById(R.id.btnOptionB);
        btnOptionC = findViewById(R.id.btnOptionC);
        btnOptionD = findViewById(R.id.btnOptionD);

        tvOptionAText = findViewById(R.id.tvOptionAText);
        tvOptionBText = findViewById(R.id.tvOptionBText);
        tvOptionCText = findViewById(R.id.tvOptionCText);
        tvOptionDText = findViewById(R.id.tvOptionDText);

        ivCheckA = findViewById(R.id.ivCheckA);
        ivCheckB = findViewById(R.id.ivCheckB);
        ivCheckC = findViewById(R.id.ivCheckC);
        ivCheckD = findViewById(R.id.ivCheckD);

        btnQuizSkip = findViewById(R.id.btnQuizSkip);
        btnQuizSubmit = findViewById(R.id.btnQuizSubmit);

        // Hidden backwards compatibility views
        rvQuestions = findViewById(R.id.rvQuestions);
        if (rvQuestions != null) rvQuestions.setVisibility(View.GONE);
        tvEmptyQuiz = findViewById(R.id.tvEmptyQuiz);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnTimerStart = findViewById(R.id.btnTimerStart);
        btnTimerPause = findViewById(R.id.btnTimerPause);
        btnTimerResume = findViewById(R.id.btnTimerResume);
        btnTimerStop = findViewById(R.id.btnTimerStop);

        if (tvChapterTitle != null) {
            tvChapterTitle.setText(chapterTitle);
        }

        View btnBack = findViewById(R.id.btnQuizBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> confirmExitExam());
        }

        View btnBookmark = findViewById(R.id.btnQuizBookmark);
        if (btnBookmark != null) {
            btnBookmark.setOnClickListener(v -> startActivity(new Intent(this, SavedItemsActivity.class)));
        }

        setupOptionClicks();

        if (btnQuizSkip != null) {
            btnQuizSkip.setOnClickListener(v -> {
                if (examItems.isEmpty()) return;
                if (currentPosition < examItems.size() - 1) {
                    currentPosition++;
                    displayQuestion(currentPosition);
                } else {
                    promptSubmitExam();
                }
            });
        }

        if (btnQuizSubmit != null) {
            btnQuizSubmit.setOnClickListener(v -> {
                if (examItems.isEmpty()) return;
                if (currentPosition < examItems.size() - 1) {
                    currentPosition++;
                    displayQuestion(currentPosition);
                } else {
                    promptSubmitExam();
                }
            });
        }

        setupNavigation();
    }

    private void setupOptionClicks() {
        if (btnOptionA != null) btnOptionA.setOnClickListener(v -> selectOption(0));
        if (btnOptionB != null) btnOptionB.setOnClickListener(v -> selectOption(1));
        if (btnOptionC != null) btnOptionC.setOnClickListener(v -> selectOption(2));
        if (btnOptionD != null) btnOptionD.setOnClickListener(v -> selectOption(3));
    }

    private void selectOption(int index) {
        if (examItems.isEmpty() || currentPosition >= examItems.size()) return;
        ExamItem item = examItems.get(currentPosition);
        item.selectedOption = index;
        highlightSelectedOption(index);
    }

    private void resetOptionViews() {
        if (btnOptionA != null) btnOptionA.setBackgroundResource(R.drawable.bg_btn_option_unselected);
        if (btnOptionB != null) btnOptionB.setBackgroundResource(R.drawable.bg_btn_option_unselected);
        if (btnOptionC != null) btnOptionC.setBackgroundResource(R.drawable.bg_btn_option_unselected);
        if (btnOptionD != null) btnOptionD.setBackgroundResource(R.drawable.bg_btn_option_unselected);

        if (ivCheckA != null) ivCheckA.setVisibility(View.GONE);
        if (ivCheckB != null) ivCheckB.setVisibility(View.GONE);
        if (ivCheckC != null) ivCheckC.setVisibility(View.GONE);
        if (ivCheckD != null) ivCheckD.setVisibility(View.GONE);
    }

    private void highlightSelectedOption(int index) {
        resetOptionViews();
        if (index == 0 && btnOptionA != null) {
            btnOptionA.setBackgroundResource(R.drawable.bg_btn_option_selected);
            if (ivCheckA != null) ivCheckA.setVisibility(View.VISIBLE);
        } else if (index == 1 && btnOptionB != null) {
            btnOptionB.setBackgroundResource(R.drawable.bg_btn_option_selected);
            if (ivCheckB != null) ivCheckB.setVisibility(View.VISIBLE);
        } else if (index == 2 && btnOptionC != null) {
            btnOptionC.setBackgroundResource(R.drawable.bg_btn_option_selected);
            if (ivCheckC != null) ivCheckC.setVisibility(View.VISIBLE);
        } else if (index == 3 && btnOptionD != null) {
            btnOptionD.setBackgroundResource(R.drawable.bg_btn_option_selected);
            if (ivCheckD != null) ivCheckD.setVisibility(View.VISIBLE);
        }
    }

    private void loadQuestionsAndStartExam() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<QuestionEntity> pool = db.questionDao().getQuestionsByChapter(chapterId);

            // If empty (e.g. mock exam or ID mismatch), try finding questions
            if (pool == null || pool.isEmpty()) {
                pool = db.questionDao().getAllQuestions();
            }

            List<ExamItem> items = new ArrayList<>();
            if (pool != null && !pool.isEmpty()) {
                List<QuestionEntity> shuffledPool = new ArrayList<>(pool);
                Collections.shuffle(shuffledPool);

                // Select 20 to 25 random questions
                int targetCount = Math.min(25, Math.max(20, Math.min(25, shuffledPool.size())));
                if (targetCount > shuffledPool.size()) {
                    targetCount = shuffledPool.size();
                }

                for (int i = 0; i < targetCount; i++) {
                    items.add(new ExamItem(shuffledPool.get(i)));
                }
            }

            mainHandler.post(() -> {
                if (items.isEmpty()) {
                    Toast.makeText(this, "No exam questions available.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                examItems.clear();
                examItems.addAll(items);
                currentPosition = 0;
                displayQuestion(0);
                startExamTimer(EXAM_DURATION_MILLIS);
            });
        });
    }

    private void displayQuestion(int index) {
        if (examItems.isEmpty() || index < 0 || index >= examItems.size()) return;
        ExamItem item = examItems.get(index);

        if (tvProgress != null) {
            tvProgress.setText(String.format(Locale.getDefault(), "Question %d of %d", (index + 1), examItems.size()));
        }

        int percent = (int) (((index + 1) / (float) examItems.size()) * 100);
        if (pbQuizProgress != null) pbQuizProgress.setProgress(percent);
        if (tvProgressPercentText != null) tvProgressPercentText.setText(percent + "%");

        if (tvQuizQuestion != null) {
            tvQuizQuestion.setText(item.entity.getQuestionText());
        }

        List<String> options = item.shuffledOptions;
        if (options.size() > 0 && tvOptionAText != null) tvOptionAText.setText("A)  " + options.get(0));
        if (options.size() > 1 && tvOptionBText != null) tvOptionBText.setText("B)  " + options.get(1));
        if (options.size() > 2 && tvOptionCText != null) tvOptionCText.setText("C)  " + options.get(2));
        if (options.size() > 3 && tvOptionDText != null) tvOptionDText.setText("D)  " + options.get(3));

        if (item.selectedOption != -1) {
            highlightSelectedOption(item.selectedOption);
        } else {
            resetOptionViews();
        }

        if (btnQuizSubmit != null) {
            if (index == examItems.size() - 1) {
                btnQuizSubmit.setText("Finish Exam");
            } else {
                btnQuizSubmit.setText("Submit & Next");
            }
        }
    }

    private void startExamTimer(long durationMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        remainingTimeMillis = durationMillis;

        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                updateTimerUI(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                remainingTimeMillis = 0;
                updateTimerUI(0);
                Toast.makeText(QuizActivity.this, "Time's up! Automatically submitting exam...", Toast.LENGTH_SHORT).show();
                performExamSubmission();
            }
        };
        countDownTimer.start();
    }

    private void updateTimerUI(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        if (tvTimerRemaining != null) {
            tvTimerRemaining.setText(String.format(Locale.getDefault(), "Time Left: %02d:%02d", minutes, seconds));
        }
    }

    private void promptSubmitExam() {
        int unanswered = 0;
        for (ExamItem item : examItems) {
            if (item.selectedOption == -1) {
                unanswered++;
            }
        }

        if (unanswered > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Submit Exam?")
                    .setMessage(String.format(Locale.getDefault(), "You have %d unanswered question%s. Are you sure you want to submit now?", unanswered, unanswered > 1 ? "s" : ""))
                    .setPositiveButton("Submit Anyway", (dialog, which) -> performExamSubmission())
                    .setNegativeButton("Keep Reviewing", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Complete Exam?")
                    .setMessage("You have answered all questions. Submit and view your performance results?")
                    .setPositiveButton("Submit", (dialog, which) -> performExamSubmission())
                    .setNegativeButton("Review", null)
                    .show();
        }
    }

    private void performExamSubmission() {
        if (isSubmitted) return;
        isSubmitted = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (isServiceBound && timerService != null) {
            timerService.stopTimer();
        }

        int total = examItems.size();
        int correct = 0;
        int incorrect = 0;
        int unanswered = 0;

        for (ExamItem item : examItems) {
            if (item.selectedOption == -1) {
                unanswered++;
            } else if (item.selectedOption == item.correctOptionIndex) {
                correct++;
            } else {
                incorrect++;
            }
        }

        int accuracy = total > 0 ? (int) Math.round((correct / (double) total) * 100.0) : 0;
        int elapsedSeconds = (int) Math.max(1L, (EXAM_DURATION_MILLIS - remainingTimeMillis) / 1000L);

        final int finalCorrect = correct;
        final int finalIncorrect = incorrect;
        final int finalUnanswered = unanswered;

        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);

            // 1. Record study session for time tracking and analytics
            StudySessionEntity session = new StudySessionEntity(
                    "EXAM",
                    chapterId,
                    elapsedSeconds,
                    System.currentTimeMillis()
            );
            db.studySessionDao().insert(session);

            // 2. Record Practice Attempt
            PracticeAttemptEntity attempt = new PracticeAttemptEntity(
                    chapterId,
                    paperId,
                    chapterTitle,
                    total,
                    finalCorrect,
                    finalIncorrect,
                    finalUnanswered,
                    accuracy,
                    elapsedSeconds,
                    System.currentTimeMillis()
            );
            long attemptId = db.practiceDao().insertAttempt(attempt);

            // 3. Record individual Practice Answers for results review
            List<PracticeAnswerEntity> answerEntities = new ArrayList<>();
            for (ExamItem item : examItems) {
                List<String> opts = item.shuffledOptions;
                answerEntities.add(new PracticeAnswerEntity(
                        attemptId,
                        item.entity.getId(),
                        item.entity.getQuestionText(),
                        opts.size() > 0 ? opts.get(0) : "",
                        opts.size() > 1 ? opts.get(1) : "",
                        opts.size() > 2 ? opts.get(2) : "",
                        opts.size() > 3 ? opts.get(3) : "",
                        item.selectedOption,
                        item.correctOptionIndex,
                        item.selectedOption == item.correctOptionIndex,
                        item.entity.getExplanation()
                ));
            }
            db.practiceDao().insertAnswers(answerEntities);

            mainHandler.post(() -> {
                Intent resultIntent = new Intent(QuizActivity.this, QuizResultActivity.class);
                resultIntent.putExtra(QuizResultActivity.EXTRA_ATTEMPT_ID, attemptId);
                startActivity(resultIntent);
                finish();
            });
        });
    }

    private void confirmExitExam() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Exam Practice?")
                .setMessage("Your current progress will not be saved if you exit without submitting.")
                .setPositiveButton("Exit Exam", (dialog, which) -> finish())
                .setNegativeButton("Continue", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        confirmExitExam();
    }

    // TimerUpdateReceiver.TimerListener callbacks for compatibility
    @Override
    public void onTimerUpdate(long remainingMillis) {}

    @Override
    public void onTimerFinished() {}

    @Override
    protected void onStart() {
        super.onStart();
        if (!isReceiverRegistered) {
            timerReceiver = new TimerUpdateReceiver(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(StudyTimerService.ACTION_TIMER_UPDATE);
            filter.addAction(StudyTimerService.ACTION_TIMER_FINISHED);
            ContextCompat.registerReceiver(this, timerReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
            isReceiverRegistered = true;
        }

        Intent serviceIntent = new Intent(this, StudyTimerService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isServiceBound) {
            try {
                unbindService(serviceConnection);
            } catch (Exception e) {
                Log.e(TAG, "Error unbinding service: " + e.getMessage());
            }
            isServiceBound = false;
            timerService = null;
        }

        if (isReceiverRegistered && timerReceiver != null) {
            timerReceiver.clearListener();
            unregisterReceiver(timerReceiver);
            isReceiverRegistered = false;
            timerReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    private void setupNavigation() {
        View navHome = findViewById(R.id.navTabHome);
        if (navHome != null) navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        View navStudy = findViewById(R.id.navTabStudy);
        if (navStudy != null) navStudy.setOnClickListener(v ->
                startActivity(new Intent(this, ChapterActivity.class)));
        View navSaved = findViewById(R.id.navTabSaved);
        if (navSaved != null) navSaved.setOnClickListener(v ->
                startActivity(new Intent(this, SavedItemsActivity.class)));
        View navProfile = findViewById(R.id.navTabProfile);
        if (navProfile != null) navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));
    }
}

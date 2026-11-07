package com.example.bioprep.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.bioprep.adapters.QuestionAdapter;
import com.example.bioprep.data.SampleBiologyData;
import com.example.bioprep.models.Question;
import com.example.bioprep.models.QuizResult;
import com.example.bioprep.receivers.TimerUpdateReceiver;
import com.example.bioprep.services.StudyTimerService;
import com.example.bioprep.utils.QuizScorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Quiz activity displaying MCQs using RecyclerView and controlling study timer via Bound Service.
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

    private static final String TAG = "BIOPREP_LIFECYCLE";
    private static final long DEFAULT_TIMER_DURATION_MS = 60000L; // 60 seconds demonstration

    private static final String KEY_ANSWER_KEYS = "saved_answer_keys";
    private static final String KEY_ANSWER_VALUES = "saved_answer_values";
    private static final String KEY_CURRENT_POS = "saved_current_pos";
    private static final String KEY_TIMER_REMAINING = "saved_timer_remaining";
    private static final String KEY_TIMER_RUNNING = "saved_timer_running";

    // Intent extras
    private String paperId;
    private String chapterId;
    private String chapterTitle;

    // UI elements
    private TextView tvChapterTitle;
    private TextView tvProgress;
    private TextView tvTimerRemaining;
    private Button btnTimerStart;
    private Button btnTimerPause;
    private Button btnTimerResume;
    private Button btnTimerStop;
    private RecyclerView rvQuestions;
    private TextView tvEmptyQuiz;
    private Button btnPrev;
    private Button btnNext;
    private Button btnSubmit;

    // Quiz state
    private List<Question> questions = new ArrayList<>();
    private final Map<String, Integer> selectedAnswers = new HashMap<>();
    private int currentPosition = 0;
    private LinearLayoutManager layoutManager;
    private QuestionAdapter questionAdapter;
    private boolean isSubmitted = false;

    // Timer service & receiver state
    private StudyTimerService timerService;
    private boolean isServiceBound = false;
    private TimerUpdateReceiver timerReceiver;
    private boolean isReceiverRegistered = false;
    private long savedRemainingTimerMillis = DEFAULT_TIMER_DURATION_MS;
    private boolean timerWasRunning = false;

    // Runtime permission launcher for Android 13+
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, R.string.perm_granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.perm_denied, Toast.LENGTH_SHORT).show();
                }
            });

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StudyTimerService.LocalBinder binder = (StudyTimerService.LocalBinder) service;
            timerService = binder.getService();
            isServiceBound = true;
            Log.d(TAG, "QuizActivity: ServiceConnection onServiceConnected");

            // Sync UI with current service state
            if (timerService.isRunning()) {
                updateTimerUI(timerService.getRemainingTimeMillis());
                setTimerButtonsRunning();
            } else if (timerWasRunning && savedRemainingTimerMillis > 0) {
                // Resume from saved remaining duration
                timerService.startTimer(savedRemainingTimerMillis);
                setTimerButtonsRunning();
            } else if (timerService.getRemainingTimeMillis() > 0) {
                updateTimerUI(timerService.getRemainingTimeMillis());
                setTimerButtonsPaused();
            } else {
                setTimerButtonsStopped();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
            isServiceBound = false;
            Log.d(TAG, "QuizActivity: ServiceConnection onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "QuizActivity: onCreate");
        setContentView(R.layout.activity_quiz);

        // Read extras
        paperId = getIntent().getStringExtra(EXTRA_PAPER_ID);
        chapterId = getIntent().getStringExtra(EXTRA_CHAPTER_ID);
        chapterTitle = getIntent().getStringExtra(EXTRA_CHAPTER_TITLE);

        if (paperId == null) paperId = SampleBiologyData.PAPER_BIOLOGY_1;
        if (chapterId == null) chapterId = "c1_p1";
        if (chapterTitle == null) chapterTitle = "Chapter Quiz";

        initViews();
        restoreState(savedInstanceState);
        setupQuizList();
        setupTimerControls();
        setupNavigationControls();
    }

    // Interactive single question views matching Exam Practice Sample Questions.jpeg
    private TextView tvQuizQuestion;
    private TextView tvProgressPercentText;
    private android.widget.ProgressBar pbQuizProgress;
    private View btnOptionA, btnOptionB, btnOptionC, btnOptionD;
    private TextView tvOptionAText, tvOptionBText, tvOptionCText, tvOptionDText;
    private View ivCheckA, ivCheckB, ivCheckC, ivCheckD;
    private Button btnQuizSkip, btnQuizSubmit;
    private int selectedOptionIndex = -1;

    private void initViews() {
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvProgress = findViewById(R.id.tvProgress);

        tvTimerRemaining = findViewById(R.id.tvTimerRemaining);
        btnTimerStart = findViewById(R.id.btnTimerStart);
        btnTimerPause = findViewById(R.id.btnTimerPause);
        btnTimerResume = findViewById(R.id.btnTimerResume);
        btnTimerStop = findViewById(R.id.btnTimerStop);
        rvQuestions = findViewById(R.id.rvQuestions);
        tvEmptyQuiz = findViewById(R.id.tvEmptyQuiz);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);

        tvQuizQuestion = findViewById(R.id.tvQuizQuestion);
        tvProgressPercentText = findViewById(R.id.tvProgressPercentText);
        pbQuizProgress = findViewById(R.id.pbQuizProgress);

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

        if (tvChapterTitle != null) {
            tvChapterTitle.setText(chapterTitle);
        }

        View btnBack = findViewById(R.id.btnQuizBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnBookmark = findViewById(R.id.btnQuizBookmark);
        if (btnBookmark != null) {
            btnBookmark.setOnClickListener(v -> startActivity(new Intent(this, SavedItemsActivity.class)));
        }

        // Setup Option Click Listeners
        setupOptionClicks();

        // Setup Skip and Submit & Next
        if (btnQuizSkip != null) {
            btnQuizSkip.setOnClickListener(v -> {
                if (questions != null && currentPosition < questions.size() - 1) {
                    currentPosition++;
                    displaySingleQuestion(currentPosition);
                } else {
                    handleSubmitAttempt();
                }
            });
        }

        if (btnQuizSubmit != null) {
            btnQuizSubmit.setOnClickListener(v -> {
                if (questions != null && !questions.isEmpty() && currentPosition < questions.size()) {
                    Question q = questions.get(currentPosition);
                    if (selectedOptionIndex != -1) {
                        selectedAnswers.put(q.getId(), selectedOptionIndex);
                        boolean isCorrect = (selectedOptionIndex == q.getCorrectOptionIndex());
                        if (isCorrect) {
                            Toast.makeText(this, "✓ Correct Answer! Well done!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "✗ Incorrect. Correct was: " + q.getOptions().get(q.getCorrectOptionIndex()), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (currentPosition < questions.size() - 1) {
                        currentPosition++;
                        displaySingleQuestion(currentPosition);
                    } else {
                        handleSubmitAttempt();
                    }
                }
            });
        }

        View navHome = findViewById(R.id.navTabHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(this, HomeActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            });
        }
        View navStudy = findViewById(R.id.navTabStudy);
        if (navStudy != null) {
            navStudy.setOnClickListener(v -> startActivity(new Intent(this, ChapterActivity.class)));
        }
        View navSaved = findViewById(R.id.navTabSaved);
        if (navSaved != null) {
            navSaved.setOnClickListener(v -> startActivity(new Intent(this, SavedItemsActivity.class)));
        }
        View navProfile = findViewById(R.id.navTabProfile);
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }
    }

    private void setupOptionClicks() {
        if (btnOptionA != null) btnOptionA.setOnClickListener(v -> selectOption(0));
        if (btnOptionB != null) btnOptionB.setOnClickListener(v -> selectOption(1));
        if (btnOptionC != null) btnOptionC.setOnClickListener(v -> selectOption(2));
        if (btnOptionD != null) btnOptionD.setOnClickListener(v -> selectOption(3));
    }

    private void selectOption(int index) {
        selectedOptionIndex = index;
        resetOptionViews();
        highlightOptionSelected(index);
        if (questions != null && currentPosition < questions.size()) {
            selectedAnswers.put(questions.get(currentPosition).getId(), index);
        }
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

    private void highlightOptionSelected(int index) {
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

    private void displaySingleQuestion(int index) {
        if (questions == null || questions.isEmpty() || index < 0 || index >= questions.size()) return;
        Question q = questions.get(index);

        if (tvQuizQuestion != null) {
            tvQuizQuestion.setText(q.getQuestionText());
        }
        if (tvProgress != null) {
            tvProgress.setText("Question " + (index + 1) + " of " + questions.size());
        }
        int percent = (int) (((index + 1) / (float) questions.size()) * 100);
        if (pbQuizProgress != null) {
            pbQuizProgress.setProgress(percent);
        }
        if (tvProgressPercentText != null) {
            tvProgressPercentText.setText(percent + "%");
        }

        List<String> options = q.getOptions();
        resetOptionViews();

        if (options.size() > 0 && tvOptionAText != null) tvOptionAText.setText("A)  " + options.get(0));
        if (options.size() > 1 && tvOptionBText != null) tvOptionBText.setText("B)  " + options.get(1));
        if (options.size() > 2 && tvOptionCText != null) tvOptionCText.setText("C)  " + options.get(2));
        if (options.size() > 3 && tvOptionDText != null) tvOptionDText.setText("D)  " + options.get(3));

        Integer prevSelected = selectedAnswers.get(q.getId());
        if (prevSelected != null) {
            selectedOptionIndex = prevSelected;
            highlightOptionSelected(prevSelected);
        } else {
            selectedOptionIndex = -1;
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ArrayList<String> keys = savedInstanceState.getStringArrayList(KEY_ANSWER_KEYS);
            ArrayList<Integer> values = savedInstanceState.getIntegerArrayList(KEY_ANSWER_VALUES);
            if (keys != null && values != null && keys.size() == values.size()) {
                selectedAnswers.clear();
                for (int i = 0; i < keys.size(); i++) {
                    selectedAnswers.put(keys.get(i), values.get(i));
                }
            }
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POS, 0);
            savedRemainingTimerMillis = savedInstanceState.getLong(KEY_TIMER_REMAINING, DEFAULT_TIMER_DURATION_MS);
            timerWasRunning = savedInstanceState.getBoolean(KEY_TIMER_RUNNING, false);
            Log.d(TAG, "QuizActivity: State restored. Answers count: " + selectedAnswers.size());
        }
    }

    private void setupQuizList() {
        if ("full_mock".equalsIgnoreCase(chapterId) || "all".equalsIgnoreCase(chapterId) || chapterId == null || chapterId.trim().isEmpty()) {
            questions = SampleBiologyData.getAllQuestions();
        } else {
            questions = SampleBiologyData.getQuestions(paperId, chapterId);
            if (questions == null || questions.isEmpty()) {
                questions = SampleBiologyData.getAllQuestions();
            }
        }
        if (questions == null || questions.isEmpty()) {
            rvQuestions.setVisibility(View.GONE);
            tvEmptyQuiz.setVisibility(View.VISIBLE);
            btnPrev.setEnabled(false);
            btnNext.setEnabled(false);
            btnSubmit.setEnabled(false);
            tvProgress.setText("0 / 0");
            return;
        }

        rvQuestions.setVisibility(View.VISIBLE);
        tvEmptyQuiz.setVisibility(View.GONE);
        updateProgress();

        layoutManager = new LinearLayoutManager(this);
        rvQuestions.setLayoutManager(layoutManager);

        questionAdapter = new QuestionAdapter(questions, selectedAnswers, (questionId, selectedIndex) -> {
            selectedAnswers.put(questionId, selectedIndex);
            updateProgress();
        });
        rvQuestions.setAdapter(questionAdapter);

        if (currentPosition >= 0 && currentPosition < questions.size()) {
            rvQuestions.scrollToPosition(currentPosition);
        }

        rvQuestions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager != null) {
                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisible != RecyclerView.NO_POSITION) {
                        currentPosition = firstVisible;
                        displaySingleQuestion(currentPosition);

        updateNavigationButtons();
                    }
                }
            }
        });

        displaySingleQuestion(currentPosition);

        updateNavigationButtons();
    }

    private void updateProgress() {
        int total = questions != null ? questions.size() : 0;
        int answered = selectedAnswers.size();
        tvProgress.setText(String.format(Locale.getDefault(), "%d / %d answered", answered, total));
    }

    private void updateNavigationButtons() {
        int total = questions != null ? questions.size() : 0;
        btnPrev.setEnabled(currentPosition > 0);
        btnNext.setEnabled(currentPosition < total - 1);
    }

    private void setupTimerControls() {
        btnTimerStart.setOnClickListener(v -> {
            checkAndRequestNotificationPermission();
            if (isServiceBound && timerService != null) {
                timerService.startTimer(DEFAULT_TIMER_DURATION_MS);
                setTimerButtonsRunning();
            }
        });

        btnTimerPause.setOnClickListener(v -> {
            if (isServiceBound && timerService != null) {
                timerService.pauseTimer();
                savedRemainingTimerMillis = timerService.getRemainingTimeMillis();
                timerWasRunning = false;
                setTimerButtonsPaused();
            }
        });

        btnTimerResume.setOnClickListener(v -> {
            if (isServiceBound && timerService != null) {
                timerService.resumeTimer();
                setTimerButtonsRunning();
            }
        });

        btnTimerStop.setOnClickListener(v -> {
            if (isServiceBound && timerService != null) {
                timerService.stopTimer();
                savedRemainingTimerMillis = DEFAULT_TIMER_DURATION_MS;
                timerWasRunning = false;
                tvTimerRemaining.setText(R.string.timer_default);
                setTimerButtonsStopped();
            }
        });
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void setTimerButtonsRunning() {
        btnTimerStart.setEnabled(false);
        btnTimerPause.setEnabled(true);
        btnTimerResume.setEnabled(false);
        btnTimerStop.setEnabled(true);
    }

    private void setTimerButtonsPaused() {
        btnTimerStart.setEnabled(false);
        btnTimerPause.setEnabled(false);
        btnTimerResume.setEnabled(true);
        btnTimerStop.setEnabled(true);
    }

    private void setTimerButtonsStopped() {
        btnTimerStart.setEnabled(true);
        btnTimerPause.setEnabled(false);
        btnTimerResume.setEnabled(false);
        btnTimerStop.setEnabled(false);
    }

    private void setupNavigationControls() {
        btnPrev.setOnClickListener(v -> {
            if (currentPosition > 0) {
                currentPosition--;
                rvQuestions.smoothScrollToPosition(currentPosition);
                displaySingleQuestion(currentPosition);

        updateNavigationButtons();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (questions != null && currentPosition < questions.size() - 1) {
                currentPosition++;
                rvQuestions.smoothScrollToPosition(currentPosition);
                displaySingleQuestion(currentPosition);

        updateNavigationButtons();
            }
        });

        btnSubmit.setOnClickListener(v -> handleSubmitAttempt());
    }

    private void handleSubmitAttempt() {
        if (isSubmitted || questions.isEmpty()) return;

        int total = questions.size();
        int answered = selectedAnswers.size();
        int unanswered = total - answered;

        if (unanswered > 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_unanswered_title)
                    .setMessage(getString(R.string.dialog_unanswered_msg, unanswered))
                    .setPositiveButton(R.string.btn_submit_anyway, (dialog, which) -> submitQuiz())
                    .setNegativeButton(R.string.btn_continue, null)
                    .show();
        } else {
            submitQuiz();
        }
    }

    private void submitQuiz() {
        if (isSubmitted) return;
        isSubmitted = true;
        btnSubmit.setEnabled(false);

        // Stop timer on submission
        if (isServiceBound && timerService != null) {
            timerService.stopTimer();
        }

        QuizResult result = QuizScorer.score(questions, selectedAnswers);

        Intent intent = new Intent(QuizActivity.this, AnalyticsActivity.class);
        intent.putExtra(EXTRA_TOTAL, result.getTotalQuestions());
        intent.putExtra(EXTRA_CORRECT, result.getCorrectCount());
        intent.putExtra(EXTRA_INCORRECT, result.getIncorrectCount());
        intent.putExtra(EXTRA_UNANSWERED, result.getUnansweredCount());
        intent.putExtra(EXTRA_PERCENTAGE, result.getScorePercentage());
        startActivity(intent);
        finish();
    }

    private void updateTimerUI(long remainingMillis) {
        long seconds = (remainingMillis / 1000) % 60;
        long minutes = (remainingMillis / (1000 * 60)) % 60;
        tvTimerRemaining.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    // TimerUpdateReceiver.TimerListener callbacks
    @Override
    public void onTimerUpdate(long remainingMillis) {
        savedRemainingTimerMillis = remainingMillis;
        updateTimerUI(remainingMillis);
    }

    @Override
    public void onTimerFinished() {
        tvTimerRemaining.setText(R.string.timer_completed);
        setTimerButtonsStopped();
        savedRemainingTimerMillis = DEFAULT_TIMER_DURATION_MS;
        timerWasRunning = false;
        Toast.makeText(this, R.string.timer_completed, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "QuizActivity: onSaveInstanceState");

        ArrayList<String> keys = new ArrayList<>(selectedAnswers.keySet());
        ArrayList<Integer> values = new ArrayList<>();
        for (String k : keys) {
            values.add(selectedAnswers.get(k));
        }
        outState.putStringArrayList(KEY_ANSWER_KEYS, keys);
        outState.putIntegerArrayList(KEY_ANSWER_VALUES, values);
        outState.putInt(KEY_CURRENT_POS, currentPosition);

        if (isServiceBound && timerService != null) {
            outState.putLong(KEY_TIMER_REMAINING, timerService.getRemainingTimeMillis());
            outState.putBoolean(KEY_TIMER_RUNNING, timerService.isRunning());
        } else {
            outState.putLong(KEY_TIMER_REMAINING, savedRemainingTimerMillis);
            outState.putBoolean(KEY_TIMER_RUNNING, timerWasRunning);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "QuizActivity: onStart");

        // 1. Register dynamic BroadcastReceiver safely
        if (!isReceiverRegistered) {
            timerReceiver = new TimerUpdateReceiver(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(StudyTimerService.ACTION_TIMER_UPDATE);
            filter.addAction(StudyTimerService.ACTION_TIMER_FINISHED);
            ContextCompat.registerReceiver(this, timerReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
            isReceiverRegistered = true;
            Log.d(TAG, "QuizActivity: Registered TimerUpdateReceiver");
        }

        // 2. Bind to StudyTimerService
        Intent serviceIntent = new Intent(this, StudyTimerService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "QuizActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "QuizActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "QuizActivity: onStop");

        // Pause timer and capture duration when user leaves QuizActivity
        if (isServiceBound) {
            if (timerService != null) {
                timerWasRunning = timerService.isRunning();
                if (timerWasRunning) {
                    timerService.pauseTimer();
                }
                savedRemainingTimerMillis = timerService.getRemainingTimeMillis();
            }
            try {
                unbindService(serviceConnection);
            } catch (Exception e) {
                Log.e(TAG, "Error unbinding service: " + e.getMessage());
            }
            isServiceBound = false;
            timerService = null;
            Log.d(TAG, "QuizActivity: Unbound StudyTimerService");
        }

        // Unregister BroadcastReceiver
        if (isReceiverRegistered && timerReceiver != null) {
            timerReceiver.clearListener();
            unregisterReceiver(timerReceiver);
            isReceiverRegistered = false;
            timerReceiver = null;
            Log.d(TAG, "QuizActivity: Unregistered TimerUpdateReceiver");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "QuizActivity: onDestroy");
    }
}


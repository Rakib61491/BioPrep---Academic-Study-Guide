package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.PracticeAnswerEntity;
import com.example.bioprep.database.PracticeAttemptEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Quiz Results Activity implementing Requirement 10:
 * - Summary Section: Total questions, Correct, Incorrect, Unanswered, Accuracy %, Time Spent.
 * - Question Review Section: Shows all questions, options, student's answer, correct answer, status badge, and explanations.
 * - Bottom Navigation: Displays ONLY two buttons - "Again" and "Back".
 */
public class QuizResultActivity extends AppCompatActivity {

    public static final String EXTRA_ATTEMPT_ID = "extra_attempt_id";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextView tvChapterSubtitle;
    private TextView tvAccuracyPercent;
    private TextView tvFeedbackText;
    private TextView tvTimeSpent;
    private TextView tvTotal;
    private TextView tvCorrect;
    private TextView tvIncorrect;
    private TextView tvUnanswered;
    private RecyclerView rvQuestions;
    private Button btnAgain;
    private Button btnBack;

    private PracticeAttemptEntity currentAttempt;
    private final List<PracticeAnswerEntity> answerList = new ArrayList<>();
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        bindViews();

        long attemptId = getIntent().getLongExtra(EXTRA_ATTEMPT_ID, -1L);
        if (attemptId != -1L) {
            loadAttemptData(attemptId);
        } else {
            finish();
        }
    }

    private void bindViews() {
        ImageButton btnBackHeader = findViewById(R.id.btnResultBackHeader);
        if (btnBackHeader != null) {
            btnBackHeader.setOnClickListener(v -> finish());
        }

        tvChapterSubtitle = findViewById(R.id.tvResultChapterSubtitle);
        tvAccuracyPercent = findViewById(R.id.tvResultAccuracyPercent);
        tvFeedbackText = findViewById(R.id.tvResultFeedbackText);
        tvTimeSpent = findViewById(R.id.tvResultTimeSpent);
        tvTotal = findViewById(R.id.tvResultTotal);
        tvCorrect = findViewById(R.id.tvResultCorrect);
        tvIncorrect = findViewById(R.id.tvResultIncorrect);
        tvUnanswered = findViewById(R.id.tvResultUnanswered);

        rvQuestions = findViewById(R.id.rvResultQuestions);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReviewAdapter(answerList);
        rvQuestions.setAdapter(adapter);

        btnBack = findViewById(R.id.btnResultBack);
        btnAgain = findViewById(R.id.btnResultAgain);

        btnBack.setOnClickListener(v -> finish());

        btnAgain.setOnClickListener(v -> {
            if (currentAttempt != null) {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_CHAPTER_ID, currentAttempt.getChapterId());
                intent.putExtra(QuizActivity.EXTRA_PAPER_ID, currentAttempt.getPaperId());
                intent.putExtra(QuizActivity.EXTRA_CHAPTER_TITLE, currentAttempt.getChapterTitle());
                startActivity(intent);
                finish();
            } else {
                finish();
            }
        });
    }

    private void loadAttemptData(long attemptId) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            PracticeAttemptEntity attempt = db.practiceDao().getAttemptById(attemptId);
            List<PracticeAnswerEntity> answers = db.practiceDao().getAnswersForAttempt(attemptId);

            mainHandler.post(() -> {
                if (attempt == null) {
                    finish();
                    return;
                }
                currentAttempt = attempt;
                displaySummary(attempt);

                answerList.clear();
                if (answers != null) {
                    answerList.addAll(answers);
                }
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void displaySummary(PracticeAttemptEntity attempt) {
        if (tvChapterSubtitle != null) {
            tvChapterSubtitle.setText(attempt.getChapterTitle() != null ? attempt.getChapterTitle() : "Biology Exam Practice");
        }
        if (tvAccuracyPercent != null) {
            tvAccuracyPercent.setText(attempt.getAccuracyPercent() + "%");
        }

        if (tvFeedbackText != null) {
            int acc = attempt.getAccuracyPercent();
            if (acc >= 85) {
                tvFeedbackText.setText("Outstanding Mastery! 🌟");
            } else if (acc >= 70) {
                tvFeedbackText.setText("Good Performance! Keep it up. 👍");
            } else if (acc >= 50) {
                tvFeedbackText.setText("Fair Attempt. Needs review. 📖");
            } else {
                tvFeedbackText.setText("Needs Improvement. Revise chapter! ⚠️");
            }
        }

        if (tvTimeSpent != null) {
            int secs = attempt.getTimeSpentSeconds();
            int m = secs / 60;
            int s = secs % 60;
            tvTimeSpent.setText(String.format(Locale.getDefault(), "Time spent: %02dm %02ds", m, s));
        }

        if (tvTotal != null) tvTotal.setText(String.valueOf(attempt.getTotalQuestions()));
        if (tvCorrect != null) tvCorrect.setText(String.valueOf(attempt.getCorrectCount()));
        if (tvIncorrect != null) tvIncorrect.setText(String.valueOf(attempt.getIncorrectCount()));
        if (tvUnanswered != null) tvUnanswered.setText(String.valueOf(attempt.getUnansweredCount()));
    }

    private static class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
        private final List<PracticeAnswerEntity> items;

        public ReviewAdapter(List<PracticeAnswerEntity> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_review, parent, false);
            return new ReviewViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
            PracticeAnswerEntity item = items.get(position);
            holder.bind(item, position + 1);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestionNum;
        private final TextView tvStatusBadge;
        private final TextView tvQuestionText;
        private final TextView tvOptionA;
        private final TextView tvOptionB;
        private final TextView tvOptionC;
        private final TextView tvOptionD;
        private final TextView tvStudentChoice;
        private final TextView tvCorrectAnswer;
        private final TextView tvExplanation;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNum = itemView.findViewById(R.id.tvReviewQuestionNum);
            tvStatusBadge = itemView.findViewById(R.id.tvReviewStatusBadge);
            tvQuestionText = itemView.findViewById(R.id.tvReviewQuestionText);
            tvOptionA = itemView.findViewById(R.id.tvReviewOptionA);
            tvOptionB = itemView.findViewById(R.id.tvReviewOptionB);
            tvOptionC = itemView.findViewById(R.id.tvReviewOptionC);
            tvOptionD = itemView.findViewById(R.id.tvReviewOptionD);
            tvStudentChoice = itemView.findViewById(R.id.tvReviewStudentChoice);
            tvCorrectAnswer = itemView.findViewById(R.id.tvReviewCorrectAnswer);
            tvExplanation = itemView.findViewById(R.id.tvReviewExplanation);
        }

        public void bind(PracticeAnswerEntity item, int number) {
            tvQuestionNum.setText("Question " + number);
            tvQuestionText.setText(item.getQuestionText());

            String[] options = new String[]{
                    item.getOptionA(),
                    item.getOptionB(),
                    item.getOptionC(),
                    item.getOptionD()
            };
            TextView[] optViews = new TextView[]{tvOptionA, tvOptionB, tvOptionC, tvOptionD};
            String[] labels = new String[]{"A) ", "B) ", "C) ", "D) "};

            for (int i = 0; i < 4; i++) {
                optViews[i].setText(labels[i] + (i < options.length && options[i] != null ? options[i] : ""));
                // Default styling
                optViews[i].setBackgroundColor(0xFFF8FAFC);
                optViews[i].setTextColor(0xFF0F172A);
            }

            int selected = item.getSelectedOption();
            int correct = item.getCorrectOption();

            // Correct option highlight (Soft green)
            if (correct >= 0 && correct < 4) {
                optViews[correct].setBackgroundColor(0xFFDCFCE7);
                optViews[correct].setTextColor(0xFF15803D);
            }

            // If student answered wrong, highlight wrong pick in soft red
            if (selected >= 0 && selected < 4 && selected != correct) {
                optViews[selected].setBackgroundColor(0xFFFEE2E2);
                optViews[selected].setTextColor(0xFFB91C1C);
            }

            if (selected == -1) {
                tvStatusBadge.setText("Skipped");
                tvStatusBadge.setBackgroundColor(0xFFFEF3C7);
                tvStatusBadge.setTextColor(0xFFB45309);
                tvStudentChoice.setText("Your answer: [Skipped / Unanswered]");
                tvStudentChoice.setTextColor(0xFFB45309);
            } else if (item.isCorrect()) {
                tvStatusBadge.setText("Correct");
                tvStatusBadge.setBackgroundColor(0xFFDCFCE7);
                tvStatusBadge.setTextColor(0xFF15803D);
                String chosenText = selected < options.length ? options[selected] : "";
                tvStudentChoice.setText("Your answer: " + labels[selected] + chosenText);
                tvStudentChoice.setTextColor(0xFF15803D);
            } else {
                tvStatusBadge.setText("Incorrect");
                tvStatusBadge.setBackgroundColor(0xFFFEE2E2);
                tvStatusBadge.setTextColor(0xFFB91C1C);
                String chosenText = (selected >= 0 && selected < options.length) ? options[selected] : "None";
                tvStudentChoice.setText("Your answer: " + (selected >= 0 ? labels[selected] : "") + chosenText);
                tvStudentChoice.setTextColor(0xFFB91C1C);
            }

            String correctText = (correct >= 0 && correct < options.length) ? options[correct] : "";
            tvCorrectAnswer.setText("Correct answer: " + (correct >= 0 ? labels[correct] : "") + correctText);

            if (item.getExplanation() != null && !item.getExplanation().trim().isEmpty()) {
                tvExplanation.setText(item.getExplanation());
            } else {
                tvExplanation.setText("Based on the National Curriculum and Textbook Board (NCTB) HSC Biology syllabus standards.");
            }
        }
    }
}


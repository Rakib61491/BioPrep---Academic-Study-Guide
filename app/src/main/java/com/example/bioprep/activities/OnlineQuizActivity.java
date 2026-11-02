package com.example.bioprep.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.bioprep.R;
import com.example.bioprep.models.Question;
import com.example.bioprep.network.HttpRequestManager;
import com.example.bioprep.network.RemoteQuestionParser;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates HTTP GET requests using HttpURLConnection and JSON parsing with org.json.
 */
public class OnlineQuizActivity extends AppCompatActivity {

    private static final String TAG = "BIOPREP_LIFECYCLE";

    private static final String KEY_QUESTION_TEXT = "saved_question_text";
    private static final String KEY_TOPIC = "saved_topic";
    private static final String KEY_OPTIONS = "saved_options";
    private static final String KEY_CORRECT_INDEX = "saved_correct_index";
    private static final String KEY_EXPLANATION = "saved_explanation";
    private static final String KEY_SOURCE = "saved_source";
    private static final String KEY_SELECTED_INDEX = "saved_selected_index";
    private static final String KEY_FEEDBACK_TEXT = "saved_feedback_text";
    private static final String KEY_FEEDBACK_COLOR = "saved_feedback_color";
    private static final String KEY_FEEDBACK_VISIBLE = "saved_feedback_visible";
    private static final String KEY_ERROR_MSG = "saved_error_msg";

    private Button btnFetch;
    private LinearLayout layoutLoading;
    private LinearLayout layoutError;
    private TextView tvError;
    private Button btnRetry;
    private LinearLayout cardQuestion;
    private TextView tvTopic;
    private TextView tvQuestionText;
    private RadioGroup rgOptions;
    private RadioButton rbOption0;
    private RadioButton rbOption1;
    private RadioButton rbOption2;
    private RadioButton rbOption3;
    private Button btnCheckAnswer;
    private TextView tvFeedback;
    private TextView tvSource;

    private HttpRequestManager httpManager;
    private Question currentQuestion;
    private int selectedOptionIndex = -1;
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnlineQuizActivity: onCreate");
        setContentView(R.layout.activity_online_quiz);

        httpManager = new HttpRequestManager();
        initViews();

        btnFetch.setOnClickListener(v -> fetchRemoteQuestion());
        btnRetry.setOnClickListener(v -> fetchRemoteQuestion());
        btnCheckAnswer.setOnClickListener(v -> checkAnswer());

        rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbOnline0) selectedOptionIndex = 0;
            else if (checkedId == R.id.rbOnline1) selectedOptionIndex = 1;
            else if (checkedId == R.id.rbOnline2) selectedOptionIndex = 2;
            else if (checkedId == R.id.rbOnline3) selectedOptionIndex = 3;
            else selectedOptionIndex = -1;

            if (selectedOptionIndex != -1) {
                btnCheckAnswer.setEnabled(true);
            }
        });

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            // Automatically fetch first question when opened cleanly
            fetchRemoteQuestion();
        }
    }

    private void initViews() {
        btnFetch = findViewById(R.id.btnFetchQuestion);
        layoutLoading = findViewById(R.id.layoutOnlineLoading);
        layoutError = findViewById(R.id.layoutOnlineError);
        tvError = findViewById(R.id.tvOnlineError);
        btnRetry = findViewById(R.id.btnRetryFetch);
        cardQuestion = findViewById(R.id.cardOnlineQuestion);
        tvTopic = findViewById(R.id.tvOnlineTopic);
        tvQuestionText = findViewById(R.id.tvOnlineQuestionText);
        rgOptions = findViewById(R.id.rgOnlineOptions);
        rbOption0 = findViewById(R.id.rbOnline0);
        rbOption1 = findViewById(R.id.rbOnline1);
        rbOption2 = findViewById(R.id.rbOnline2);
        rbOption3 = findViewById(R.id.rbOnline3);
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer);
        tvFeedback = findViewById(R.id.tvOnlineFeedback);
        tvSource = findViewById(R.id.tvOnlineSourceLabel);
    }

    private void restoreState(Bundle savedInstanceState) {
        String qText = savedInstanceState.getString(KEY_QUESTION_TEXT);
        if (qText != null) {
            String topic = savedInstanceState.getString(KEY_TOPIC, "");
            ArrayList<String> options = savedInstanceState.getStringArrayList(KEY_OPTIONS);
            int correctIndex = savedInstanceState.getInt(KEY_CORRECT_INDEX, 0);
            String explanation = savedInstanceState.getString(KEY_EXPLANATION, "");
            String source = savedInstanceState.getString(KEY_SOURCE, "OpenTDB Science & Nature");
            selectedOptionIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, -1);

            if (options != null && options.size() == 4) {
                currentQuestion = new Question("saved_remote", "", "", topic, qText, options, correctIndex, explanation, source, null);
                displayQuestion(currentQuestion);

                if (selectedOptionIndex >= 0 && selectedOptionIndex < 4) {
                    switch (selectedOptionIndex) {
                        case 0: rbOption0.setChecked(true); break;
                        case 1: rbOption1.setChecked(true); break;
                        case 2: rbOption2.setChecked(true); break;
                        case 3: rbOption3.setChecked(true); break;
                    }
                }

                if (savedInstanceState.getBoolean(KEY_FEEDBACK_VISIBLE, false)) {
                    tvFeedback.setText(savedInstanceState.getString(KEY_FEEDBACK_TEXT, ""));
                    tvFeedback.setTextColor(savedInstanceState.getInt(KEY_FEEDBACK_COLOR, ContextCompat.getColor(this, R.color.color_correct)));
                    tvFeedback.setVisibility(View.VISIBLE);
                    btnCheckAnswer.setEnabled(false);
                }
            }
        } else {
            String errorMsg = savedInstanceState.getString(KEY_ERROR_MSG);
            if (errorMsg != null) {
                showError(errorMsg);
            } else {
                fetchRemoteQuestion();
            }
        }
    }

    private void fetchRemoteQuestion() {
        if (isFetching) return;

        isFetching = true;
        btnFetch.setEnabled(false);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
        cardQuestion.setVisibility(View.GONE);
        tvFeedback.setVisibility(View.GONE);

        String endpoint = getString(R.string.opentdb_endpoint);

        httpManager.executeGet(endpoint, new HttpRequestManager.HttpCallback() {
            @Override
            public void onSuccess(String responseBody) {
                if (isFinishing() || isDestroyed()) return;

                isFetching = false;
                btnFetch.setEnabled(true);
                layoutLoading.setVisibility(View.GONE);

                try {
                    currentQuestion = RemoteQuestionParser.parseQuestion(responseBody);
                    displayQuestion(currentQuestion);
                } catch (Exception e) {
                    Log.e(TAG, "Parser error: " + e.getMessage(), e);
                    if (e.getMessage() != null && e.getMessage().contains("response_code")) {
                        showError(getString(R.string.msg_error_api));
                    } else {
                        showError(getString(R.string.msg_error_parse));
                    }
                }
            }

            @Override
            public void onFailure(Exception exception) {
                if (isFinishing() || isDestroyed()) return;

                isFetching = false;
                btnFetch.setEnabled(true);
                layoutLoading.setVisibility(View.GONE);

                String errorMsg;
                if (exception instanceof SocketTimeoutException) {
                    errorMsg = getString(R.string.msg_error_timeout);
                } else if (exception instanceof HttpRequestManager.HttpException) {
                    int code = ((HttpRequestManager.HttpException) exception).getStatusCode();
                    errorMsg = getString(R.string.msg_error_server, code);
                } else if (exception instanceof IOException) {
                    errorMsg = getString(R.string.msg_error_network);
                } else {
                    errorMsg = getString(R.string.msg_error_server, 500);
                }
                showError(errorMsg);
            }
        });
    }

    private void displayQuestion(Question question) {
        if (question == null) return;

        tvTopic.setText(question.getTopic());
        tvQuestionText.setText(question.getQuestionText());

        List<String> options = question.getOptions();
        rbOption0.setText(options.size() > 0 ? options.get(0) : "");
        rbOption1.setText(options.size() > 1 ? options.get(1) : "");
        rbOption2.setText(options.size() > 2 ? options.get(2) : "");
        rbOption3.setText(options.size() > 3 ? options.get(3) : "");

        rgOptions.clearCheck();
        selectedOptionIndex = -1;
        tvFeedback.setVisibility(View.GONE);
        btnCheckAnswer.setEnabled(true);

        tvSource.setText(getString(R.string.source_label, question.getSource()));
        cardQuestion.setVisibility(View.VISIBLE);
    }

    private void checkAnswer() {
        if (currentQuestion == null) return;

        if (selectedOptionIndex < 0) {
            Toast.makeText(this, R.string.msg_select_answer, Toast.LENGTH_SHORT).show();
            return;
        }

        int correctIndex = currentQuestion.getCorrectOptionIndex();
        String correctText = currentQuestion.getOptions().get(correctIndex);

        tvFeedback.setVisibility(View.VISIBLE);
        if (selectedOptionIndex == correctIndex) {
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.color_correct));
            tvFeedback.setText(R.string.msg_correct);
        } else {
            tvFeedback.setTextColor(ContextCompat.getColor(this, R.color.color_incorrect));
            tvFeedback.setText(getString(R.string.msg_incorrect, correctText));
        }

        btnCheckAnswer.setEnabled(false);
    }

    private void showError(String message) {
        tvError.setText(message);
        layoutError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "OnlineQuizActivity: onSaveInstanceState");
        if (currentQuestion != null) {
            outState.putString(KEY_QUESTION_TEXT, currentQuestion.getQuestionText());
            outState.putString(KEY_TOPIC, currentQuestion.getTopic());
            outState.putStringArrayList(KEY_OPTIONS, new ArrayList<>(currentQuestion.getOptions()));
            outState.putInt(KEY_CORRECT_INDEX, currentQuestion.getCorrectOptionIndex());
            outState.putString(KEY_EXPLANATION, currentQuestion.getExplanation());
            outState.putString(KEY_SOURCE, currentQuestion.getSource());
            outState.putInt(KEY_SELECTED_INDEX, selectedOptionIndex);
            if (tvFeedback.getVisibility() == View.VISIBLE) {
                outState.putBoolean(KEY_FEEDBACK_VISIBLE, true);
                outState.putString(KEY_FEEDBACK_TEXT, tvFeedback.getText().toString());
                outState.putInt(KEY_FEEDBACK_COLOR, tvFeedback.getCurrentTextColor());
            }
        } else if (layoutError.getVisibility() == View.VISIBLE) {
            outState.putString(KEY_ERROR_MSG, tvError.getText().toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnlineQuizActivity: onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnlineQuizActivity: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnlineQuizActivity: onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnlineQuizActivity: onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnlineQuizActivity: onDestroy");
        if (httpManager != null) {
            httpManager.cancel();
            httpManager.shutdown();
        }
    }
}

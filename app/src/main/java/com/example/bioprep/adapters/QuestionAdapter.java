package com.example.bioprep.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bioprep.R;
import com.example.bioprep.models.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView Adapter for displaying Biology MCQs.
 * Implements recycling protection to avoid stale selections.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(String questionId, int selectedIndex);
    }

    private final List<Question> questions;
    private final Map<String, Integer> selectedAnswers;
    private final OnAnswerSelectedListener listener;

    public QuestionAdapter(List<Question> questions, Map<String, Integer> selectedAnswers, OnAnswerSelectedListener listener) {
        this.questions = questions != null ? new ArrayList<>(questions) : new ArrayList<>();
        this.selectedAnswers = selectedAnswers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position, selectedAnswers, listener);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvQuestionNumber;
        private final TextView tvQuestionTopic;
        private final TextView tvQuestionText;
        private final RadioGroup rgOptions;
        private final RadioButton rbOption0;
        private final RadioButton rbOption1;
        private final RadioButton rbOption2;
        private final RadioButton rbOption3;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvItemQuestionNumber);
            tvQuestionTopic = itemView.findViewById(R.id.tvItemQuestionTopic);
            tvQuestionText = itemView.findViewById(R.id.tvItemQuestionText);
            rgOptions = itemView.findViewById(R.id.rgOptions);
            rbOption0 = itemView.findViewById(R.id.rbOption0);
            rbOption1 = itemView.findViewById(R.id.rbOption1);
            rbOption2 = itemView.findViewById(R.id.rbOption2);
            rbOption3 = itemView.findViewById(R.id.rbOption3);
        }

        public void bind(Question question, int position, Map<String, Integer> selectedAnswers, OnAnswerSelectedListener listener) {
            // 1. Detach old listener before modifying state to prevent callback triggers during recycling
            rgOptions.setOnCheckedChangeListener(null);

            // 2. Set text content
            tvQuestionNumber.setText(String.format("Question %d", position + 1));
            tvQuestionTopic.setText(question.getTopic() != null ? question.getTopic() : "");
            tvQuestionText.setText(question.getQuestionText());

            List<String> options = question.getOptions();
            rbOption0.setText(options.size() > 0 ? options.get(0) : "");
            rbOption1.setText(options.size() > 1 ? options.get(1) : "");
            rbOption2.setText(options.size() > 2 ? options.get(2) : "");
            rbOption3.setText(options.size() > 3 ? options.get(3) : "");

            // 3. Clear check and restore checked state from answer map
            rgOptions.clearCheck();
            Integer selectedIndex = selectedAnswers != null ? selectedAnswers.get(question.getId()) : null;
            if (selectedIndex != null) {
                switch (selectedIndex) {
                    case 0:
                        rbOption0.setChecked(true);
                        break;
                    case 1:
                        rbOption1.setChecked(true);
                        break;
                    case 2:
                        rbOption2.setChecked(true);
                        break;
                    case 3:
                        rbOption3.setChecked(true);
                        break;
                    default:
                        break;
                }
            }

            // 4. Attach new listener
            rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
                int index = -1;
                if (checkedId == R.id.rbOption0) {
                    index = 0;
                } else if (checkedId == R.id.rbOption1) {
                    index = 1;
                } else if (checkedId == R.id.rbOption2) {
                    index = 2;
                } else if (checkedId == R.id.rbOption3) {
                    index = 3;
                }

                if (index != -1 && listener != null) {
                    listener.onAnswerSelected(question.getId(), index);
                }
            });
        }
    }
}


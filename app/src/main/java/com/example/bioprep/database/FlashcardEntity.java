package com.example.bioprep.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "flashcards", indices = {@Index("chapterId")})
public class FlashcardEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String chapterId;
    private String paper;
    private String questionEn;
    private String questionBn;
    private String answerEn;
    private String answerBn;
    private String medicalTip;
    private boolean isAgain;

    public FlashcardEntity(@NonNull String id, String chapterId, String paper, String questionEn,
                           String questionBn, String answerEn, String answerBn, String medicalTip) {
        this.id = id;
        this.chapterId = chapterId;
        this.paper = paper;
        this.questionEn = questionEn;
        this.questionBn = questionBn;
        this.answerEn = answerEn;
        this.answerBn = answerBn;
        this.medicalTip = medicalTip;
        this.isAgain = false;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getPaper() {
        return paper;
    }

    public void setPaper(String paper) {
        this.paper = paper;
    }

    public String getQuestionEn() {
        return questionEn;
    }

    public void setQuestionEn(String questionEn) {
        this.questionEn = questionEn;
    }

    public String getQuestionBn() {
        return questionBn;
    }

    public void setQuestionBn(String questionBn) {
        this.questionBn = questionBn;
    }

    public String getAnswerEn() {
        return answerEn;
    }

    public void setAnswerEn(String answerEn) {
        this.answerEn = answerEn;
    }

    public String getAnswerBn() {
        return answerBn;
    }

    public void setAnswerBn(String answerBn) {
        this.answerBn = answerBn;
    }

    public String getMedicalTip() {
        return medicalTip;
    }

    public void setMedicalTip(String medicalTip) {
        this.medicalTip = medicalTip;
    }

    public boolean isAgain() {
        return isAgain;
    }

    public void setAgain(boolean again) {
        isAgain = again;
    }
}


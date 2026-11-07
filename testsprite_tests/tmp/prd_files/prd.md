# BioPrep Academic Study Guide - Product Requirements Document (PRD)

## Project Overview
BioPrep Academic Study Guide is a native Android educational application engineered for HSC biology students and medical admission test candidates in Bangladesh (covering Botany and Zoology).

## Key Features & User Stories

### 1. Startup & Navigation
- As a student, I see a Loading Screen with an animated progress bar that transitions to the Home Dashboard.
- As a student on Home, tapping my avatar opens My Profile.
- As a student on Home, tapping the Goal Ring opens Daily Goal Analysis.
- Every sub-screen provides a back `<` button to return smoothly.

### 2. Active Recall & Spaced Repetition
- As a student, I view active recall prompt questions in English and Bengali.
- I can draft my thoughts in a scratchpad before tapping "Reveal Answer".
- I rate my recall using "Again", "Good", or "Easy" pills to update mastery.

### 3. Study Notes Management
- As a student, I search, view, create, edit, and delete notes.
- Notes are persistently stored using SQLite ContentProvider (`BioNotesProvider`).
- Filter notes by categories (Photosynthesis, Genetics, Cell Biology, Ecology).

### 4. Flashcards Interface
- As a student, I flip flashcards in 3D to see answers and medical tips.
- Rate flashcards with Anki-style buttons (Again, Hard, Good, Easy).
- Toggle Lab Manual experiments (ListView & RecyclerView).

### 5. Exam Practice & MCQs
- As a student, I browse Botany and Zoology chapters via collapsible accordions.
- Practice chapter-specific MCQs or launch a full 20-question mock exam.
- Select answers (A/B/C/D) with checkmark indicators and instant feedback.
- Real-time countdown timer with Start, Pause, Resume, and Stop controls.

### 6. Goal Analytics & Saved Items
- Track 7-day study streak, daily goal progress, and 4 KPI metrics.
- Adjust study goals and share summary reports.
- Bookmark topics, notes, flashcards, and questions into Saved Items.

### 7. User Profile & Preferences
- View ranking badge, edit personal name and college details via SharedPreferences.
- Configure study reminders and preferences.


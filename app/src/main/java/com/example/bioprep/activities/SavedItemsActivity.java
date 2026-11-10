package com.example.bioprep.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bioprep.R;
import com.example.bioprep.database.AppDatabase;
import com.example.bioprep.database.SavedItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Saved Items Activity implementing Stitch UI Design and dynamic persistent bookmarks:
 * - Initially empty until saved by the user (Requirements 6, 8, 9, 10).
 * - "All" pill has white background (#FFFFFF) with dark legible text (Requirement 7).
 * - Categorized by All, Saved Notes, Saved Active Recall, Saved MCQ / Questions, and Saved Flash Cards.
 */
public class SavedItemsActivity extends AppCompatActivity {

    private LinearLayout layoutSavedItemsContainer;
    private LinearLayout layoutSavedEmpty;
    private TextView tvSavedEmptyTitle;
    private TextView tvSavedEmptyDesc;

    private TextView pillAll;
    private TextView pillNotes;
    private TextView pillRecall;
    private TextView pillMcq;
    private TextView pillFlashcards;

    private String currentFilterType = "ALL";
    private String currentSearchQuery = "";
    private final List<SavedItemEntity> currentLoadedItems = new ArrayList<>();

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);

        db = AppDatabase.getInstance(this);

        initViews();
        setupFilterPills();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedItems();
    }

    private void initViews() {
        layoutSavedItemsContainer = findViewById(R.id.layoutSavedItemsContainer);
        layoutSavedEmpty = findViewById(R.id.layoutSavedEmpty);
        tvSavedEmptyTitle = findViewById(R.id.tvSavedEmptyTitle);
        tvSavedEmptyDesc = findViewById(R.id.tvSavedEmptyDesc);

        pillAll = findViewById(R.id.pillFilterAll);
        pillNotes = findViewById(R.id.pillFilterNotes);
        pillRecall = findViewById(R.id.pillFilterRecall);
        pillMcq = findViewById(R.id.pillFilterMcq);
        pillFlashcards = findViewById(R.id.pillFilterFlashcards);

        View btnBack = findViewById(R.id.btnSavedBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View btnClearAll = findViewById(R.id.btnClearAllBookmarks);
        if (btnClearAll != null) {
            btnClearAll.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(SavedItemsActivity.this)
                        .setTitle("Clear All Saved Items")
                        .setMessage("Are you sure you want to remove all saved items?")
                        .setPositiveButton("Clear", (dialog, which) -> {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                db.savedItemDao().clearAll();
                                runOnUiThread(this::loadSavedItems);
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        EditText etSearchSaved = findViewById(R.id.etSearchSaved);
        if (etSearchSaved != null) {
            etSearchSaved.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().trim().toLowerCase();
                    renderSavedCards();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupFilterPills() {
        updatePillsUI();

        if (pillAll != null) {
            pillAll.setOnClickListener(v -> {
                currentFilterType = "ALL";
                updatePillsUI();
                loadSavedItems();
            });
        }

        if (pillNotes != null) {
            pillNotes.setOnClickListener(v -> {
                currentFilterType = "NOTE";
                updatePillsUI();
                loadSavedItems();
            });
        }

        if (pillRecall != null) {
            pillRecall.setOnClickListener(v -> {
                currentFilterType = "ACTIVE_RECALL";
                updatePillsUI();
                loadSavedItems();
            });
        }

        if (pillMcq != null) {
            pillMcq.setOnClickListener(v -> {
                currentFilterType = "MCQ";
                updatePillsUI();
                loadSavedItems();
            });
        }

        if (pillFlashcards != null) {
            pillFlashcards.setOnClickListener(v -> {
                currentFilterType = "FLASHCARD";
                updatePillsUI();
                loadSavedItems();
            });
        }
    }

    private void updatePillsUI() {
        // Requirement 7: "In 'Saved' page, 'All' button background color should be white. It must show all the saved button."
        if (pillAll != null) {
            if ("ALL".equals(currentFilterType)) {
                pillAll.setBackgroundResource(R.drawable.bg_pill_white);
                pillAll.setTextColor(0xFF0F172A); // Dark readable text on white
            } else {
                pillAll.setBackgroundResource(R.drawable.bg_card_dark_teal);
                pillAll.setTextColor(0xFFFFFFFF);
            }
        }

        TextView[] otherPills = {pillNotes, pillRecall, pillMcq, pillFlashcards};
        String[] types = {"NOTE", "ACTIVE_RECALL", "MCQ", "FLASHCARD"};

        for (int i = 0; i < otherPills.length; i++) {
            TextView p = otherPills[i];
            if (p == null) continue;
            if (types[i].equals(currentFilterType)) {
                p.setBackgroundResource(R.drawable.bg_pill_cyan);
                p.setTextColor(0xFF0A382C);
            } else {
                p.setBackgroundResource(R.drawable.bg_card_dark_teal);
                p.setTextColor(0xFFFFFFFF);
            }
        }
    }

    private void loadSavedItems() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<SavedItemEntity> items;
            if ("ALL".equals(currentFilterType)) {
                items = db.savedItemDao().getAllSavedItems();
            } else {
                items = db.savedItemDao().getSavedItemsByType(currentFilterType);
            }

            runOnUiThread(() -> {
                currentLoadedItems.clear();
                if (items != null) {
                    currentLoadedItems.addAll(items);
                }
                renderSavedCards();
            });
        });
    }

    private void renderSavedCards() {
        if (layoutSavedItemsContainer == null) return;
        layoutSavedItemsContainer.removeAllViews();

        List<SavedItemEntity> filtered = new ArrayList<>();
        for (SavedItemEntity entity : currentLoadedItems) {
            if (currentSearchQuery.isEmpty() ||
                    (entity.getTitle() != null && entity.getTitle().toLowerCase().contains(currentSearchQuery)) ||
                    (entity.getContent() != null && entity.getContent().toLowerCase().contains(currentSearchQuery)) ||
                    (entity.getExtraInfo() != null && entity.getExtraInfo().toLowerCase().contains(currentSearchQuery))) {
                filtered.add(entity);
            }
        }

        if (filtered.isEmpty()) {
            if (layoutSavedEmpty != null) {
                layoutSavedEmpty.setVisibility(View.VISIBLE);
                if (tvSavedEmptyTitle != null) {
                    if ("ALL".equals(currentFilterType)) {
                        tvSavedEmptyTitle.setText("No Saved Items Yet");
                    } else if ("NOTE".equals(currentFilterType)) {
                        tvSavedEmptyTitle.setText("No Saved Notes Yet");
                    } else if ("ACTIVE_RECALL".equals(currentFilterType)) {
                        tvSavedEmptyTitle.setText("No Saved Active Recall Drills");
                    } else if ("MCQ".equals(currentFilterType)) {
                        tvSavedEmptyTitle.setText("No Saved MCQs Yet");
                    } else if ("FLASHCARD".equals(currentFilterType)) {
                        tvSavedEmptyTitle.setText("No Saved Flashcards Yet");
                    }
                }
            }
            return;
        }

        if (layoutSavedEmpty != null) {
            layoutSavedEmpty.setVisibility(View.GONE);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (SavedItemEntity item : filtered) {
            View card = inflater.inflate(R.layout.item_saved_card, layoutSavedItemsContainer, false);

            TextView tvType = card.findViewById(R.id.tvSavedItemType);
            TextView tvExtra = card.findViewById(R.id.tvSavedItemExtra);
            TextView tvTitle = card.findViewById(R.id.tvSavedItemTitle);
            TextView tvContent = card.findViewById(R.id.tvSavedItemContent);
            ImageView btnRemove = card.findViewById(R.id.btnRemoveSavedItem);

            if (tvType != null) {
                String typeDisplay;
                switch (item.getType()) {
                    case "FLASHCARD":
                        typeDisplay = "Flashcard";
                        break;
                    case "MCQ":
                        typeDisplay = "MCQ / Practice";
                        break;
                    case "ACTIVE_RECALL":
                        typeDisplay = "Active Recall";
                        break;
                    case "NOTE":
                        typeDisplay = "Saved Note";
                        break;
                    default:
                        typeDisplay = item.getType();
                        break;
                }
                tvType.setText(typeDisplay);
            }

            if (tvExtra != null) {
                tvExtra.setText(item.getExtraInfo() != null ? item.getExtraInfo() : "HSC Biology");
            }

            if (tvTitle != null) {
                tvTitle.setText(item.getTitle() != null ? item.getTitle() : "Untitled Item");
            }

            if (tvContent != null) {
                tvContent.setText(item.getContent() != null ? item.getContent() : "");
            }

            // Remove/Unsave item handler
            if (btnRemove != null) {
                btnRemove.setOnClickListener(v -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.savedItemDao().delete(item);
                        runOnUiThread(this::loadSavedItems);
                    });
                });
            }

            // Card click: open corresponding module
            card.setOnClickListener(v -> {
                Intent targetIntent;
                switch (item.getType()) {
                    case "FLASHCARD":
                        targetIntent = new Intent(SavedItemsActivity.this, ChapterActivity.class);
                        targetIntent.putExtra(ChapterActivity.EXTRA_MODE, ChapterActivity.MODE_FLASHCARDS);
                        break;
                    case "MCQ":
                        targetIntent = new Intent(SavedItemsActivity.this, ChapterActivity.class);
                        targetIntent.putExtra(ChapterActivity.EXTRA_MODE, ChapterActivity.MODE_EXAM);
                        break;
                    case "ACTIVE_RECALL":
                        targetIntent = new Intent(SavedItemsActivity.this, ChapterActivity.class);
                        targetIntent.putExtra(ChapterActivity.EXTRA_MODE, ChapterActivity.MODE_ACTIVE_RECALL);
                        break;
                    case "NOTE":
                        targetIntent = new Intent(SavedItemsActivity.this, NotesActivity.class);
                        break;
                    default:
                        targetIntent = new Intent(SavedItemsActivity.this, ChapterActivity.class);
                        break;
                }
                startActivity(targetIntent);
            });

            layoutSavedItemsContainer.addView(card);
        }
    }

    private void setupNavigation() {
        View navHome = findViewById(R.id.navTabHome);
        if (navHome != null) navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        View navStudy = findViewById(R.id.navTabStudy);
        if (navStudy != null) navStudy.setOnClickListener(v -> {
            startActivity(new Intent(this, ChapterActivity.class));
            finish();
        });

        View navSaved = findViewById(R.id.navTabSaved);
        if (navSaved != null) navSaved.setOnClickListener(v -> {});

        View navProfile = findViewById(R.id.navTabProfile);
        if (navProfile != null) navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }
}

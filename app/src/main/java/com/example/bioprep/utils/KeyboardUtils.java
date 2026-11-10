package com.example.bioprep.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

/**
 * Utility for handling software keyboard visibility, layout resizing,
 * and automatic scrolling for accessible input fields.
 */
public class KeyboardUtils {

    public interface KeyboardVisibilityListener {
        void onKeyboardVisibilityChanged(boolean isVisible);
    }

    /**
     * Attaches a listener to detect when the software keyboard opens and closes.
     */
    public static void attachKeyboardListener(Activity activity, View rootLayout, KeyboardVisibilityListener listener) {
        if (activity == null || rootLayout == null || listener == null) return;

        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasKeyboardVisible = false;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                // Threshold: > 15% of screen height implies soft keyboard is visible
                boolean isKeyboardNowVisible = keypadHeight > (screenHeight * 0.15);

                if (isKeyboardNowVisible != wasKeyboardVisible) {
                    wasKeyboardVisible = isKeyboardNowVisible;
                    listener.onKeyboardVisibilityChanged(isKeyboardNowVisible);
                }
            }
        });
    }

    /**
     * Automatically scrolls the ScrollView to bring the target input into view
     * with extra bottom padding whenever focused or clicked.
     */
    public static void autoScrollOnFocus(final ScrollView scrollView, final View... inputViews) {
        if (scrollView == null || inputViews == null) return;

        for (final View view : inputViews) {
            if (view == null) continue;

            final View.OnFocusChangeListener existing = view.getOnFocusChangeListener();
            view.setOnFocusChangeListener((v, hasFocus) -> {
                if (existing != null) existing.onFocusChange(v, hasFocus);
                if (hasFocus) {
                    scrollToView(scrollView, v);
                }
            });

            view.setOnClickListener(v -> scrollToView(scrollView, v));
        }
    }

    /**
     * Smoothly scrolls the ScrollView so that the target view and immediate controls
     * below it are completely visible above the keyboard.
     */
    public static void scrollToView(final ScrollView scrollView, final View view) {
        if (scrollView == null || view == null) return;
        view.postDelayed(() -> {
            Rect rect = new Rect();
            view.getDrawingRect(rect);
            // Add generous bottom offset (140px) so buttons or helper text below remain visible
            rect.bottom += 140;
            view.requestRectangleOnScreen(rect, true);
        }, 220);
    }

    /**
     * Hides the software keyboard from the current focused view.
     */
    public static void hideKeyboard(Activity activity) {
        if (activity == null) return;
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}


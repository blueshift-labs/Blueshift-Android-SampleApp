package com.blueshift.reads.framework;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Helper class to implement edge-to-edge display for Android apps
 * Designed to be compatible with Android 15's edge-to-edge concept
 */
public class EdgeToEdgeHelper {

    /**
     * Configures the activity for edge-to-edge display
     * @param activity The activity to configure
     */
    public static void setupEdgeToEdge(@NonNull Activity activity) {
        // Make the content appear behind the system bars
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        
        // For clarity on light backgrounds
        int lightStatusBar = activity.getWindow().getDecorView().getSystemUiVisibility() | 
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        activity.getWindow().getDecorView().setSystemUiVisibility(lightStatusBar);
        
        // Make status and navigation bars transparent
        activity.getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
    }
    
    /**
     * Applies insets to the specified root view to prevent content from being obscured
     * by system bars (status bar, navigation bar)
     * @param rootView The root view of the layout
     */
    public static void applyWindowInsets(@NonNull View rootView) {
        // Apply window insets correctly to avoid content being hidden under system bars
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, windowInsets) -> {
            int topInset = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int bottomInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            int leftInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).left;
            int rightInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).right;
            
            // Get current padding
            int paddingLeft = view.getPaddingLeft();
            int paddingTop = view.getPaddingTop();
            int paddingRight = view.getPaddingRight();
            int paddingBottom = view.getPaddingBottom();
            
            // Add system insets to existing padding
            view.setPadding(
                    paddingLeft + leftInset,
                    paddingTop + topInset,
                    paddingRight + rightInset,
                    paddingBottom + bottomInset
            );
            
            return WindowInsetsCompat.CONSUMED;
        });
    }
    
    /**
     * Applies insets to the specified root view preserving its original padding
     * @param rootView The root view of the layout
     * @param preserveOriginalPadding Whether to preserve the original padding values
     */
    public static void applyWindowInsets(@NonNull View rootView, boolean preserveOriginalPadding) {
        if (!preserveOriginalPadding) {
            applyWindowInsets(rootView);
            return;
        }
        
        // Store original padding
        final int originalPaddingLeft = rootView.getPaddingLeft();
        final int originalPaddingTop = rootView.getPaddingTop();
        final int originalPaddingRight = rootView.getPaddingRight();
        final int originalPaddingBottom = rootView.getPaddingBottom();
        
        // Apply window insets correctly to avoid content being hidden under system bars
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsets) {
                int topInset = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
                int bottomInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                int leftInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).left;
                int rightInset = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).right;
                
                // Apply original padding plus insets
                view.setPadding(
                        originalPaddingLeft + leftInset,
                        originalPaddingTop + topInset,
                        originalPaddingRight + rightInset,
                        originalPaddingBottom + bottomInset
                );
                
                return WindowInsetsCompat.CONSUMED;
            }
        });
    }
}
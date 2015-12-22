package org.anibyl.slounik.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Slounik's progress bar.
 * <p/>
 * Created by Usievaład Kimajeŭ on 21.12.15.
 */
public class ProgressBar extends SmoothProgressBar {
    private boolean visible;

    public ProgressBar(Context context) {
        super(context);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void progressiveStart() {
        visible = true;
        super.progressiveStart();
    }

    @Override
    public void progressiveStop() {
        visible = false;
        super.progressiveStop();
    }

    private void init() {
        setVisibility(View.INVISIBLE);

        setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
            @Override
            public void onStop() {
                if (!visible) {
                    setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onStart() {
                if (visible) {
                    setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
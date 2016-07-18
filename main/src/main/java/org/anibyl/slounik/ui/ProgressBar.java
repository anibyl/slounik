package org.anibyl.slounik.ui;

import android.content.Context;
import android.util.AttributeSet;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Slounik's progress bar.
 * <p/>
 * Created by Usievaład Kimajeŭ on 21.12.15.
 */
public class ProgressBar extends SmoothProgressBar {
    private boolean invisible;

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
        invisible = false;
        super.progressiveStart();
    }

    @Override
    public void progressiveStop() {
        invisible = true;
        super.progressiveStop();
    }

    private void init() {
        setVisibility(INVISIBLE);

        setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
            @Override
            public void onStop() {
                if (invisible) {
                    setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onStart() {
                if (!invisible) {
                    setVisibility(VISIBLE);
                }
            }
        });
    }
}
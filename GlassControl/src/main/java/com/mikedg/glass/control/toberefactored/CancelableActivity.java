package com.mikedg.glass.control.toberefactored;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.mikedg.glass.control.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Michael on 12/10/13.
 */
public abstract class CancelableActivity extends Activity {
    private static final int TIME_MS = 3000;
    private ProgressBar mProgress;
    private GestureDetector mDetector;
    private ObjectAnimator mAnimator;
    private GestureDetector mGestureDetector;
    private TimerTask mTask;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private ImageView mIconImageView;

    private String mCompletedTitle;
    private Drawable mCompletedIcon;
    private String mCompletedSubtitle;

    private String mInProgressTitle;
    private String mInProgressSubtitle;
    private Drawable mInProgressIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cancelable);

        mTitleTextView = (TextView) findViewById(R.id.title);
        mSubtitleTextView = (TextView) findViewById(R.id.subtitle);
        mIconImageView = (ImageView) findViewById(R.id.icon);

        changeViews(mInProgressTitle, mInProgressSubtitle, mInProgressIcon);

        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setMax(TIME_MS);
        mAnimator = ObjectAnimator.ofInt(mProgress, "progress", 0, TIME_MS);
        mAnimator.setDuration(TIME_MS).start();
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                confirm();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimator.removeAllListeners();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //FIXME: below isn't working, wtf
        mGestureDetector = new GestureDetector(this);
        //Create a base listener for generic gestures
        mGestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    confirm();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                    return true;
                }
                return false;
            }
        });

    }

    private void changeViews(String title, String subtitle, Drawable icon) {
        mTitleTextView.setText(title);
        mSubtitleTextView.setText(subtitle);
        if (icon == null) mIconImageView.setVisibility(View.GONE);
        else mIconImageView.setVisibility(View.VISIBLE);
        mIconImageView.setImageDrawable(icon);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        mGestureDetector.onMotionEvent(event);
        return true;
    }

    private void confirm() {
        mAnimator.cancel();
        mProgress.setProgress(TIME_MS);
        mProgress.setVisibility(View.GONE);

        playSuccess();
        onConfirmed();

        //Now show the completed section and wait before dismissing
        //After onConfirmed, so we can update text/icons
        changeViews(mCompletedTitle, mCompletedSubtitle, mCompletedIcon);

        cancelTimer();
        mTask = new TimerTask() {
            public void run() {
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(mTask, 2000);
    }

    //For when we finish cancel any timers
    private void cancelTimer() {
        if (mTask != null) mTask.cancel();
    }

    @Override
    public void onBackPressed() {
        dismiss();
        //Don't call super, so we dont get the sound built in, but we need ot call finish
        finish();
    }

    protected void dismiss() {
        mAnimator.cancel();
        playDismiss();
        cancelTimer();
        onDismissed();
    };

    public abstract void onConfirmed();
    public abstract void onDismissed();
//    public abstract void onDone();

    private void playSuccess() {
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        manager.playSoundEffect(Sounds.SUCCESS);
    }

    private void playDismiss() {
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        manager.playSoundEffect(Sounds.DISMISSED);
    }


    public void setInProgressTitle(String inProgressTitle) {
        this.mInProgressTitle = inProgressTitle;
    }

    public void setInProgressSubtitle(String inProgressSubtitle) {
        this.mInProgressSubtitle = inProgressSubtitle;
    }

    public void setInProgressIcon(Drawable inProgressIcon) {
        this.mInProgressIcon = inProgressIcon;
    }

    public void setCompletedTitle(String completedTitle) {
        this.mCompletedTitle = completedTitle;
    }

    public void setCompletedSubtitle(String completedSubtitle) {
        this.mCompletedSubtitle = completedSubtitle;
    }

    public void setCompletedIcon(Drawable completedIcon) {
        this.mCompletedIcon = completedIcon;
    }
}

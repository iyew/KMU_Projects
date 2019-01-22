package com.example.kth.httpstreaming;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class PlaybackInfoListener {
    @IntDef({State.INVALID, State.PLAYING, State.PAUSED, State.RESET, State.COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    @interface State {

        int INVALID = -1;
        int PLAYING = 0;
        int PAUSED = 1;
        int RESET = 2;
        int COMPLETED = 3;
    }

    void onDurationChanged(int duration) {
    }

    void onPositionChanged(int position) {
    }

    void onStateChanged(@State int state) {
    }

    void onPlaybackCompleted() {
    }
}

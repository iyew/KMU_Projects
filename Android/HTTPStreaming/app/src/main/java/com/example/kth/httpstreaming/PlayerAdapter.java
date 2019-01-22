package com.example.kth.httpstreaming;

public interface PlayerAdapter {
    void loadMedia(String mp3);

    void release();

    boolean isPlaying();

    void play();

    void reset();

    void pause();

    void initializeProgressCallback();

    void seekTo(int position);
}

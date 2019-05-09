// http://unikys.tistory.com/350
// https://developer.android.com/guide/topics/media/mediaplayer
// https://github.com/googlesamples/android-SimpleMediaPlayer/
package com.example.kth.httpstreaming;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.MalformedURLException;
import java.net.URL;

public class MusicPlayerAcitivty extends AppCompatActivity {
    PlayerAdapter playerAdapter;
    MusicPlayerHolder musicPlayerHolder;
    ImageTask imageTask;

    SeekBar seekBarAudio;
    ToggleButton togglePlaynPause;
    ImageButton buttonStop;
    TextView textTitle, textArtist;
    ImageView coverImage;

    boolean isSeeking = false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        togglePlaynPause = (ToggleButton)findViewById(R.id.toggle_playnpause);
        buttonStop = (ImageButton)findViewById(R.id.button_stop);
        textTitle = (TextView)findViewById(R.id.text_title);
        textArtist = (TextView)findViewById(R.id.text_artist);
        seekBarAudio = (SeekBar)findViewById(R.id.seekBar_audio);
        coverImage = (ImageView)findViewById(R.id.imageView_album);

        intent = getIntent();
        textTitle.setText(intent.getStringExtra("MUSIC_TITLE"));
        textArtist.setText(intent.getStringExtra("MUSIC_ARTIST"));

        imageTask = new ImageTask();

        togglePlaynPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    playerAdapter.play();
                } else {
                    playerAdapter.pause();
                }
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerAdapter.reset();
            }
        });

        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userSelectedPosition = 0;

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                playerAdapter.seekTo(userSelectedPosition);
            }
        });

        musicPlayerHolder = new MusicPlayerHolder(this);
        musicPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        playerAdapter = musicPlayerHolder;
        imageTask.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String mp3 = intent.getStringExtra("MUSIC_TITLE");
        String url = "http://192.168.219.105:8001/music/" + mp3 + ".mp3";
        playerAdapter.loadMedia(url);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isChangingConfigurations() && playerAdapter.isPlaying()) {

        } else {
            playerAdapter.release();
        }
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            seekBarAudio.setMax(duration);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void onPositionChanged(int position) {
            if (!isSeeking) {
                seekBarAudio.setProgress(position, true);
            }
        }

        @Override
        public void onStateChanged(@State int state) {
        }

        @Override
        public void onPlaybackCompleted() {
        }
    }

    public class ImageTask extends AsyncTask<Void, Void, Boolean> {
        private String imageName;
        private Bitmap bitmapCover;
        ImageTask() {
            imageName = intent.getStringExtra("MUSIC_TITLE");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("http://192.168.219.105:8001/image/" + imageName + ".jpg");
                bitmapCover = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success) {
                coverImage.setImageBitmap(bitmapCover);
            }
        }
    }
}

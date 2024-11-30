package com.example.mymusicapp;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    private TextView songTitle, tvCurrentTime, tvTotalDuration;
    private SeekBar seekBar;
    private Button btnPlayPause, btnStop, btnForward, btnRewind, btnNext, btnPrevious, btnBack;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private List<Song> songList;
    private int currentSongIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songTitle = findViewById(R.id.songTitle);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalDuration = findViewById(R.id.tvTotalDuration);
        seekBar = findViewById(R.id.seekBar);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);
        btnForward = findViewById(R.id.btnForward);
        btnRewind = findViewById(R.id.btnRewind);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnBack = findViewById(R.id.btnBack);

        String songsJson = getIntent().getStringExtra("songs");
        songList = new Gson().fromJson(songsJson, new TypeToken<List<Song>>() {}.getType());
        currentSongIndex = getIntent().getIntExtra("currentSongIndex", 0);

        mediaPlayer = new MediaPlayer();
        loadSong(currentSongIndex);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setText("Play");
            } else {
                mediaPlayer.start();
                btnPlayPause.setText("Pause");
                updateSeekBar();
            }
        });

        btnStop.setOnClickListener(v -> {
            mediaPlayer.stop();
            btnPlayPause.setText("Play");
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnForward.setOnClickListener(v -> mediaPlayer.seekTo(Math.min(mediaPlayer.getCurrentPosition() + 10000, mediaPlayer.getDuration())));

        btnRewind.setOnClickListener(v -> mediaPlayer.seekTo(Math.max(mediaPlayer.getCurrentPosition() - 10000, 0)));

        btnNext.setOnClickListener(v -> {
            if (currentSongIndex < songList.size() - 1) {
                currentSongIndex++;
                loadSong(currentSongIndex);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                loadSong(currentSongIndex);
            }
        });

        btnBack.setOnClickListener(v -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            finish();
        });
    }

    private void loadSong(int index) {
        Song song = songList.get(index);
        songTitle.setText(song.getTitle());
        mediaPlayer.reset();
        try {
            AssetFileDescriptor descriptor = getAssets().openFd(song.getFilePath());
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(mediaPlayer.getDuration());
        tvTotalDuration.setText(formatTime(mediaPlayer.getDuration()));
        tvCurrentTime.setText(formatTime(0));
        updateSeekBar();
    }

    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}

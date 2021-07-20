package com.example.apnainsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
VideoView videoView;
ProgressBar progressBar;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
     videoView = findViewById(R.id.videoView);
     progressBar = findViewById(R.id.progressBar2);

        progressBar.setVisibility(View.VISIBLE);
Intent intent = getIntent();
        String uri =intent.getStringExtra("videoURL") ;
        Uri uri2 = Uri.parse(uri);
        videoView.setVideoURI(uri2);
        if(videoView.getBufferPercentage() >= 0) {

        }

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.INVISIBLE);
                videoView.start();
                mp.setLooping(true);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();

    }
}


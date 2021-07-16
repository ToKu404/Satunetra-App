package com.example.satunetra.activities.registered;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.satunetra.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import pl.droidsonroids.gif.GifImageView;

public class RoomActivity extends AppCompatActivity implements View.OnTouchListener {

    private YouTubePlayerView ypvRoom;
    private GestureDetector mGestureDetector;
    private ConstraintLayout btnGesture;
    private ImageView ivPause;
    private GifImageView gifImageView;
    private YouTubePlayer player;
    private TextView roomName, titleInstruction;
    private int sesi = 1;
    private boolean isPlay = false;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        btnGesture = findViewById(R.id.btn_gestur_room);
        mGestureDetector = new GestureDetector(this, new GestureListener());

        ypvRoom = findViewById(R.id.ypv_room);
        btnGesture = findViewById(R.id.btn_gestur_room);
        ivPause = findViewById(R.id.iv_not_speech_chat);
        gifImageView = findViewById(R.id.iv_is_speech_chat);
        roomName = findViewById(R.id.tv_room_name);
        titleInstruction = findViewById(R.id.tv_room_title);

        type = getIntent().getStringExtra("type");
        if(type.equals("meditasi")){
            gifImageView.setImageResource(R.drawable.meditasi);
        }else if(type.equals("music")){
            gifImageView.setImageResource(R.drawable.music);
        }else if(type.equals("podcasts")){
            gifImageView.setImageResource(R.drawable.podcast);
        }

        getLifecycle().addObserver(ypvRoom);

        btnGesture.setOnTouchListener(this);

        ypvRoom.getPlayerUiController();

        ypvRoom.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                player = youTubePlayer;
                playVideo(getIntent().getStringExtra("link1"));
                titleInstruction.setText(getIntent().getStringExtra("title1"));
                super.onReady(youTubePlayer);
            }



            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, PlayerConstants.@NotNull PlayerState state)
            {
                if(state == PlayerConstants.PlayerState.ENDED){
                    if(sesi==1){
                        sesi++;
                        playVideo(getIntent().getStringExtra("link2"));
                        titleInstruction.setText(getIntent().getStringExtra("title2"));
                    }else{
                        onBackPressed();
                    }
                }
                super.onStateChange(youTubePlayer, state);
            }

        });
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId()==R.id.btn_gestur_room){
            System.out.println("ASTAGA");
            mGestureDetector.onTouchEvent(event);
        }
        return true;
    }


    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //pause
            if(player!=null){
                System.out.println("PAUSE");
               if(isPlay==true){
                   isPlay = false;
                   player.pause();
                   ivPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
               }else{
                   isPlay = true;
                   player.play();
                   ivPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
               }
            }
            return false;
        }
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            if(sesi==1){
                                sesi++;
                                playVideo(getIntent().getStringExtra("link2"));
                                titleInstruction.setText(getIntent().getStringExtra("title2"));
                            }else{
                                Toast.makeText(RoomActivity.this, "INI SESI TERAKHIR", Toast.LENGTH_SHORT).show();
                            }

                        }
                        result = true;
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onBackPressed();
            return super.onDoubleTap(e);
        }
    }

    private void playVideo(String link){
        roomName.setText("SESI "+ type.toUpperCase() + String.valueOf(sesi));
        player.pause();
        player.loadVideo(link,0);
        player.play();
        isPlay = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

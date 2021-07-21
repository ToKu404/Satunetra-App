package com.example.satunetra.activities.registered;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.satunetra.R;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity implements View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private ImageView ivPause;
    private MediaPlayer mediaPlayer;
    private ProgressBar pbRoom;
    private LinearLayout llRoom;
    private TextView titleInstruction;
    private int sesi;
    private ArrayList<String> links;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        sesi = 1;

        ConstraintLayout btnGesture = findViewById(R.id.btn_gestur_room);
        mGestureDetector = new GestureDetector(this, new GestureListener());

        btnGesture = findViewById(R.id.btn_gestur_room);
        ivPause = findViewById(R.id.iv_not_speech_chat);
        TextView roomName = findViewById(R.id.tv_room_name);
        titleInstruction = findViewById(R.id.tv_room_title);

        mediaPlayer = new MediaPlayer();

        llRoom = findViewById(R.id.ll_voice_chat);
        pbRoom = findViewById(R.id.pb_room);

        pbRoom.setVisibility(View.VISIBLE);
        llRoom.setVisibility(View.GONE);

        links = getIntent().getStringArrayListExtra("link");
        String type = getIntent().getStringExtra("type");

        roomName.setText(type.toUpperCase());

        btnGesture.setOnTouchListener(this);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                pbRoom.setVisibility(View.GONE);
                llRoom.setVisibility(View.VISIBLE);
                playVideo();
            }},300);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(sesi<=links.size()){
                    sesi++;
                    playVideo();
                }else{
                    onBackPressed();
                }
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
            if(mediaPlayer!=null){
               if(mediaPlayer.isPlaying()){
                   mediaPlayer.pause();
                   ivPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
               }else{
                   mediaPlayer.start();
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
                            if(sesi<=links.size()){
                                sesi++;
                                playVideo();
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

    private void playVideo(){
        try {
            pbRoom.setVisibility(View.GONE);
            llRoom.setVisibility(View.VISIBLE);
            String link = links.get(sesi-1);
            titleInstruction.setText("SESI "+sesi);
            mediaPlayer.setDataSource(link);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.reset();
        mediaPlayer.stop();
        finish();
    }
}

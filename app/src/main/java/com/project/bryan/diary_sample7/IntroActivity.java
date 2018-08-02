package com.project.bryan.diary_sample7;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class IntroActivity extends AppCompatActivity {
    Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageView iv_intro = (ImageView)findViewById(R.id.imageView_of_Intro);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(iv_intro);
        Glide.with(this).load(R.drawable.intro_cal).into(gifImage);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
    // 4초뒤에 다음화면(MainActivity)으로 넘어가기 Handler 사용
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent); // 다음화면으로 넘어가기
            finish(); // Activity 화면 제거
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 다시 화면에 들어왔을 때 예약 걸어주기
        handler.postDelayed(r, 2500);
    }

    @Override
    protected void onPause() {
        super.onPause();
    // 화면을 벗어나면, handler 에 예약해놓은 작업을 취소하자
        handler.removeCallbacks(r); // 예약 취소
    }

}

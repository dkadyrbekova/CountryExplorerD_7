package com.example.countryexplorerd; // Твой новый пакет

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Убедись, что activity_splash.xml скопирован в res/layout
        setContentView(R.layout.activity_splash);

        // Ждем 2 секунды (2000 мс) и переходим в MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Закрываем Splash, чтобы не вернуться в него кнопкой "Назад"
        }, 2000);
    }
}
package com.elder.shopguide;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int OVERLAY_CODE = 1001;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.CHINESE);
                tts.setSpeechRate(0.65f);
            }
        });

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> startOverlay());

        // Auto-start if permission already granted
        if (canDrawOverlay()) {
            startOverlayService();
        }
    }

    private boolean canDrawOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    private void startOverlay() {
        if (!canDrawOverlay()) {
            // Speak guidance
            if (tts != null) {
                tts.speak(
                    "请允许悬浮窗权限，这样购物引导才能显示在其他APP上方。请点击确定，然后在设置页面打开开关。",
                    TextToSpeech.QUEUE_FLUSH, null, "guide");
            }

            TextView tip = findViewById(R.id.tipText);
            tip.setText("\uD83D\uDCA1 \u8BF7\u5728\u8BBE\u7F6E\u4E2D\u6253\u5F00\u201C\u60AC\u6D6E\u7A97\u201D\u5F00\u5173\uFF0C\u7136\u540E\u70B9\u201C\u5DF2\u5F00\u542F\u201D\u6309\u94AE");

            Intent intent = new Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName())
            );
            startActivityForResult(intent, OVERLAY_CODE);
        } else {
            startOverlayService();
        }
    }

    private void startOverlayService() {
        Intent intent = new Intent(this, GuideOverlayService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        finish(); // Close setup screen, overlay stays
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_CODE) {
            if (canDrawOverlay()) {
                startOverlayService();
            } else {
                TextView tip = findViewById(R.id.tipText);
                tip.setText("\u274C \u6CA1\u6709\u5F00\u542F\u60AC\u6D6E\u7A97\u6743\u9650\uFF0C\u8BF7\u91CD\u65B0\u70B9\u51FB\u201C\u5F00\u59CB\u5F15\u5BFC\u201D\u91CD\u8BD5");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}

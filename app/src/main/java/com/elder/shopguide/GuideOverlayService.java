package com.elder.shopguide;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class GuideOverlayService extends Service {
    private WindowManager wm;
    private ViewGroup bubbleView, cardView;
    private TextToSpeech tts;
    private GuideSession session;
    private WindowManager.LayoutParams bubbleParams, cardParams;
    private boolean isShowingCard = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Foreground notification (required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                "guide", "购物引导", NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
            startForeground(1, new Notification.Builder(this, "guide")
                .setContentTitle("\u94F6\u9F84\u6570\u5B57\u751F\u6D3B\u52A9\u624B")
                .setContentText("\u60AC\u6D6E\u5F15\u5BFC\u5DF2\u542F\u52A8")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build());
        }

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.CHINESE);
                tts.setSpeechRate(0.65f);
            }
        });

        // Default to shopping scene
        session = new GuideSession("shopping");
        createBubble();
    }

    // ===== 悬浮气泡 =====
    private void createBubble() {
        bubbleView = new LinearLayout(this);
        bubbleView.setLayoutParams(new LinearLayout.LayoutParams(
            dpToPx(56), dpToPx(56)));
        bubbleView.setBackgroundResource(android.R.drawable.ic_dialog_info);
        bubbleView.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        ((LinearLayout) bubbleView).setGravity(Gravity.CENTER);

        TextView tv = new TextView(this);
        tv.setText("\uD83D\uDCED");
        tv.setTextSize(28);
        bubbleView.addView(tv);

        bubbleParams = new WindowManager.LayoutParams(
            dpToPx(56), dpToPx(56),
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );
        bubbleParams.gravity = Gravity.TOP | Gravity.START;
        bubbleParams.x = dpToPx(20);
        bubbleParams.y = dpToPx(200);

        // Drag
        bubbleView.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: return true;
                case MotionEvent.ACTION_MOVE:
                    bubbleParams.x = (int) (e.getRawX() - bubbleView.getWidth()/2);
                    bubbleParams.y = (int) (e.getRawY() - bubbleView.getHeight()/2);
                    wm.updateViewLayout(bubbleView, bubbleParams);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (isShowingCard) hideCard();
                    else showCard();
                    return true;
            }
            return false;
        });

        wm.addView(bubbleView, bubbleParams);
    }

    // ===== 引导卡片 =====
    private void showCard() {
        if (cardView != null) return;

        cardView = (ViewGroup) LayoutInflater.from(this).inflate(
            R.layout.overlay_card, null);

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.88f);
        cardParams = new WindowManager.LayoutParams(
            width, WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );
        cardParams.gravity = Gravity.CENTER;

        updateCard();
        wm.addView(cardView, cardParams);
        isShowingCard = true;
    }

    private void updateCard() {
        if (cardView == null || session == null) return;
        GuideStep step = session.getCurrentStep();
        int cur = session.getCurrentIndex() + 1;
        int total = session.getTotalSteps();

        ((TextView) cardView.findViewById(R.id.cardTitle))
            .setText(session.getSceneName() + " \u2014 " + step.title);
        ((TextView) cardView.findViewById(R.id.cardStep))
            .setText("\u7B2C " + cur + " / " + total + " \u6B65\uFF1A" + step.subtitle);
        ((TextView) cardView.findViewById(R.id.cardText))
            .setText(step.text);
        ((TextView) cardView.findViewById(R.id.cardTip))
            .setText(step.tip);
        ((TextView) cardView.findViewById(R.id.cardProgress))
            .setText(progressBar(cur, total));

        Button prevBtn = cardView.findViewById(R.id.cardPrev);
        if (session.getCurrentIndex() > 0) {
            prevBtn.setVisibility(View.VISIBLE);
            prevBtn.setOnClickListener(v -> { session.prevStep(); updateCard(); speakStep(); });
        } else {
            prevBtn.setVisibility(View.GONE);
        }

        Button nextBtn = cardView.findViewById(R.id.cardNext);
        if (session.isLastStep()) {
            nextBtn.setText("\u2705 \u5B8C\u6210");
            nextBtn.setOnClickListener(v -> showDone());
        } else {
            nextBtn.setText("\u505A\u5B8C\u4E86\uFF0C\u4E0B\u4E00\u6B65 \u2192");
            nextBtn.setOnClickListener(v -> { session.nextStep(); updateCard(); speakStep(); });
        }

        Button repeatBtn = cardView.findViewById(R.id.cardRepeat);
        repeatBtn.setOnClickListener(v -> speakStep());

        Button closeBtn = cardView.findViewById(R.id.cardClose);
        closeBtn.setOnClickListener(v -> hideCard());

        speakStep();
    }

    private void speakStep() {
        if (tts == null || session == null) return;
        GuideStep step = session.getCurrentStep();
        if (step != null) {
            tts.speak(step.title + "\u3002" + step.text,
                TextToSpeech.QUEUE_FLUSH, null, "guide");
        }
    }

    private void showDone() {
        if (cardView == null) return;
        ((TextView) cardView.findViewById(R.id.cardTitle))
            .setText("\uD83C\uDF89 \u606D\u559C\u60A8\uFF01");
        ((TextView) cardView.findViewById(R.id.cardStep))
            .setText("\u60A8\u5DF2\u5B8C\u6210\u4E86\u5168\u90E8" + session.getTotalSteps() + "\u6B65");
        ((TextView) cardView.findViewById(R.id.cardText))
            .setText("\u4EE5\u540E\u505A\u8FD9\u4EF6\u4E8B\u5C31\u6309\u8FD9\u4E2A\u6B65\u9AA4\u6765\u3002\n\u9047\u5230\u95EE\u9898\u968F\u65F6\u53EF\u4EE5\u56DE\u6765\u590D\u4E60\u3002");
        ((TextView) cardView.findViewById(R.id.cardTip))
            .setText("\u611F\u8C22\u60A8\u7684\u4F7F\u7528\uFF0C\u795D\u60A8\u751F\u6D3B\u6109\u5FEB\uFF01");
        ((TextView) cardView.findViewById(R.id.cardProgress))
            .setText(progressBar(session.getTotalSteps(), session.getTotalSteps()));

        cardView.findViewById(R.id.cardPrev).setVisibility(View.GONE);
        ((Button) cardView.findViewById(R.id.cardNext)).setText("\u2190 \u91CD\u65B0\u5F00\u59CB");
        cardView.findViewById(R.id.cardNext).setOnClickListener(v -> {
            session = new GuideSession("shopping");
            updateCard();
        });
    }

    private void hideCard() {
        if (cardView != null) {
            wm.removeView(cardView);
            cardView = null;
        }
        isShowingCard = false;
        if (tts != null) tts.stop();
    }

    private String progressBar(int cur, int total) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<cur; i++) sb.append("\u2588");
        for (int i=cur; i<total; i++) sb.append("\u25A1");
        return sb.toString();
    }

    @Override
    public void onDestroy() {
        hideCard();
        if (bubbleView != null) wm.removeView(bubbleView);
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}

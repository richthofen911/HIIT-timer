package com.example.yichaoli.weartimer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends WearableActivity {

    private TextView mTextTime1;
    private TextView mTextTime2;
    private TextView mTextTime3;
    private TextView mTextTimeRemaining;

    private Button mBtnStart;

    private int selectedTime = 0;

    private Handler handler;

    private Timer timer;

    private Vibrator vibrator;

    private final long[] vibrationWavePattern = new long[]{200, 600};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        handler = new Handler();
        timer = new Timer();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mTextTime1 = findViewById(R.id.text_time_1);
        mTextTime2 = findViewById(R.id.text_time_2);
        mTextTime3 = findViewById(R.id.text_time_3);
        mTextTimeRemaining = findViewById(R.id.text_time_remaining);

        mBtnStart = findViewById(R.id.btn_start);

        setTimesSelections(1);
    }

    public void onTime1Select(View view) {
        setTimesSelections(1);
    }

    public void onTime2Select(View view) {
        setTimesSelections(2);
    }

    public void onTime3Select(View view) {
        setTimesSelections(3);
    }

    private void setTimesSelections(int selection) {
        Drawable red = getDrawable(R.drawable.rect_red_solid_corner);
        Drawable trans = getDrawable(R.drawable.rect_trans_solid_corner);
        switch (selection) {
            case 1:
                mTextTime1.setBackground(red);
                mTextTime2.setBackground(trans);
                mTextTime3.setBackground(trans);

                mTextTimeRemaining.setText("30");

                selectedTime = 1;
                break;
            case 2:
                mTextTime1.setBackground(trans);
                mTextTime2.setBackground(red);
                mTextTime3.setBackground(trans);

                mTextTimeRemaining.setText("60");

                selectedTime = 2;
                break;
            case 3:
                mTextTime1.setBackground(trans);
                mTextTime2.setBackground(trans);
                mTextTime3.setBackground(red);

                mTextTimeRemaining.setText("120");

                selectedTime = 3;
                break;
            default:
                break;
        }
    }

    public void onBtnClick(View view) {
        String currentBtnStatus = mBtnStart.getText().toString().toLowerCase();
        if (currentBtnStatus.equals("start")) {
            switch (selectedTime) {
                case 1:
                    updateTimeRemaining(30);
                    break;
                case 2:
                    updateTimeRemaining(60);
                    break;
                case 3:
                    updateTimeRemaining(120);
                    break;
                default:
                    break;
            }
        } else if (currentBtnStatus.equals("stop")) {
            timer.cancel();
            timer.purge();

            setTimesSelections(selectedTime);
            mBtnStart.setText(R.string.start);
        }
    }

    private void updateTimeRemaining(final int countdown) {
        final long targetTime = System.currentTimeMillis() / 1000 + countdown;
        mBtnStart.setText(R.string.stop);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long updatedTime = targetTime - System.currentTimeMillis() / 1000;
                        if (updatedTime >= 0) {
                            mTextTimeRemaining.setText(String.valueOf(updatedTime));
                        } else {
                            timer.cancel();
                            timer.purge();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createWaveform(vibrationWavePattern, 2));
                            } else {
                                vibrator.vibrate(1000);
                            }
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                            r.play();

                            setTimesSelections(selectedTime);
                            mBtnStart.setText(R.string.start);
                        }
                    }
                });
            }
        }, 1000, 1000);
    }
}

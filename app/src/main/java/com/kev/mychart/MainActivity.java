package com.kev.mychart;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.kevinnguyen.weekchartview.WeekChartView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private WeekChartView weekChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weekChartView = findViewById(R.id.week_chart_view);
        weekChartView.setAnimationEnable(true);
        weekChartView.setAmounts(new int[]{getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt()});
//        delaySetData();
    }

    private void delaySetData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weekChartView.setAmounts(new int[]{getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt(), getRandomInt()});
                delaySetData();
            }
        }, 4000);
    }

    private int getRandomInt() {
        Random r = new Random();
        int low = 0;
        int high = 10;
        return r.nextInt(high - low) + low;
    }

}

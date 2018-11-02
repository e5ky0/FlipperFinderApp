package com.pinmyballs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.pinmyballs.fragment.FragmentDialogScore;
import com.pinmyballs.fragment.FragmentScoreFlipper;
import com.pinmyballs.metier.Flipper;
import com.pinmyballs.metier.Score;
import com.pinmyballs.service.ScoreService;
import com.pinmyballs.utils.ListeScoresAdapter;

import java.util.ArrayList;

public class PopLegend extends AppCompatActivity {

    private static final String TAG = "PopLegend";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.pop_legend);

        //Adjust Popup size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.55));
    }
}






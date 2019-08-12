package com.horizon.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.horizon.contentframe.ContentActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentActivity.Start(this, FragmentSample.newInstance());
    }
}

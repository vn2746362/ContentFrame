package com.horizon.sample;

import android.util.Log;

import com.horizon.contentframe.ContentFragment;

public class FragmentSample extends ContentFragment {

    public static FragmentSample newInstance(){
        return new FragmentSample();
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_sample;
    }
}

package com.horizon.contentframe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.horizon.contentframe.Handlers.VoidCallHandler;


/*by Horizon*/
public abstract class ContentFragment extends Fragment implements ReferenceDisposer {
    protected final String TAG = this.getClass().getSimpleName();

    public ContentActivity activity(){
        return (ContentActivity) getContext();
    }

    //
    protected VoidCallHandler backPressedHandler;
    public VoidCallHandler getBackPressedHandler(){ return backPressedHandler; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity().getWindow().setSoftInputMode(initSoftInputMode());
        try {
            return inflater.inflate(initLayout(), container, false);
        } catch (Exception e){
            Log.e(TAG, "[!!!] Failed init layout with Exception: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Release references
        backPressedHandler = null;

        //Dispose all attributes
        dispose();
    }

    public int initSoftInputMode(){
        return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
    }
    public abstract int initLayout();

    //
    public boolean isSafeFragment(){
        return !(isRemoving() || isDetached() || !isAdded() || getActivity() == null || getView() == null);
    }
}


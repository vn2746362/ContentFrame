package com.horizon.contentframe.Handlers;

import android.content.Intent;

/*By Horizon*/
public interface ActivityResultHandler {
    void onResult(int requestCode, int resultCode, Intent data);
}

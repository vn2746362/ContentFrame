package com.horizon.contentframe.Handlers;

/*By Horizon*/
public interface PermissionResultHandler {
    void onGranted(String pem);
    void onDenied(String pem);
}

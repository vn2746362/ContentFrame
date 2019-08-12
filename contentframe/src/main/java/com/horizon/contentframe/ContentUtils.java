package com.horizon.contentframe;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListPopupWindow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Method;
import java.util.List;

/*By Horizon*/
public class ContentUtils {
    public static void launchApp(Context context, String packageName, Bundle bundle){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            // We found the activity now start the activity

            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(intent);
        } else {
            //open on store
            try {
                // Bring user to the market or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.setData(Uri.parse("market://details?id=" + packageName));
                context.startActivity(intent);
            } catch ( final Exception e ) {
                // Bring user to play store.
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                context.startActivity(intent);
            }

        }
    }

    public static boolean hasHardwareAcceleration(AppCompatActivity activity) {
        // Has HW acceleration been enabled manually in the current window?
        Window window = activity.getWindow();
        if (window != null) {
            if ((window.getAttributes().flags
                    & WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED) != 0) {
                return true;
            }
        }

        // Has HW acceleration been enabled in the manifest?
        try {
            ActivityInfo info = activity.getPackageManager().getActivityInfo(
                    activity.getComponentName(), 0);
            if ((info.flags & ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Chrome", "getActivityInfo(self) should not fail");
        }
        return false;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }

    public static void compatibleSetWindowLayoutType(final ListPopupWindow listPopupWindow, int layoutType) {
        Method[] methods = ListPopupWindow.class.getMethods();
        for(Method m: methods){
            if(m.getName().equals("setWindowLayoutType")) {
                try{
                    m.invoke(listPopupWindow, layoutType);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    //Send result to Fragment
    public static void startPickFile(ContentFragment fragment, String type, String[] mimeTypes, int code){
        //Create an Intent with action
        //ACTION_GET_CONTENT for any type of file
        //ACTION_PICK for media with mimeTypes
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if(mimeTypes != null)
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Launching the Intent
        fragment.startActivityForResult(intent, code);
    }

    //Send result to Activity ONLY
    public static void startPickFile(ContentActivity activity, String type, String[] mimeTypes, int code){
        //Create an Intent with action
        //ACTION_GET_CONTENT for any type of file
        //ACTION_PICK for media with mimeTypes
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if(mimeTypes != null)
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Launching the Intent
        activity.startActivityForResult(intent, code);
    }
}

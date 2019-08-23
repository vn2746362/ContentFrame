package com.horizon.contentframe;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.horizon.contentframe.Handlers.ActivityResultHandler;
import com.horizon.contentframe.Handlers.PermissionResultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/*By Horizon*/
public class ContentActivity extends AppCompatActivity {
    private final String TAG = ContentActivity.class.getSimpleName();

    //CODE
    private final static int CODE_REQUEST_PERMISSION = 0x128;

    //Entry Activity
    public static ContentActivity getEntryActivity(){
        if(openingActivities.size() == 0 ) return null;
        return openingActivities.peek();
    }

    //Opening Activities
    private static Stack<ContentActivity> openingActivities = new Stack<>();
    public static int getOpeningActivityCount(){
        return openingActivities.size();
    }

    //Fragment
    private static ContentFragment prepareFragment = null;


    //
    public ActivityResultHandler activityResultHandler;

    //Permissions
    private PermissionResultHandler permissionResultHandler;

    //Fragment Control
    private FragmentManager fragmentManager;

    public static void Start(Context context, ContentFragment prepareFragment) {
        ContentActivity.prepareFragment = prepareFragment;

        //
        Intent intent = new Intent(context, ContentActivity.class);
        context.startActivity(intent);
    }

    public static void StartForResult(Context context, ContentFragment prepareFragment, int requestCode) {
        ContentActivity.prepareFragment = prepareFragment;

        //
        Intent intent = new Intent(context, ContentActivity.class);
        ((ContentActivity)context).startActivityForResult(intent, requestCode);
    }

    public ContentActivity() {
        super();

        //Push to list
        openingActivities.push(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        //overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        //Set UI Container
        setContentView(R.layout.activity_content);

        //Put Fragment to UI
        fragmentManager = getSupportFragmentManager();
        try {
            String fragName = prepareFragment.getClass().getSimpleName()+"@"+UUID.randomUUID().toString();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, prepareFragment, fragName);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Release
            prepareFragment = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //Release References
        activityResultHandler   = null;
        fragmentManager         = null;

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Fragment f = getVisibleFragment();
        if(f == null){
            super.onBackPressed();
        }else{
            if(f instanceof ContentFragment){
                ContentFragment cf = (ContentFragment) f;
                if(cf.getBackPressedHandler() == null) super.onBackPressed();
                else cf.getBackPressedHandler().onCall();
            }
            else super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(activityResultHandler != null) activityResultHandler.onResult(requestCode, resultCode, data);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CODE_REQUEST_PERMISSION && permissionResultHandler != null){
            int size = permissions.length;
            for(int i=0; i<size;i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    permissionResultHandler.onGranted(permissions[i]);
                else
                    permissionResultHandler.onDenied(permissions[i]);
            }
        }
    }

    @Override
    public void finish() {
        openingActivities.remove(this);
        super.finish();
    }

    //region Super Methods
    public void superBackPressed(){
        super.onBackPressed();
    }
	
    public void superFinish(){
        super.finish();
    }
    //endregion

    public void finishAllActivitiesButThis(){
        ContentActivity currentActivity = null;
        while(!openingActivities.empty()){
            ContentActivity activity = openingActivities.pop();
            if(activity.equals(this)) currentActivity = activity;
            else activity.superFinish();
        }

        //
        if(currentActivity != null) openingActivities.push(currentActivity);
    }

    //region Fragment methods
    private Fragment getVisibleFragment(){
        Fragment f = null;
        for(Fragment fragment:fragmentManager.getFragments()){
            if(fragment.isVisible()){
                f = fragment;
                break;
            }
        }
        return f;
    }

    public void stackFragment(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        String tag = fragment.getClass().getName();
        transaction.add(R.id.container, fragment, tag);
        if(addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void addFragment(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment visibleFragment = getVisibleFragment();
        String tag = fragment.getClass().getName();
        if(visibleFragment != null) {
            transaction.hide(visibleFragment);
            transaction.add(R.id.container, fragment, tag);
        }else transaction.replace(R.id.container, fragment, tag);
        if(addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void addFragmentWithAnimation(Fragment fragment, int[] animations, boolean addToBackStack){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String tag = fragment.getClass().getName();
        if(animations == null)
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else transaction.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);
        Fragment visibleFragment = getVisibleFragment();
        if(visibleFragment != null) {
            transaction.hide(visibleFragment);
            transaction.add(R.id.container, fragment, tag);
        }else transaction.replace(R.id.container, fragment, tag);
        if(addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String tag = fragment.getClass().getName();
        transaction.replace(R.id.container, fragment, tag);
        if(addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void replaceFragmentWithAnimation(Fragment fragment, int[] animations, boolean addToBackStack){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String tag = fragment.getClass().getName();
        if(animations == null)
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else transaction.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);
        transaction.replace(R.id.container, fragment, tag);
        if(addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void popBackFragment(){
        fragmentManager.popBackStack();
    }
    //endregion

    //region Permission
    public void checkPermission(String[] perms, PermissionResultHandler permissionResultHandler){
        this.permissionResultHandler = permissionResultHandler;

        //Must request for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> noGrantList = new ArrayList<>();
            List<String> grantedList = new ArrayList<>();
            for(String perm:perms) {
                if (checkCallingOrSelfPermission(perm) != PackageManager.PERMISSION_GRANTED)
                    noGrantList.add(perm);
                else grantedList.add(perm);
            }

            if(noGrantList.size() > 0){
                requestPermissions(noGrantList.toArray(new String[]{}), CODE_REQUEST_PERMISSION);
            }else{
                if(permissionResultHandler != null){
                    for(String perm:grantedList) permissionResultHandler.onGranted(perm);
                }
            }
        }

        //Permission included in Manifest
        else {
            if(permissionResultHandler != null){
                for(String perm:perms) permissionResultHandler.onGranted(perm);
            }
        }
    }
    //endregion
}

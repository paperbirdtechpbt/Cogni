package com.pbt.cogni.fragment.Current;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.pbt.cogni.util.AppUtils;

import java.util.List;

public class BroadClassRecicver  extends BroadcastReceiver {

    String TAG="##BroadClassRecicver";
        @Override
    public void onReceive(Context context, Intent intent) {


        Log.e(TAG,"On Recicve");

        System.out.println("Service Reciver Called");
//
        if (isMyServiceRunning(context, GPSTracker.class)) {
            Log.e(TAG,"Service running no need to start again");
//            System.out.println("Service running no need to start again");
        }
        else {

            Intent background = new Intent(context, GPSTracker.class);
//            context.startService(background);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(background);
            }
            else {
                context.startService(background);
            }
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (services != null) {
            for (int i = 0; i < services.size(); i++) {
                if ((serviceClass.getName()).equals(services.get(i).service.getClassName()) && services.get(i).pid != 0) {
                    return true;
                }
            }
        }
        return false;
    }
}

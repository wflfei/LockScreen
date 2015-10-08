package com.wfl.lockscreen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class GrdService extends Service {
    KeyguardManager keyguardManager = null;
    KeyguardManager.KeyguardLock keyguardLock = null;
    boolean registered = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("GrdService", "Action: " + intent.getAction());
            Intent mainIntent = new Intent(context, ScreenActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    };

    public GrdService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!registered) {
            keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            keyguardLock = keyguardManager.newKeyguardLock("");
            keyguardLock.disableKeyguard();
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            //intentFilter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(broadcastReceiver, intentFilter);
            Log.v("GrdService", "Register Receiver");
            registered = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        registered = false;
        super.onDestroy();
        startService(new Intent(this, GrdService.class));
    }
}

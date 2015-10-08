package com.wfl.lockscreen;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends BroadcastReceiver {

    public LockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            System.out.println("StandardBroadcastReceiver...");
            //解锁
            KeyguardManager km = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kk = km.newKeyguardLock("");
            kk.disableKeyguard();
            //启动服务
            Intent service=new Intent();
            service.setClass(context,GrdService.class);//稍后再定义
            context.startService(service);
            /*Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(mainIntent);*/
        } else {
            //启动服务
            Intent service=new Intent();
            service.setClass(context,GrdService.class);//稍后再定义
            context.startService(service);
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }
}

package com.v2v.fitnesshub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("msg");

        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("msg", msg);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}

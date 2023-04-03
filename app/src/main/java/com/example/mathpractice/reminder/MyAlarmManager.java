package com.example.mathpractice.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyAlarmManager {
	public static final int ALARM_REQUEST_CODE = 234324243;
	public static void setAlarm(Activity activity, int minutes){
		Intent intent = new Intent(activity, MyBroadcastReceiver.class);
		@SuppressLint("UnspecifiedImmutableFlag")
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				activity.getApplicationContext(), ALARM_REQUEST_CODE, intent, 0);
		AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (minutes * 60_000L), pendingIntent);
		Toast.makeText(activity, "Alarm set in " + minutes + " minutes",Toast.LENGTH_LONG).show();
	}
}

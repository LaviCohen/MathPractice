package com.example.mathpractice.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Static utility class that handles the {@link AlarmManager} usage, to create reminders.
 * */
public class MyAlarmManager {

	/**
	 * The code for alarm requests.
	 * */
	public static final int ALARM_REQUEST_CODE = 1777;

	/**
	 * This method sets reminder alarm to post reminder notification.
	 * @param activity the current activity.
	 * @param minutes the number of minutes to remind in.
	 * */
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

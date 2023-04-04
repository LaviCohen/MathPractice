package com.example.mathpractice.reminder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;

/**
 * Subclass of {@link BroadcastReceiver}, used to receive alarm broadcast and invoke {@link MyNotificationManager}'s
 * postNotification method and to make {@link Toast}.
 * */
public class MyBroadcastReceiver extends BroadcastReceiver {

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("active");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			Intent notificationIntent = new Intent(context, PracticeActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			MyNotificationManager.postNotification(context, R.drawable.ic_back,
					"Return", "Return to practice... ( ;", notificationIntent);
		}
		Toast.makeText(context, "Return to practice....",Toast.LENGTH_LONG).show();
	}
}

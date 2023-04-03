package com.example.mathpractice.reminder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.mathpractice.R;
import com.example.mathpractice.reminder.MyNotificationManager;
import com.example.mathpractice.reminder.ReminderService;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("active");
		MyNotificationManager.postNotification(context, R.drawable.ic_back,
				"Return", "Return to practice... ( ;");
		Toast.makeText(context, "Return to practice....",Toast.LENGTH_LONG).show();
	}
}

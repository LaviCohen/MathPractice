package com.example.mathpractice.broadcasts;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mathpractice.R;
import com.example.mathpractice.notifications.MyNotificationManager;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getBooleanExtra("state", false)) {
			MyNotificationManager.postNotification(context, R.drawable.ic_leaderboard, "Chance is here",
					"Go offline to practice math, ha??");
		}
	}
}

package com.example.mathpractice.service;

import android.app.Notification;
import android.app.Service;
import android.content.ContextParams;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.mathpractice.R;
import com.example.mathpractice.notifications.MyNotificationManager;

public class ReminderService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(() -> {
			try {
				Thread.sleep(1000*2*5);
				MyNotificationManager.postNotification(ReminderService.this, R.drawable.ic_back,
						"Return", "Return to practice... (;");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stopSelf();
		}).start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

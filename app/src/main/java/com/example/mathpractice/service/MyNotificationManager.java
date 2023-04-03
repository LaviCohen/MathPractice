package com.example.mathpractice.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyNotificationManager {

	private static int id = 0;

	private static boolean inited = false;
	private static final String CHANNEL_ID = "MyChannelID";

	public static void init(Context context){
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			CharSequence name = "MyChannel";
			String description = "My Description";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = null;
			channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
		inited = true;
	}

	public static void postNotification(Context context, int icon, String  title, String content){
		if (!inited) {
			init(context);
		}
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(content)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify(id++, builder.build());
	}
}

package com.example.mathpractice.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Static utility class that handles the {@link NotificationManager} usage, to postNotifications.
 * */
public class MyNotificationManager {

	/**
	 * Static notification ID counter.
	 * */
	private static int id = 0;

	/**
	 * Is that the first notification? if true, init the class and became false. used once per app running.
	 * */
	private static boolean firstNotification = true;

	/**
	 * The notification channel ID.
	 * */
	private static final String CHANNEL_ID = "MyChannelID";

	/**
	 * This method initializes this class utility, create all needed such as channel etc.
	 * @param context current active context.
	 * */
	public static void init(Context context){
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			CharSequence name = "MyChannel";
			String description = "My Description";
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
		firstNotification = false;
	}

	/**
	 * This method post notification as required, and init the class if needed.
	 * @param context current active context.
	 * @param icon the notification's icon.
	 * @param title the notification's title.
	 * @param content the notification's content.
	 * @param intent the intent which will be passed when the user click on the notification.
	 * */
	public static void postNotification(Context context, int icon, String  title, String content, Intent intent){
		if (firstNotification) {
			init(context);
		}
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(content)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
			builder.setContentIntent(pendingIntent);
		}
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify(id++, builder.build());
	}
}

package com.example.mathpractice.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mathpractice.activities.practice.PracticeActivity;

public class MyNotificationManager {

	private static int id = 0;

	private static boolean inited = false;
	private static final String CHANNEL_ID = "MyChannelID";

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
		inited = true;
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public static void postNotification(Context context, int icon, String  title, String content){
		if (!inited) {
			init(context);
		}
		Intent intent = new Intent(context, PracticeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(content)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setContentIntent(pendingIntent);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		notificationManager.notify(id++, builder.build());
	}
}

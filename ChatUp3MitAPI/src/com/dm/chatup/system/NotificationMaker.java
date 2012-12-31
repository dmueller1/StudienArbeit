package com.dm.chatup.system;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

public class NotificationMaker {
	
	public static Class<?> actualClass = null;
	
	@SuppressWarnings("deprecation")
	public static Notification makeNotification(String text, Activity quellKlasse, Class<?> zielKlasse) {
		Notification notification = new Notification(android.R.drawable.stat_notify_sync, text, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledOnMS=2000;
		notification.ledOffMS=1000;
		Intent i = new Intent(quellKlasse.getApplicationContext(), zielKlasse);
		PendingIntent contentIntent = PendingIntent.getActivity(quellKlasse.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(quellKlasse, "NotificationActivity", text, contentIntent);
		return notification;
	}

	@SuppressWarnings("deprecation")
	public static Notification makeNotification(String text, Activity quellKlasse, Class<?> zielKlasse, int chatID) {
		Notification notification = new Notification(android.R.drawable.stat_notify_sync, text, System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.ledOnMS=2000;
		notification.ledOffMS=1000;
		Intent i = new Intent(quellKlasse.getApplicationContext(), zielKlasse);
		i.putExtra("chatID", chatID);
		PendingIntent contentIntent = PendingIntent.getActivity(quellKlasse.getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(quellKlasse, "NotificationActivity", text, contentIntent);
		return notification;
	}

}

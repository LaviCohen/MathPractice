package com.example.mathpractice.schedule;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;

import java.util.Calendar;

public class Scheduler {

	public static class MyCalendar{

		public String calName;
		public int calID;
		public MyCalendar(String calName, int calID) {
			this.calName = calName;
			this.calID = calID;
		}

		@Override
		public String toString() {
			return "MyCalendar{" +
					"calName='" + calName + '\'' +
					", calID='" + calID + '\'' +
					'}';
		}
	}

	public static void addEvent(int calID, Context context){
		long startMillis = 0;
		long endMillis = 0;
		Calendar beginTime = Calendar.getInstance();
		beginTime.set(2022, 10, 25, 7, 30);
		startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.set(2022, 10, 25, 8, 45);
		endMillis = endTime.getTimeInMillis();

		ContentResolver cr = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CalendarContract.Events.DTSTART, startMillis);
		values.put(CalendarContract.Events.DTEND, endMillis);
		values.put(CalendarContract.Events.TITLE, "Practice Goal");
		values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
		values.put(CalendarContract.Events.DESCRIPTION, "You should reach some point before this times comes!");
		values.put(CalendarContract.Events.CALENDAR_ID, 1);
		Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

		long eventID = Long.parseLong(uri.getLastPathSegment());
		Toast.makeText(context, "Event " + eventID + " added", Toast.LENGTH_SHORT).show();
	}
	public static MyCalendar [] getCalendar(Context c) {

		String[] projection = {"_id", "calendar_displayName"};
		Uri calendars;
		calendars = Uri.parse("content://com.android.calendar/calendars");

		ContentResolver contentResolver = c.getContentResolver();
		Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

		MyCalendar[] m_calendars = null;

		if (managedCursor.moveToFirst()){
			m_calendars = new MyCalendar[managedCursor.getCount()];
			String calName;
			int calID;
			int cont= 0;
			int nameCol = managedCursor.getColumnIndex(projection[1]);
			int idCol = managedCursor.getColumnIndex(projection[0]);
			do {
				calName = managedCursor.getString(nameCol);
				calID = managedCursor.getInt(idCol);
				m_calendars[cont] = new MyCalendar(calName, calID);
				cont++;
			} while(managedCursor.moveToNext());
			managedCursor.close();
		}
		return m_calendars;
	}
}

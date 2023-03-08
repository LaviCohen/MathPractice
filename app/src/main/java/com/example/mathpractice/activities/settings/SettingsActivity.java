package com.example.mathpractice.activities.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setTitle("Settings");
		SettingsFragment sf = new SettingsFragment();
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.settings_container, sf)
				.commit();
	}
	public static int getUserGeneralLevel(Context context) {
		return getUserLevel(context, 0) + getUserLevel(context, 1) - 1;
	}
	public static int getUserLevel(Context context, int type) {
		String user = PreferenceManager.getDefaultSharedPreferences(context)
				.getString("user", "Local");
		if (user.equals("Local")) {
			return 1;
		}
		Cursor c = new DataBaseHelper(context).execSQLForReading("SELECT level_type_" + type +
				" FROM users WHERE username = '" + user + "';");
		c.moveToFirst();
		System.out.println("There are " + c.getColumnCount() + " columns & " + c.getCount() + " rows");
		@SuppressLint("Range") int level = c.getInt(c.getColumnIndex("level_type_" + type));
		c.close();
		return level;
	}
	@Override
	public void onBackPressed() {
		gotoPracticeActivity();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.back_menu_option) {
			gotoPracticeActivity();
		}
		return true;
	}

	public void gotoPracticeActivity(){
		Intent i = new Intent(SettingsActivity.this, PracticeActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.back_menu, menu);
		return true;
	}
}
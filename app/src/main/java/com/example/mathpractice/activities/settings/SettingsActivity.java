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
import com.example.mathpractice.sqlDataBase.PracticesHelper;

/**
 * This activity is the activity to show and change the settings for the current user.
 * Also used as static class for access current user's data.
 * */
public class SettingsActivity extends AppCompatActivity {

	/**
	 * OnCreate method of the screen, part of its life-cycle.
	 * Initialize the title and the settings fragment.
	 * @param savedInstanceState default android param.
	 */
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

	/**
	 * Getter for general user level, calculated from getUserLevel for each type minus 1.
	 * @param context context to pass to getUserLevel method.
	 * @return the user's general level.
	 */
	public static int getUserGeneralLevel(Context context) {
		return getUserLevel(context, 0) + getUserLevel(context, 1) - 1;
	}

	/**
	 * Getter for general user level, calculated from getUserLevel for each type minus 1.
	 * @param context context to pass to getUserLevel method.
	 * @param type type of practice which its level required, as specified in {@link com.example.mathpractice.math.AbstractPractice}.
	 * @return the user's level in the specified type.
	 */
	public static int getUserLevel(Context context, int type) {
		String user = PreferenceManager.getDefaultSharedPreferences(context)
				.getString("user", "Local");
		if (user.equals("Local")) {
			return 1;
		}
		Cursor c = new PracticesHelper(context).execSQLForReading("SELECT level_type_" + type +
				" FROM users WHERE username = '" + user + "';");
		c.moveToFirst();
		System.out.println("There are " + c.getColumnCount() + " columns & " + c.getCount() + " rows");
		@SuppressLint("Range") int level = c.getInt(c.getColumnIndex("level_type_" + type));
		c.close();
		return level;
	}

	/**
	 * Method to go back to practice activity when back navigation button is pressed.
	 * */
	@Override
	public void onBackPressed() {
		gotoPracticeActivity();
	}

	/**
	 * Method to go back to practice activity when upper menu back button is pressed.
	 * */
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.back_menu_option) {
			gotoPracticeActivity();
		}
		return true;
	}

	/**
	 * Method to go back to practice activity, called when back button is pressed.
	 * */
	public void gotoPracticeActivity(){
		Intent i = new Intent(SettingsActivity.this, PracticeActivity.class);
		startActivity(i);
		finish();
	}

	/**
	 * Inflates the menu into the activity.
	 * @param menu the activity menu to inflate into.
	 * @return true - if the menu was inflated successfully.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.back_menu, menu);
		return true;
	}
}
package com.example.mathpractice.activities.practice;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.fragments.AbstractPracticeFragment;
import com.example.mathpractice.activities.practice.fragments.MulTableFragment;
import com.example.mathpractice.activities.practice.fragments.TrinomFragment;
import com.example.mathpractice.activities.user.UserPageActivity;
import com.example.mathpractice.activities.scores.ScoresActivity;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.reminder.MyAlarmManager;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;
import com.example.mathpractice.sqlDataBase.PracticesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This activity is the frame of the practicing fragments, contains the menus and {@link android.widget.FrameLayout} container.
 * */
public class PracticeActivity extends AppCompatActivity {

	/**
	 * The {@link DataBaseHelper} object which will be used in this activity, created once on onCreate.
	 * */
	public PracticesHelper dataBase = null;

	/**
	 * The current {@link AbstractPracticeFragment} currently shown on the screen, either {@link MulTableFragment} or
	 * {@link TrinomFragment}.
	 * */
	public AbstractPracticeFragment currentFragment = null;

	/**
	 * OnCreate method of the screen, part of its life-cycle.
	 * @param savedInstanceState default android param.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);

		if (dataBase == null) {
			dataBase = new PracticesHelper(this);
		}

		Button remindMeLaterButton = findViewById(R.id.remind_me_later_button);

		remindMeLaterButton.setOnClickListener(view -> {
			Dialog d = new Dialog(PracticeActivity.this);
			d.setTitle("Pick Number");
			d.setContentView(R.layout.number_picker_alarm_dialog_layout);
			Button ok = d.findViewById(R.id.set_button);
			Button cancel = d.findViewById(R.id.cancel_button);
			NumberPicker np = d.findViewById(R.id.number_picker);
			np.setMinValue(1);
			np.setMaxValue(100);
			np.setValue(5);
			np.setWrapSelectorWheel(false);
			ok.setOnClickListener(v -> {
				MyAlarmManager.setAlarm(PracticeActivity.this, np.getValue());
				d.dismiss();
			});
			cancel.setOnClickListener(v -> d.dismiss());
			d.show();
		});

		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_practice_menu);

		bottomNavigationView.setOnItemSelectedListener(item -> {
			AbstractPracticeFragment selected = null;
			if (item.getItemId() == R.id.trinom_menu_option) {
				if (currentFragment != null && currentFragment instanceof TrinomFragment) {
					return true;
				}
				selected = new TrinomFragment();
			} else if (item.getItemId() == R.id.mul_table_menu_option) {
				if (currentFragment != null && currentFragment instanceof MulTableFragment) {
					return true;
				}
				selected = new MulTableFragment();
			}
			assert selected != null;
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
			currentFragment = selected;
			return true;
		});
		int fragmentID = getIntent().getIntExtra("practice", -1);
		if (fragmentID == -1) {
			fragmentID = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).
					getString("defaultPractice", "0")) == 0 ?
					R.id.trinom_menu_option : R.id.mul_table_menu_option;
		} else {
			fragmentID = fragmentID == 0 ? R.id.trinom_menu_option : R.id.mul_table_menu_option;
		}
		if (fragmentID == R.id.trinom_menu_option) {
			currentFragment = new TrinomFragment();
		} else {
			currentFragment = new MulTableFragment();
		}
		bottomNavigationView.setSelectedItemId(fragmentID);

		System.out.println(currentFragment);

		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
	}

	/**
	 * This method handle upper menu events, and go to the correct activity - {@link ScoresActivity}, {@link SettingsActivity}
	 *  and {@link UserPageActivity}.
	 * */
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.scores_menu_option) {
			Intent i = new Intent(this, ScoresActivity.class);
			int curType = -1;
			if (currentFragment instanceof TrinomFragment) curType = 0;
			else if (currentFragment instanceof MulTableFragment) curType = 1;
			i.putExtra("practice", curType);
			startActivity(i);
			finish();
		} else if (item.getItemId() == R.id.settings_menu_option) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			finish();
		} else if (item.getItemId() == R.id.user_menu_option) {
			Intent i = new Intent(this, UserPageActivity.class);
			startActivity(i);
			finish();
		}
		return true;
	}

	/**
	 * Method that inflates the upper menu for this activity.
	 * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.practice_activity_menu, menu);
		return true;
	}
}
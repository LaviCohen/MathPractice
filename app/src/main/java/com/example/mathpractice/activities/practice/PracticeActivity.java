package com.example.mathpractice.activities.practice;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.fragments.MulTableFragment;
import com.example.mathpractice.activities.practice.fragments.TrinomFragment;
import com.example.mathpractice.activities.user.UserPageActivity;
import com.example.mathpractice.activities.scores.ScoresActivity;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.service.ReminderService;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PracticeActivity extends AppCompatActivity {


	public DataBaseHelper dataBase = null;

	public Fragment currentFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);

		if (dataBase == null) {
			dataBase = new DataBaseHelper(this);
		}

		Button remindMeLaterButton = findViewById(R.id.remind_me_later_button);

		remindMeLaterButton.setOnClickListener(view -> {
			Intent serviceIntent = new Intent(PracticeActivity.this, ReminderService.class);
			startService(serviceIntent);
		});

		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_practice_menu);

		bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				Fragment selected = null;
				if (item.getItemId() == R.id.trinom_menu_option) {
					selected = new TrinomFragment();
				} else if (item.getItemId() == R.id.mul_table_menu_option) {
					selected = new MulTableFragment();
				}
				if (selected != currentFragment){
					getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selected).commit();
					currentFragment = selected;
				}
				return true;
			}
		});
		currentFragment = new TrinomFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.scores_menu_option) {
			Intent i = new Intent(this, ScoresActivity.class);
			int curType = -1;
			if (currentFragment instanceof TrinomFragment) curType = 0;
			else if (currentFragment instanceof MulTableFragment) curType = 1;
			i.putExtra("type", curType);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.practice_activity_menu, menu);
		return true;
	}
}
package com.example.mathpractice.activities.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.scores.ScoresActivity;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

public class SettingsFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey);
		Preference levelPreference = findPreference("level");
		assert levelPreference != null;
		levelPreference.setOnPreferenceChangeListener((preference, newValue) -> {
			preference.setSummary(newValue.toString());
			return true;
		});
		SharedPreferences sharedPreferences = levelPreference.getSharedPreferences();
		assert sharedPreferences != null;
		levelPreference.setSummary(sharedPreferences.getString("level", "1"));
		Preference autoLevelUpPreference = findPreference("autoLevelUp");
		assert autoLevelUpPreference != null;
		autoLevelUpPreference.setOnPreferenceChangeListener((preference, newValue) -> {
			preference.setSummary(Boolean.getBoolean(newValue.toString()) ? "Enabled" : "Disabled");
			return true;
		});
		autoLevelUpPreference.setSummary(sharedPreferences.
				getBoolean("autoLevelUp", false) ? "Enabled" : "Disabled");
		Preference calculatedScoresHistoryOfLevels = findPreference("calculation");
		assert calculatedScoresHistoryOfLevels != null;
		calculatedScoresHistoryOfLevels.setOnPreferenceChangeListener((preference, newValue) -> {
			DataBaseHelper dataBase = new DataBaseHelper(SettingsFragment.this.getContext());
			for (int i = 0; i < 3; i++) {
				Cursor c = dataBase.execSQLForReading("SELECT MAX(level) FROM practices_done_type_" + i + "_user_" +
						sharedPreferences.getString("user", "Local"));
				c.moveToFirst();
				@SuppressLint("Range")
				int maxLevel = c.getInt(c.getColumnIndex("MAX(level)"));
				c.close();
				for (int j = 0; j < maxLevel; j++) {
					ScoresActivity.updateScores(j + 1, i, this.getContext(), newValue.toString());
				}
			}
			return true;
		});
	}
}
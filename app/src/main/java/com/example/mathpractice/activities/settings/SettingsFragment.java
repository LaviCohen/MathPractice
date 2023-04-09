package com.example.mathpractice.activities.settings;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.scores.ScoresUtilities;
import com.example.mathpractice.sqlDataBase.PracticesHelper;

import java.util.Objects;

/**
 * The settings fragment, as required in android preferences documentation.
 * */
public class SettingsFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey);
		Preference userPreference = findPreference("user");
		assert userPreference != null;
		userPreference.setSummary(Objects.requireNonNull(userPreference.getSharedPreferences()).getString("user", "Local"));
		Preference levelPreference = findPreference("level");
		assert levelPreference != null;
		levelPreference.setSummary("Level " + SettingsActivity.getUserGeneralLevel(this.getContext()));
		Preference calculatedScoresHistoryOfLevels = findPreference("calculation");
		assert calculatedScoresHistoryOfLevels != null;
		calculatedScoresHistoryOfLevels.setOnPreferenceChangeListener((preference, newValue) -> {
			PracticesHelper dataBase = new PracticesHelper(SettingsFragment.this.getContext());
			for (int i = 0; i < 3; i++) {
				Cursor c = dataBase.execSQLForReading("SELECT MAX(level) FROM practices_done_type_" + i + "_user_" +
						Objects.requireNonNull(preference.getSharedPreferences()).getString("user", "Local"));
				c.moveToFirst();
				@SuppressLint("Range")
				int maxLevel = c.getInt(c.getColumnIndex("MAX(level)"));
				c.close();
				for (int j = 0; j < maxLevel; j++) {
					ScoresUtilities.updateScores(this.getContext(), i, j + 1, newValue.toString());
				}
			}
			return true;
		});
	}
}
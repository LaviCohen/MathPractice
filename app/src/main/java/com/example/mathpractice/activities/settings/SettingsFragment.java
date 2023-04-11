package com.example.mathpractice.activities.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.activities.scores.ScoresUtilities;
import com.example.mathpractice.reminder.MyAlarmManager;
import com.example.mathpractice.sqlDataBase.PracticesHelper;

import java.util.Objects;

/**
 * The settings fragment, as required in android preferences documentation.
 * */
public class SettingsFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getContext());
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
			for (int i = 0; i < 2; i++) {
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
		Preference levelUpScore = findPreference("levelUpScore");
		assert levelUpScore != null;
		levelUpScore.setOnPreferenceClickListener(preference -> {
			Dialog d = new Dialog(SettingsFragment.this.getContext());
			d.setTitle("Pick Number");
			d.setContentView(R.layout.number_picker_prefrences_dialog_layout);
			Button ok = d.findViewById(R.id.set_button);
			Button cancel = d.findViewById(R.id.cancel_button);
			NumberPicker np = d.findViewById(R.id.number_picker);
			np.setMinValue(70);
			np.setMaxValue(90);
			np.setWrapSelectorWheel(false);
			np.setValue(sp.getInt("levelUpScore", 80));
			ok.setOnClickListener(v -> {
				sp.edit().putInt("levelUpScore", np.getValue()).apply();
				d.dismiss();
				preference.setSummary(np.getValue() + "");
			});
			cancel.setOnClickListener(v -> d.dismiss());
			d.show();
			return true;
		});
		levelUpScore.setSummary(sp.getInt("levelUpScore", 80) + "");

	}
}
package com.example.mathpractice.activities.practice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.activities.scores.ScoresActivity;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.math.AbstractPractice;
import com.example.mathpractice.math.Trinom;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

public abstract class AbstractPracticeFragment extends Fragment {

	protected AbstractPractice currentPractice;
	protected TextView textView;
	protected Button check;


	protected DataBaseHelper dataBase;

	public View startFragment(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState, int layout) {
		View parentView = inflater.inflate(layout, container, false);
		dataBase = ((PracticeActivity)getActivity()).dataBase;
		textView = parentView.findViewById(R.id.textView);
		check = parentView.findViewById(R.id.check);
		check.setOnClickListener(view -> {
			if (check.getText().toString().equals(getString(R.string.next))){
				generatePractice(getContext());
				check.setText(R.string.check);
				emptyInputViews();
			} else {
				int checkValue = checkInputs();
				if (checkValue == -1) {//Illegal input
					showIllegalInputToast();
				} else {
					if (checkValue == 0) {
						showFailText();
					} else {
						textView.setText(R.string.well_done);
					}
					dataBase.addPractice(currentPractice, PreferenceManager.getDefaultSharedPreferences(
							AbstractPracticeFragment.this.getContext()).getString("user", "Local"), checkValue == 1);
					check.setText(R.string.next);
					String calculation = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("calculation", "all");
					boolean levelUp = ScoresActivity.updateScores(currentPractice.getLevel(), currentPractice.getType(), this.getContext(), calculation);
					if (levelUp) {
						Toast.makeText(this.getContext(), "Level-Up!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		generatePractice(getContext());
		getInputViews(parentView);
		return parentView;
	}

	protected abstract void getInputViews(View parentView);

	protected abstract void showFailText();

	protected abstract void showIllegalInputToast();

	protected abstract int checkInputs();

	protected abstract void emptyInputViews();

	protected abstract void generatePractice(Context context);
}

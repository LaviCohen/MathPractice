package com.example.mathpractice.activities.practice.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.mathpractice.activities.scores.ScoresUtilities;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.math.AbstractPractice;
import com.example.mathpractice.sqlDataBase.PracticesHelper;

/**
 * This is the abstract class that represents a practice fragment, parent of {@link MulTableFragment} and {@link TrinomFragment}.
 * */
public abstract class AbstractPracticeFragment extends Fragment {

	/**
	 * The current practice which currently displayed.
	 * */
	protected AbstractPractice currentPractice;
	/**
	 * The {@link TextView} that displays the currentPractice.
	 * */
	protected TextView practiceTextView;
	/**
	 * The {@link TextView} that displays the level of the currentPractice.
	 * */
	protected TextView levelTextView;
	/**
	 * The button to check the answer.
	 * After clicked, the response for the answer displayed and the button's text changed to "Next".
	 * After clicking again, new practice is displayed and the button's text return to be "Check".
	 * */
	protected Button check;

	/**
	 * The {@link com.example.mathpractice.sqlDataBase.DataBaseHelper} object which will be used in this activity, created once on startFragment.
	 * */
	protected PracticesHelper dataBase;
	/**
	 * This method initialize the common things to both of the practice fragment.
//	 * That includes the wrapping functionality, such as inserting data to the database and changing the practice.
	 * @param inflater used to inflate the layout file to this fragment.
	 * @param container the {@link ViewGroup} who will be the parent of the inflated parent view.
	 * @param layout the layout of the new fragment.
	 * @return the parent view of the new fragment.
	 * */
	@SuppressLint("SetTextI18n")
	public View startFragment(LayoutInflater inflater, ViewGroup container,
							  int layout) {
		View parentView = inflater.inflate(layout, container, false);
		dataBase = ((PracticeActivity) requireActivity()).getDataBase();
		practiceTextView = parentView.findViewById(R.id.textView);
		levelTextView = parentView.findViewById(R.id.levelTextView);
		check = parentView.findViewById(R.id.check);
		check.setOnClickListener(view -> check());
		generatePractice(getContext());
		levelTextView.setText("Level " + SettingsActivity.getUserLevel(getContext(), currentPractice.getType()));
		getInputViews(parentView);
		return parentView;
	}
	/**
	 * This method performs the check functionality, including data store and generating new practice if needed
	 * */
	@SuppressLint("SetTextI18n")
	public void check(){
		if (check.getText().toString().equals(getString(R.string.next))){
			generatePractice(getContext());
			levelTextView.setText("Level " + SettingsActivity.getUserLevel(getContext(), currentPractice.getType()));
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
					practiceTextView.setText(R.string.well_done);
				}
				dataBase.addPractice(currentPractice, PreferenceManager.getDefaultSharedPreferences(
						AbstractPracticeFragment.this.requireContext()).getString("user", "Local"), checkValue == 1);
				check.setText(R.string.next);
				String calculation = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("calculation", "all");
				boolean levelUp = ScoresUtilities.updateScores(this.getContext(), currentPractice.getType(), currentPractice.getLevel(), calculation);
				if (levelUp) {
					Toast.makeText(this.getContext(), "Level-Up! You're on level " +
							SettingsActivity.getUserGeneralLevel(AbstractPracticeFragment.this.getContext())
							+ " now!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	/**
	 * This method let each individual practice fragment to initialize its own Views, which used to input this type of practice.
	 * @param parentView the parent view of the fragment, to call findViewById on it.
	 * */
	protected abstract void getInputViews(View parentView);

	/**
	 * This method let each individual practice fragment to show its own fail text.
	 * */
	protected abstract void showFailText();
	
	/**
	 * This method let each individual practice fragment to show its own fail text.
	 * */
	protected abstract void showIllegalInputToast();
	
	/**
	 * This method let each individual practice fragment to preform its own inputs check.
	 * @return <table><CAPTION><EM>Key for return values</EM></CAPTION>
	 * 			<tr><td>1</td><td>If the answer is correct</td></tr>
	 * 			<tr><td>0</td><td>If the answer is wrong</td></tr>
	 * 		 	<tr><td>-1</td><td>If the input is illegal</td></tr></table>
	 * */
	protected abstract int checkInputs();

	/**
	 * This method let each individual practice fragment to empty its own input views.
	 * */
	protected abstract void emptyInputViews();

	/**
	 * This method let each individual practice fragment to generate its own practice.
	 * @param context current activity's context to get user level.
	 * */
	protected abstract void generatePractice(Context context);
}

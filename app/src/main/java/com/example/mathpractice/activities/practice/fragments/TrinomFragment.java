package com.example.mathpractice.activities.practice.fragments;

import android.annotation.SuppressLint;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.math.AbstractPractice;
import com.example.mathpractice.math.Trinom;

/**
 * Subclass of {@link AbstractPracticeFragment}, specified for {@link Trinom} practice type.
 * All methods are inherited.
 * */
public class TrinomFragment extends AbstractPracticeFragment{

	/**
	 * Two {@link EditText}s to get answer from them (because (almost) each trinom have two solutions).
	 * */
	private EditText answer1, answer2;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return super.startFragment(inflater, container, R.layout.fragment_trinom);
	}

	@Override
	protected void getInputViews(View parentView){
		answer1 = parentView.findViewById(R.id.answerField1);
		answer2 = parentView.findViewById(R.id.answerField2);
	}

	@SuppressLint("StringFormatMatches")
	@Override
	protected void showFailText() {
		String text = String.format(getString(R.string.correct_answers), currentPractice.toExp(),
				AbstractPractice.fn(((Trinom)currentPractice).solution1), AbstractPractice.fn(((Trinom)currentPractice).solution2));
		textView.setText(text);
	}

	@Override
	protected void showIllegalInputToast() {
		Toast.makeText(getContext(), R.string.unfilled_fields, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected int checkInputs() {
		try {
			return currentPractice.check(Double.parseDouble(answer1.getText().toString()),
					Double.parseDouble(answer2.getText().toString())) ? 1 : 0;
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	@Override
	protected void emptyInputViews() {
		answer1.setText("");
		answer2.setText("");
	}

	@SuppressLint("SetTextI18n")
	@Override
	protected void generatePractice(Context context) {
		try {
			this.currentPractice = Trinom.generateTrinom(SettingsActivity.getUserLevel(context, AbstractPractice.TYPE_TRINOM));
		} catch (IllegalArgumentException iae) {
			generatePractice(context);
		}
		this.textView.setText(currentPractice.toExp() + "\nX = ?");
	}
}
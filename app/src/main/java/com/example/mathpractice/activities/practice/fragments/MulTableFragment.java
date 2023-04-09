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

import com.example.mathpractice.R;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.math.AbstractPractice;
import com.example.mathpractice.math.MulTable;

/**
 * Subclass of {@link AbstractPracticeFragment}, specified for {@link MulTable} practice type.
 * All methods are inherited.
 * */
public class MulTableFragment extends AbstractPracticeFragment {

	/**
	 * The input field to get the answer.
	 * */
	private EditText answerField;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.startFragment(inflater, container, R.layout.fragment_mul_table);
	}

	@Override
	public void getInputViews(View parentView) {
		answerField = parentView.findViewById(R.id.answerField);
	}

	@Override
	protected void showFailText() {
		textView.setText(String.format("Wrong, the correct answer for\n%s\nis %s",
				currentPractice.toExp(), AbstractPractice.fn(((MulTable)currentPractice).solution)));
	}

	@Override
	protected void showIllegalInputToast() {
		Toast.makeText(getActivity(), "You must fill the field", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected int checkInputs() {
		try {
			return currentPractice.check(Double.parseDouble(answerField.getText().toString())) ? 1 : 0;
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	@Override
	protected void emptyInputViews() {
		answerField.setText("");
	}

	@Override
	@SuppressLint("SetTextI18n")
	public void generatePractice(Context context) {
		currentPractice = MulTable.generateMulTable(SettingsActivity.getUserLevel(context, AbstractPractice.TYPE_MUL_TABLE));
		this.textView.setText(currentPractice.toExp());
	}
}
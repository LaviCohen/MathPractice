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

	/**
	 * This method initialize the fragment.
	 * @param inflater used to inflate the layout file to this fragment.
	 * @param container the {@link ViewGroup} who will be the parent of the inflated parent view.
	 * @param savedInstanceState default android param.
	 * @return the parent view of the new fragment.
	 * */
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.startFragment(inflater, container, R.layout.fragment_mul_table);
	}

	/**
	 * Implementation of the abstract <code>{@link AbstractPracticeFragment}.getInputViews</code> method.
	 * @param parentView the parent view of the fragment, to call findViewById on it.
	 */
	@Override
	public void getInputViews(View parentView) {
		answerField = parentView.findViewById(R.id.answerField);
	}

	/**
	 * Implementation of the abstract <code>{@link AbstractPracticeFragment}.showFailText</code> method.
	 */
	@Override
	protected void showFailText() {
		practiceTextView.setText(String.format("Wrong, the correct answer for\n%s\nis %s",
				currentPractice.toExp(), AbstractPractice.fn(((MulTable)currentPractice).solution)));
	}

	/**
	 * Implementation of the abstract <code>{@link AbstractPracticeFragment}.showIllegalInputToast</code> method.
	 */
	@Override
	protected void showIllegalInputToast() {
		Toast.makeText(getActivity(), "You must fill the field", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Implementation of the abstract <code>{@link AbstractPracticeFragment}.checkInputs</code> method.
	 * @return <table><CAPTION><EM>Key for return values</EM></CAPTION>
	 *				<tr><td>1</td><td>If the answer is correct</td></tr>
	 *	 			<tr><td>0</td><td>If the answer is wrong</td></tr>
	 *			 	<tr><td>-1</td><td>If the input is illegal</td></tr></table>
	 */
	@Override
	protected int checkInputs() {
		try {
			return currentPractice.check(Double.parseDouble(answerField.getText().toString())) ? 1 : 0;
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * Implementation of the abstract <code>{@link AbstractPracticeFragment}.emptyInputViews</code> method.
	 */
	@Override
	protected void emptyInputViews() {
		answerField.setText("");
	}

	/**
	 * Implementation of the abstract <code>{@link AbstractPracticeFragment}.generatePractice</code> method.
	 * Calls {@link MulTable}.generateMulTable method to generate the required practice.
	 * @param context current activity's context to get user level.
	 */
	@Override
	@SuppressLint("SetTextI18n")
	public void generatePractice(Context context) {
		currentPractice = MulTable.generateMulTable(SettingsActivity.getUserLevel(context, AbstractPractice.TYPE_MUL_TABLE));
		this.practiceTextView.setText(currentPractice.toExp());
	}
}
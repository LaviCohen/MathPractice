package com.example.mathpractice.activities.scores;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.sqlDataBase.PracticesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This activity is the activity to show scores and results of the practicing for the current user.
 * */
public class ScoresActivity extends AppCompatActivity {

	/**
	 * This two-dimensional array is holding the scores for each practicing type ({@link com.example.mathpractice.math.Trinom} or {@link com.example.mathpractice.math.MulTable}, for each level).
	 * The array reduces the number of database reading calls, which affect application's performance.
	 * */
	public static double[][] scores = null;
	/**
	 * The {@link PracticesHelper} object which will be used in this activity, created once on onCreate.
	 * */
	public PracticesHelper dataBase = null;

	/**
	 * This {@link TextView} displays the message for no data to show. Hidden when there are practices.
	 * */
	private TextView noDataTextView = null;

	/**
	 * Current practice type to show, following the types specified in {@link com.example.mathpractice.math.AbstractPractice}.
	 * */
	private int practice;

	/**
	 * This boolean holds whenever to show full data (every practice which has been done) or only parted (score of each level).
	 * */
	private boolean full = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);

		if (dataBase == null) {
			dataBase = new PracticesHelper(this);
		}
		setTitle("Scores");

		findViewById(R.id.checkBox).setOnClickListener(view -> {
			full = ((CheckBox)view).isChecked();
			showScoresForType(practice);
		});
		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_scores_menu);
		bottomNavigationView.setOnItemSelectedListener(item -> {

			int newType = item.getItemId();

			if (practice != newType) {
				practice = newType;
				showScoresForType(practice);
			}

			return true;
		});

		practice = getIntent().getIntExtra("practice", 0);

		bottomNavigationView.setSelectedItemId(practice);

		showScoresForType(practice);
	}

	/**
	 * This method shows the scores data for the type given.
	 * @param type practice type as specified in {@link com.example.mathpractice.math.AbstractPractice}.
	 * */
	@SuppressLint("Range")
	@SuppressWarnings("rawtypes")
	private void showScoresForType(int type) {
		if (noDataTextView != null) {
			((ViewGroup)findViewById(R.id.scores_list).getParent()).removeView(noDataTextView);
			noDataTextView = null;
		}
		RecyclerView recView = findViewById(R.id.scores_list);
		RecyclerView.Adapter recViewAdapter = dataBase.getAdapter(this, type, full);
		if (recViewAdapter == null) {
			noDataTextView = new TextView(this);
			noDataTextView.setText(R.string.no_data_in_scores_sentence);
			((ViewGroup)findViewById(R.id.scores_list).getParent()).addView(noDataTextView);
			recView.setVisibility(View.INVISIBLE);
			return;
		}
		recView.setAdapter(recViewAdapter);
		recView.setVisibility(View.VISIBLE);
		recView.setLayoutManager(new LinearLayoutManager(this));
	}

	/**
	 * Method to go back to practice activity when back navigation button is pressed.
	 * */
	@Override
	public void onBackPressed() {
		gotoPracticeActivity();
	}

	/**
	 * Method to go back to practice activity when upper menu back button is pressed.
	 * */
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.back_menu_option) {
			gotoPracticeActivity();
		}
		return true;
	}

	/**
	 * Method to go back to practice activity, called when back button is pressed.
	 * */
	public void gotoPracticeActivity(){
		Intent i = new Intent(ScoresActivity.this, PracticeActivity.class);
		i.putExtra("practice", practice);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.back_menu, menu);
		return true;
	}
}
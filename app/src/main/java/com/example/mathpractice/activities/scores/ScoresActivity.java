package com.example.mathpractice.activities.scores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;
import com.example.mathpractice.activities.scores.LevelsRecViewAdapter.Level;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ScoresActivity extends AppCompatActivity {

	public static double[][] scores = null;

	public DataBaseHelper dataBase = null;

	private TextView noDataTextView = null;
	private int type;
	boolean full = false;

	String user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);

		if (dataBase == null) {
			dataBase = new DataBaseHelper(this);
		}
		setTitle("Scores");

		findViewById(R.id.checkBox).setOnClickListener(view -> {
			full = ((CheckBox)view).isChecked();
			showScoresForType(type);
		});
		@SuppressWarnings("Deprecated")
		BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_scores_menu);
		bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

			int newType = -1;

			if (item.getItemId() == R.id.trinom_menu_option) {
				newType = 0;
			} else if (item.getItemId() == R.id.mul_table_menu_option) {
				newType = 1;
			}

			if (type != newType) {
				type = newType;
				showScoresForType(type);
			}

			return true;
		});

		type = getIntent().getIntExtra("type", 0);

		bottomNavigationView.setSelectedItemId(type);

		showScoresForType(type);
	}

	@SuppressLint("Range")
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

	@Override
	public void onBackPressed() {
		gotoPracticeActivity();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.back_menu_option) {
			gotoPracticeActivity();
		}
		return true;
	}

	public void gotoPracticeActivity(){
		Intent i = new Intent(ScoresActivity.this, PracticeActivity.class);
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
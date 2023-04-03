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
		RecyclerView.Adapter recViewAdapter = null;
		user = PreferenceManager.getDefaultSharedPreferences(this).getString("user", "Local");
		Cursor c = this.dataBase.execSQLForReading("SELECT MAX(level) FROM practices_done_type_" + type + "_user_" + user);
		c.moveToFirst();
		@SuppressLint("Range")
		int maxLevel = c.getInt(c.getColumnIndex("MAX(level)"));
		c.close();

		if (scores == null) {
			String calculation = PreferenceManager.getDefaultSharedPreferences(this).getString("calculation", "all");
			for (int i = 0; i < maxLevel; i++) {
				updateScores(i + 1, type, this, calculation);
			}
		}
		if (maxLevel == 0){
			noDataTextView = new TextView(this);
			noDataTextView.setText(R.string.no_data_in_scores_sentence);
			((ViewGroup)findViewById(R.id.scores_list).getParent()).addView(noDataTextView);
			recView.setVisibility(View.INVISIBLE);
			return;
		}

		ArrayList list = new ArrayList<>();

		if (!full) {
			recViewAdapter = new LevelsRecViewAdapter();
			for (int i = 0; i < maxLevel; i++){
				list.add(new Level(i + 1 + "", scores[type][i] + ""));
			}
		} else {
			recViewAdapter = new PracticesRecViewAdapter();
			c = this.dataBase.execSQLForReading("SELECT * FROM practices_done_type_" + type + "_user_" + user);
			c.moveToFirst();
			boolean cont = true;
			while (cont) {
				if (c.isLast()) {
					cont = false;
				}
				list.add(new PracticesRecViewAdapter.Practice(c.getString(c.getColumnIndex("practice")),
						Integer.parseInt(c.getString(c.getColumnIndex("level"))),
						c.getString(c.getColumnIndex("success")).equals("1")));
				c.moveToNext();
			}
			c.close();
		}

		if (recViewAdapter instanceof LevelsRecViewAdapter) {
			((LevelsRecViewAdapter)recViewAdapter).setLevels((ArrayList<Level>) list);
		} else {
			((PracticesRecViewAdapter)recViewAdapter).setPractices((ArrayList<PracticesRecViewAdapter.Practice>) list);
		}

		recView.setAdapter(recViewAdapter);
		recView.setVisibility(View.VISIBLE);
		recView.setLayoutManager(new LinearLayoutManager(this));
	}

	public static boolean updateScores(int level, int type, Context context, String calculation) {
		DataBaseHelper dataBase = new DataBaseHelper(context);
		if (scores == null) {
			scores = new double[3][3];
		}
		String user = PreferenceManager.getDefaultSharedPreferences(context).getString("user", "Local");
		Cursor c = dataBase.execSQLForReading(
				"SELECT AVG(success) FROM (SELECT success FROM practices_done_type_" + type + "_user_" +
						user  + " WHERE level = " + level +
						(calculation.equals("all") ? "" : " ORDER BY id DESC limit(" + Integer.parseInt(calculation) + ")") + ")");
		c.moveToFirst();
		scores[type][level - 1] = ((int)(c.getDouble(0) * 10000))/100.0;
		c.close();
		if (level < 3 && scores[type][level - 1] > 80.0) {
			c = dataBase.execSQLForReading("SELECT COUNT(success) FROM (SELECT success FROM practices_done_type_" + type + "_user_" +
					user + " WHERE level = " + level + ");");
			c.moveToFirst();
			int count = c.getInt(0);
			c.close();
			if (count > 5) {
				dataBase.execSQLForWriting("UPDATE users SET level_type_" + type + " = " + (level + 1)
						+ " WHERE username = '" + user + "';");
				return true;
			}
		}
		return false;
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
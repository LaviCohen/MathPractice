package com.example.mathpractice.activities.scores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathpractice.sqlDataBase.PracticesHelper;

import java.util.ArrayList;

public class ScoresUtilities {

	/**
	 * Create the required {@link RecyclerView.Adapter} for the scores activity.
	 * @param context the current active context.
	 * @param type the required adapter type.
	 * @param full whether to return full practices data aor only levels' scores.
	 * @return The required adapter.
	 * */
	@SuppressLint("Range")
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static RecyclerView.Adapter getAdapter(Context context, int type, boolean full) {

		PracticesHelper practicesHelper = new PracticesHelper(context);
		RecyclerView.Adapter recViewAdapter;
		String user = PreferenceManager.getDefaultSharedPreferences(context).getString("user", "Local");
		Cursor c = practicesHelper.execSQLForReading("SELECT MAX(level) FROM practices_done_type_" + type + "_user_" + user);
		c.moveToFirst();
		@SuppressLint("Range")
		int maxLevel = c.getInt(c.getColumnIndex("MAX(level)"));
		c.close();

		if (maxLevel == 0){
			return null;
		}

		//Updates scores array
		String calculation = PreferenceManager.getDefaultSharedPreferences(context).getString("calculation", "all");
		for (int i = 0; i < maxLevel; i++) {
			System.out.println("Iteration " + i);
			updateScores(context, type,i + 1, calculation);
		}

		ArrayList list = new ArrayList<>();

		if (!full) {
			recViewAdapter = new LevelsRecViewAdapter();
			for (int i = 0; i < maxLevel; i++){
				System.out.println("Score for level " + (i + 1) + " is " + ScoresActivity.scores[type][i]);
				list.add(new LevelsRecViewAdapter.Level(i + 1 + "", ScoresActivity.scores[type][i] + ""));
			}
		} else {
			recViewAdapter = new PracticesRecViewAdapter();
			c = practicesHelper.execSQLForReading("SELECT * FROM practices_done_type_" + type + "_user_" + user);
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
			((LevelsRecViewAdapter)recViewAdapter).setLevels((ArrayList<LevelsRecViewAdapter.Level>) list);
		} else {
			((PracticesRecViewAdapter)recViewAdapter).setPractices((ArrayList<PracticesRecViewAdapter.Practice>) list);
		}
		return recViewAdapter;
	}

	/**
	 * The method which updates the scores data.
	 * @param context the current active context.
	 * @param type the type to update its scores.
	 * @param level the level to update its scores.
	 * @param calculation the current scores calculation method.
	 * @return true if the scores update requires level-up.
	 * */
	public static boolean updateScores(Context context, int type, int level, String calculation) {
		PracticesHelper dataBase = new PracticesHelper(context);
		if (ScoresActivity.scores == null) {
			ScoresActivity.scores = new double[2][3];
		}
		String user = PreferenceManager.getDefaultSharedPreferences(context).getString("user", "Local");
		Cursor c = dataBase.execSQLForReading(
				"SELECT AVG(success) FROM (SELECT success FROM practices_done_type_" + type + "_user_" +
						user  + " WHERE level = " + level +
						(calculation.equals("all") ? "" : " ORDER BY id DESC limit(" + Integer.parseInt(calculation) + ")") + ")");
		c.moveToFirst();
		System.out.print("Change from " + ScoresActivity.scores[type][level - 1] + " to ");
		ScoresActivity.scores[type][level - 1] = ((int)(c.getDouble(0) * 10000))/100.0;
		System.out.println(ScoresActivity.scores[type][level - 1]);
		c.close();
		if (level < 3 && ScoresActivity.scores[type][level - 1] >
				PreferenceManager.getDefaultSharedPreferences(context).getInt("levelUpScore", 80)) {
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
}

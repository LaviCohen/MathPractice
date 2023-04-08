package com.example.mathpractice.sqlDataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathpractice.activities.scores.LevelsRecViewAdapter;
import com.example.mathpractice.activities.scores.PracticesRecViewAdapter;
import com.example.mathpractice.activities.scores.ScoresActivity;
import com.example.mathpractice.math.AbstractPractice;
import com.example.mathpractice.math.MulTable;

import java.util.ArrayList;

/**
 * Subclass of {@link DataBaseHelper}, used to deal with specifically the practices tables.
 * */
public class PracticesHelper extends DataBaseHelper {


	public PracticesHelper(@Nullable Context context) {
		super(context);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String query = "CREATE TABLE practices_done_type_0_user_Local (id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, practice TEXT, success INTEGER);";
		sqLiteDatabase.execSQL(query);
		query = "CREATE TABLE practices_done_type_1_user_Local (id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, practice TEXT, success INTEGER);";
		sqLiteDatabase.execSQL(query);
	}

	/**
	 * Create the required {@link RecyclerView.Adapter} for the scores activity.
	 * @param context the current active context.
	 * @param type the required adapter type.
	 * @param full whether to return full practices data aor only levels' scores.
	 * @return The required adapter.
	 * */
	@SuppressLint("Range")
	@SuppressWarnings({"rawtypes", "unchecked"})
	public RecyclerView.Adapter getAdapter(Context context, int type, boolean full) {
		RecyclerView.Adapter recViewAdapter;
		String user = PreferenceManager.getDefaultSharedPreferences(context).getString("user", "Local");
		Cursor c = execSQLForReading("SELECT MAX(level) FROM practices_done_type_" + type + "_user_" + user);
		c.moveToFirst();
		@SuppressLint("Range")
		int maxLevel = c.getInt(c.getColumnIndex("MAX(level)"));
		c.close();

		if (ScoresActivity.scores == null) {
			String calculation = PreferenceManager.getDefaultSharedPreferences(context).getString("calculation", "all");
			for (int i = 0; i < maxLevel; i++) {
				updateScores(context, type,i + 1, calculation);
			}
		}
		if (maxLevel == 0){
			return null;
		}

		ArrayList list = new ArrayList<>();

		if (!full) {
			recViewAdapter = new LevelsRecViewAdapter();
			for (int i = 0; i < maxLevel; i++){
				list.add(new LevelsRecViewAdapter.Level(i + 1 + "", ScoresActivity.scores[type][i] + ""));
			}
		} else {
			recViewAdapter = new PracticesRecViewAdapter();
			c = this.execSQLForReading("SELECT * FROM practices_done_type_" + type + "_user_" + user);
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
	 * @return if the scores update requires level-up.
	 * */
	public static boolean updateScores(Context context, int type, int level, String calculation) {
		PracticesHelper dataBase = new PracticesHelper(context);
		if (ScoresActivity.scores == null) {
			ScoresActivity.scores = new double[3][3];
		}
		String user = PreferenceManager.getDefaultSharedPreferences(context).getString("user", "Local");
		Cursor c = dataBase.execSQLForReading(
				"SELECT AVG(success) FROM (SELECT success FROM practices_done_type_" + type + "_user_" +
						user  + " WHERE level = " + level +
						(calculation.equals("all") ? "" : " ORDER BY id DESC limit(" + Integer.parseInt(calculation) + ")") + ")");
		c.moveToFirst();
		ScoresActivity.scores[type][level - 1] = ((int)(c.getDouble(0) * 10000))/100.0;
		c.close();
		if (level < 3 && ScoresActivity.scores[type][level - 1] > 80.0) {
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

	/**
	 * Adding practice which the user submitted to the database.
	 * @param practice the practice which has been submitted.
	 * @param user the user who submitted the practice.
	 * @param success whether the user was correct or not.
	 * */
	public void addPractice(AbstractPractice practice, String user, boolean success) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("level", practice.getLevel());
		cv.put("practice", practice.toExp());
		cv.put("success", success);
		System.out.println("Inserting " + cv);
		if (practice instanceof MulTable) {
			System.out.println("Mul");
		}
		String table = "practices_done_type_" + practice.getType() + "_user_" + user;
		System.out.println("Table: " + table);
		db.insert(table, null, cv);
	}

}

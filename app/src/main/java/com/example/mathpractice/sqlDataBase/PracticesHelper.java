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

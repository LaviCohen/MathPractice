package com.example.mathpractice.sqlDataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.mathpractice.math.AbstractPractice;

/**
 * Subclass of {@link DataBaseHelper}, used to deal with specifically the practices tables.
 * */
public class PracticesHelper extends DataBaseHelper {


	/**
	 * Basic inherited constructor.
	 * @param context the current context.
	 */
	public PracticesHelper(@Nullable Context context) {
		super(context);
	}

	/**
	 * Initializes the tables in the database.
	 * @param sqLiteDatabase the database.
	 */
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
		String table = "practices_done_type_" + practice.getType() + "_user_" + user;
		System.out.println("Table: " + table);
		db.insert(table, null, cv);
	}

}

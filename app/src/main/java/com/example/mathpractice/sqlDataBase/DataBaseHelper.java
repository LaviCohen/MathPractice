package com.example.mathpractice.sqlDataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * This class is abstract, parent of other classes who deal with the SQLite database.
 * Subclass of {@link SQLiteOpenHelper}, used to deal with data w/r.
 * */
public abstract class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * Basic inherited constructor.
     * @param context the current context.
     */
    public DataBaseHelper(@Nullable Context context) {
        super(context, "math_practice_db", null, 1);
    }

    /**
     * This method react to updates.
     * @param sqLiteDatabase the database.
     * @param i default android parameter.
     * @param i1 default android parameter.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Executing reading-only SQL command.
     * @param sqlCommand the command to execute.
     * @return the returned cursor object.
     * */
    public Cursor execSQLForReading(String sqlCommand){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sqlCommand, null);
    }

    /**
     * Executing writing SQL command.
     * @param query the command to execute.
     * */
    public void execSQLForWriting(String query) {
        this.getWritableDatabase().execSQL(query);
    }

}
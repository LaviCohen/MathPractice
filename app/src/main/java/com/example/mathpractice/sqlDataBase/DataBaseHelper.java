package com.example.mathpractice.sqlDataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.scores.LevelsRecViewAdapter;
import com.example.mathpractice.activities.scores.PracticesRecViewAdapter;
import com.example.mathpractice.activities.scores.ScoresActivity;
import com.example.mathpractice.math.AbstractPractice;

import com.example.mathpractice.math.MulTable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(@Nullable Context context) {
        super(context, "math_practice_db", null, 1);
    }

    public static Rect getHatRect(DataBaseHelper dbh, String username){
        Cursor c = dbh.execSQLForReading(
                "SELECT hat_ID FROM users WHERE username = '" + username + "';");
        c.moveToFirst();
        @SuppressLint("Range") int id = c.getInt(c.getColumnIndex("hat_ID"));
        c.close();
        c = dbh.execSQLForReading(
                "SELECT hat_size FROM users WHERE username = '" + username + "';");
        c.moveToFirst();
        @SuppressLint("Range") int hatSize = c.getInt(c.getColumnIndex("hat_size"));
        c.close();
        if (id != -1 && hatSize != 0) {
            c = dbh.execSQLForReading(
                    "SELECT hat_x FROM users WHERE username = '" + username + "';");
            c.moveToFirst();
            @SuppressLint("Range") int hatX = c.getInt(c.getColumnIndex("hat_x"));
            c.close();
            c = dbh.execSQLForReading(
                    "SELECT hat_y FROM users WHERE username = '" + username + "';");
            c.moveToFirst();
            @SuppressLint("Range") int hatY = c.getInt(c.getColumnIndex("hat_y"));
            c.close();
            int x = hatX - hatSize/2;
            int y = hatY - hatSize;
            return new Rect(x, y, hatSize, id);
        }
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE practices_done_type_0_user_Local (id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, practice TEXT, success INTEGER);";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE practices_done_type_1_user_Local (id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, practice TEXT, success INTEGER);";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, profile_image BLOB, hat_x INTEGER, hat_y INTEGER, hat_size INTEGER, hat_ID INTEGER, level_type_0 INTEGER, level_type_1 INTEGER);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Cursor execSQLForReading(String sqlCommand){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sqlCommand, null);
    }

    public void createNewUser(String username, String password, Bitmap image, int rotationDegs) {
        ContentValues cv = new  ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("level_type_0", 1);
        cv.put("level_type_1", 1);
        cv.put("profile_image", getBytesFromBitmap(image));
        System.out.println("Image Size: " +  image.getWidth() + ", " + image.getHeight());
        Point p = new Point(-1, -1);
        InputImage inputImage = InputImage.fromBitmap(image, rotationDegs);

        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);

        Task<List<Face>> result =
                detector.process(inputImage)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        if (faces.isEmpty()) {
                                            System.out.println("No face was detected");
                                            return;
                                        }
                                        Rect faceRect = faces.get(0).getBoundingBox();
                                        p.x = faceRect.centerX();
                                        p.y = faceRect.top + 200;
                                        DataBaseHelper.this.execSQLForWriting(
                                                "UPDATE users SET hat_x = " + p.x  + ", hat_y = " + p.y
                                                         + ", hat_size = " + faceRect.width() * 1.3
                                                 + " WHERE username = '" + username + "';"
                                        );
                                        System.out.println("Data Updated, " + p.toString());
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Failed to detect face");
                                    }
                                });
        cv.put("hat_x", p.x);
        cv.put("hat_y", p.y);
        cv.put("hat_size", 0);
        cv.put("hat_ID", -1);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert("users", null, cv);
        String query = "CREATE TABLE practices_done_type_0_user_" + username + " (id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, practice TEXT, success INTEGER);";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE practices_done_type_1_user_" + username + " (id INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, practice TEXT, success INTEGER);";
        sqLiteDatabase.execSQL(query);
    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public void addPractice(AbstractPractice practice, String user, boolean success) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("level", practice.getLevel());
        cv.put("practice", practice.toExp());
        cv.put("success", success);
        System.out.println("Inserting " + cv.toString());
        if (practice instanceof MulTable) {
            System.out.println("Mul");
        }
        String table = "practices_done_type_" + practice.getType() + "_user_" + user;
        System.out.println("Table: " + table);
        db.insert(table, null, cv);
    }

    public void execSQLForWriting(String query) {
        this.getWritableDatabase().execSQL(query);
    }

    @SuppressLint("Range")
    public RecyclerView.Adapter getAdapter(Context context, int type, boolean full) {
        RecyclerView.Adapter recViewAdapter = null;
        String user = PreferenceManager.getDefaultSharedPreferences(context).getString("user", "Local");
        Cursor c = execSQLForReading("SELECT MAX(level) FROM practices_done_type_" + type + "_user_" + user);
        c.moveToFirst();
        @SuppressLint("Range")
        int maxLevel = c.getInt(c.getColumnIndex("MAX(level)"));
        c.close();

        if (ScoresActivity.scores == null) {
            String calculation = PreferenceManager.getDefaultSharedPreferences(context).getString("calculation", "all");
            for (int i = 0; i < maxLevel; i++) {
                updateScores(i + 1, type, context, calculation);
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
    public static boolean updateScores(int level, int type, Context context, String calculation) {
        DataBaseHelper dataBase = new DataBaseHelper(context);
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
}
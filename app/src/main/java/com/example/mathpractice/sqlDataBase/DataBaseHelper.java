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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

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

/**
 * Subclass of {@link SQLiteOpenHelper}, used to deal with data w/r.
 * Also contain relevant static utility functions.
 * */
public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(@Nullable Context context) {
        super(context, "math_practice_db", null, 1);
    }

    /**
     * Get this specific user hat data, as abused {@link Rect} object.
     * @param dbh the database to use.
     * @param username the user' username.
     * @return Rect object, when the vars are arranged as follows:
     * <tr><th>The Rect Var Name</th><th>The Value Stored</th></tr>
     * <tr><td>left</td><td>Hat's x position</td></tr>
     * <tr><td>top</td><td>Hat's y position</td></tr>
     * <tr><td>right</td><td>Hat size</td></tr>
     * <tr><td>bottom</td><td>Hat ID</td></tr>
     * */
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
     * Create new user in the database.
     * @param username the new user's username.
     * @param password the new user's password.
     * @param image the new user's profile image.
     * @param rotationDegs the new user's image rotation degree.
     * */
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

    /**
     * Converts bitmap to bytes array, to store at the database.
     * @param bitmap the bitmap to convert.
     * @return Byte array which represents the given bitmap.
     * */
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
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
        System.out.println("Inserting " + cv.toString());
        if (practice instanceof MulTable) {
            System.out.println("Mul");
        }
        String table = "practices_done_type_" + practice.getType() + "_user_" + user;
        System.out.println("Table: " + table);
        db.insert(table, null, cv);
    }
    /**
     * Executing writing SQL command.
     * @param query the command to execute.
     * */
    public void execSQLForWriting(String query) {
        this.getWritableDatabase().execSQL(query);
    }

    /**
     * Create the required {@link RecyclerView.Adapter} for the scores activity.
     * @param context the current active context.
     * @param type the required adapter type.
     * @param full whether to return full practices data aor only levels' scores.
     * @return The required adapter.
     * */
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
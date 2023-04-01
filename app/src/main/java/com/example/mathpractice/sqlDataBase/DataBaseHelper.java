package com.example.mathpractice.sqlDataBase;

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

import com.example.mathpractice.R;
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
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(@Nullable Context context) {
        super(context, "math_practice_db", null, 1);
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
}
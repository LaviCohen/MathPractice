package com.example.mathpractice.sqlDataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.ByteArrayOutputStream;

/**
 * Subclass of {@link DataBaseHelper}, used to deal with specifically the users table.
 * */
public class UsersHelper extends DataBaseHelper{

	/**
	 * Basic inherited constructor.
	 * @param context the current context.
	 */
	public UsersHelper(@Nullable Context context) {
		super(context);
	}

	/**
	 * Initializes the tables in the database.
	 * @param sqLiteDatabase the database.
	 */
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String query = "CREATE TABLE users (username TEXT PRIMARY KEY, password TEXT, profile_image BLOB, hat_x INTEGER, hat_y INTEGER, hat_size INTEGER, hat_ID INTEGER, level_type_0 INTEGER, level_type_1 INTEGER);";
		sqLiteDatabase.execSQL(query);
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

		detector.process(inputImage)
				.addOnSuccessListener(
						faces -> {
							if (faces.isEmpty()) {
								System.out.println("No face was detected");
								return;
							}
							Rect faceRect = faces.get(0).getBoundingBox();
							p.x = faceRect.centerX();
							p.y = faceRect.top + 200;
							UsersHelper.this.execSQLForWriting(
									"UPDATE users SET hat_x = " + p.x  + ", hat_y = " + p.y
											+ ", hat_size = " + faceRect.width() * 1.3
											+ " WHERE username = '" + username + "';"
							);
							System.out.println("Data Updated, " + p);
						})
				.addOnFailureListener(
						e -> System.out.println("Failed to detect face"));
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
	 * Get this specific user hat data, as abused {@link Rect} object.
	 * @param username the user' username.
	 * @return Rect object, when the vars are arranged as follows:
	 * <table>
	 * <CAPTION><EM>Description of the returned object</EM></CAPTION>
	 * <tr><th>The Rect Var Name</th><th>The Value Stored</th></tr>
	 * <tr><td>left</td><td>Hat's x position</td></tr>
	 * <tr><td>top</td><td>Hat's y position</td></tr>
	 * <tr><td>right</td><td>Hat size</td></tr>
	 * <tr><td>bottom</td><td>Hat ID</td></tr>
	 * </table>
	 * */
	public Rect getHatRect(String username){
		Cursor c = this.execSQLForReading(
				"SELECT hat_ID FROM users WHERE username = '" + username + "';");
		c.moveToFirst();
		@SuppressLint("Range") int id = c.getInt(c.getColumnIndex("hat_ID"));
		c.close();
		c = this.execSQLForReading(
				"SELECT hat_size FROM users WHERE username = '" + username + "';");
		c.moveToFirst();
		@SuppressLint("Range") int hatSize = c.getInt(c.getColumnIndex("hat_size"));
		c.close();
		if (id != -1 && hatSize != 0) {
			c = this.execSQLForReading(
					"SELECT hat_x FROM users WHERE username = '" + username + "';");
			c.moveToFirst();
			@SuppressLint("Range") int hatX = c.getInt(c.getColumnIndex("hat_x"));
			c.close();
			c = this.execSQLForReading(
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
}

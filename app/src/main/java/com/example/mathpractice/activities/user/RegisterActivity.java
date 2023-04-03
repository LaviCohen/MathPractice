package com.example.mathpractice.activities.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.mathpractice.R;
import com.example.mathpractice.cameraNpictures.CameraUtilities;
import com.example.mathpractice.cameraNpictures.PictureUtilities;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

/**
 * This activity is the activity to create new user.
 * */
public class RegisterActivity extends Activity {

	/**
	 * The permission code for camera usage.
	 * */
	private static final int MY_CAMERA_PERMISSION_CODE = 100;

	/**
	 * The image view for the image profile.
	 * */
	private ImageView imageView;

	/**
	 * The bitmap of the image profile.
	 * */
	private Bitmap userPicture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		this.imageView = findViewById(R.id.myPhoto);
		Button takePhotoButton = findViewById(R.id.takePhotoButton);
		takePhotoButton.setOnClickListener(v -> {
			System.out.println("Taking photo");
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
				} else {
					CameraUtilities.dispatchTakePictureIntent(this);
				}
			} else {
				ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
				CameraUtilities.dispatchTakePictureIntent(this);
			}
		});
		Button pickPhotoFromGalleryButton = findViewById(R.id.PickFromGalleryButton);
		pickPhotoFromGalleryButton.setOnClickListener(v -> {
			System.out.println("Picking photo");
			CameraUtilities.pickPictureFromGallery(this);
		});
		EditText username = findViewById(R.id.editTextUsername);
		EditText password = findViewById(R.id.editTextPassword);
		Button finish = findViewById(R.id.finish_reg_button);
		finish.setOnClickListener(v -> register(username.getText().toString(), password.getText().toString()));
		Button rotateButton = findViewById(R.id.rotateButton);
		rotateButton.setOnClickListener(view -> {
			if (userPicture != null) {
				userPicture = PictureUtilities.rotateBitmap(userPicture, 90f);
				imageView.setImageBitmap(userPicture);
			}
		});
	}

	/**
	 * Method to register as new user, include input checks.
	 * @param username The username to register.
	 * @param password The user's password.
	 * */
	private void register(String username, String password){
		DataBaseHelper dbh = new DataBaseHelper(RegisterActivity.this);
		if (userPicture == null) {
			Toast.makeText(RegisterActivity.this, "You must take a photo", Toast.LENGTH_SHORT).show();
			return;
		}
		if (username.contains(" ")) {
			Toast.makeText(RegisterActivity.this, "Username can't contain spaces", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!dbh.execSQLForReading("SELECT * FROM users WHERE username = '" + username + "';").isAfterLast()) {
			Toast.makeText(RegisterActivity.this, "Your username is already exists", Toast.LENGTH_SHORT).show();
			return;
		}
		dbh.createNewUser(username, password, userPicture, 0);
		LoginActivity.setUser(RegisterActivity.this, username);
		startActivity(new Intent(RegisterActivity.this, UserPageActivity.class));
	}

	/**
	 * This method called when the user allow or deny camera permission, and response respectively.
	 * The params are default, as this method overridden.
	 * */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_CAMERA_PERMISSION_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
				CameraUtilities.dispatchTakePictureIntent(this);
			} else {
				Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * This function is triggered when picture is coming from the system, from gallery or image taken.
	 * The params are default, as this method overridden.
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap photo = CameraUtilities.getPicture(this, requestCode, resultCode, data);
		if (photo != null) {
			userPicture = photo;
			imageView.setImageBitmap(userPicture);
		} else {
			System.out.println("No Photo");
		}
	}
}
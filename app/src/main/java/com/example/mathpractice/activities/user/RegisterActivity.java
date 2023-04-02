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

public class RegisterActivity extends Activity {

	private static final int MY_CAMERA_PERMISSION_CODE = 100;

	private ImageView imageView;
	private Bitmap userPicture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		this.imageView = (ImageView) this.findViewById(R.id.myPhoto);
		Button takePhotoButton = (Button) this.findViewById(R.id.takePhotoButton);
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
		Button pickPhotoFromGalleryButton = (Button) this.findViewById(R.id.PickFromGalleryButton);
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
			userPicture = PictureUtilities.rotateBitmap(userPicture, 90f);
			imageView.setImageBitmap(userPicture);
		});
	}

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

	// this function is triggered when user
	// selects the image from the imageChooser
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap photo = CameraUtilities.getPicture(this, requestCode, resultCode, data);
		if (photo != null) {
			userPicture = photo;
			imageView.setImageBitmap(userPicture);
		}
	}
}
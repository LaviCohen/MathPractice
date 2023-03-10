package com.example.mathpractice.activities.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.mathpractice.R;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

import java.io.IOException;

public class RegisterActivity extends Activity {
	private static final int CAMERA_REQUEST = 1888;
	private ImageView imageView;
	private static final int MY_CAMERA_PERMISSION_CODE = 100;
	private Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		this.imageView = (ImageView) this.findViewById(R.id.myPhoto);
		Button photoButton = (Button) this.findViewById(R.id.takePhotoButton);
		photoButton.setOnClickListener(v -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
				} else {
					imageChooser();
//					ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
//					Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//					startActivityForResult(cameraIntent, CAMERA_REQUEST);
				}
			} else {
				imageChooser();
			}
		});
		EditText username = findViewById(R.id.editTextUsername);
		EditText password = findViewById(R.id.editTextPassword);
		Button finish = findViewById(R.id.finish_reg_button);
		finish.setOnClickListener(v -> {
			DataBaseHelper dbh = new DataBaseHelper(RegisterActivity.this);
			if (bitmap == null) {
				Toast.makeText(RegisterActivity.this, "You must take a photo", Toast.LENGTH_SHORT).show();
				return;
			}
			if (username.getText().toString().contains(" ")) {
				Toast.makeText(RegisterActivity.this, "Username can't contain spaces", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!dbh.execSQLForReading("SELECT * FROM users WHERE username = '" + username.getText().toString() + "';").isAfterLast()) {
				Toast.makeText(RegisterActivity.this, "Your username is already exists", Toast.LENGTH_SHORT).show();
				return;
			}
			dbh.createNewUser(
					username.getText().toString(), password.getText().toString(), bitmap, 0);
			LoginActivity.setUser(RegisterActivity.this, username.getText().toString());
			startActivity(new Intent(RegisterActivity.this, UserPageActivity.class));
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_CAMERA_PERMISSION_CODE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			} else {
				Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
			}
		}
	}

	protected void oldonActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			bitmap = photo;
			System.out.println("Photo size:" + photo.getWidth() + ", " + photo.getHeight());
			imageView.setImageBitmap(photo);
		}
	}
	int SELECT_PICTURE = 200;
	private void imageChooser() {

		// create an instance of the
		// intent of the type image
		Intent i = new Intent();
		i.setType("image/*");
		i.setAction(Intent.ACTION_GET_CONTENT);

		// pass the constant to compare it
		// with the returned requestCode
		startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
	}

	// this function is triggered when user
	// selects the image from the imageChooser
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			// compare the resultCode with the
			// SELECT_PICTURE constant
			if (requestCode == SELECT_PICTURE) {
				// Get the url of the image from data
				Uri selectedImageUri = data.getData();
				if (null != selectedImageUri) {
					imageView.setImageURI(selectedImageUri);
					try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
						System.out.println("Photo size:" + bitmap.getWidth() + ", " + bitmap.getHeight());
					} catch (IOException e) {
						Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}
}
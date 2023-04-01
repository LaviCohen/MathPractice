package com.example.mathpractice.activities.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.mathpractice.R;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends Activity {
	private static final int CAMERA_REQUEST = 1888;
	private static final int STORAGE_CAMERA_REQUEST = 188;
	private static final int SELECT_PICTURE = 200;
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
					dispatchTakePictureIntent();
				}
			} else {
				ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
				dispatchTakePictureIntent();
			}
		});
		Button pickPhotoFromGalleryButton = (Button) this.findViewById(R.id.PickFromGalleryButton);
		pickPhotoFromGalleryButton.setOnClickListener(v -> {
			System.out.println("Picking photo");
			imageChooser();
		});
		EditText username = findViewById(R.id.editTextUsername);
		EditText password = findViewById(R.id.editTextPassword);
		Button finish = findViewById(R.id.finish_reg_button);
		finish.setOnClickListener(v -> {
			register(username.getText().toString(), password.getText().toString());
		});
		Button rotateButton = findViewById(R.id.rotateButton);
		rotateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				userPicture = rotateBitmap(userPicture, 90f);
				imageView.setImageBitmap(userPicture);
			}
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
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
			} else {
				Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
			}
		}
	}
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

		if (!(resultCode == RESULT_OK)) {
			System.out.println("Error in request " + requestCode + ", result " + resultCode + "\n" + data);
			return;
		}

		if (requestCode == SELECT_PICTURE) {
			// compare the resultCode with the
			// SELECT_PICTURE constant
			if (requestCode == SELECT_PICTURE) {
				// Get the url of the image from data
				Uri selectedImageUri = data.getData();
				if (null != selectedImageUri) {
					imageView.setImageURI(selectedImageUri);
					try {
						userPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
						System.out.println("Photo size:" + userPicture.getWidth() + ", " + userPicture.getHeight());
					} catch (IOException e) {
						Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else if (requestCode == CAMERA_REQUEST) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			userPicture = photo;
			System.out.println("Photo size:" + photo.getWidth() + ", " + photo.getHeight());
			imageView.setImageBitmap(photo);
		} else if (requestCode == STORAGE_CAMERA_REQUEST) {
			Bitmap photo = BitmapFactory.decodeFile(currentPath);
			userPicture = photo;
			System.out.println("Photo size:" + photo.getWidth() + ", " + photo.getHeight());
			imageView.setImageBitmap(photo);
		}
	}
	public static Bitmap rotateBitmap(Bitmap source, float angle)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
						"com.example.android.fileprovider",
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, STORAGE_CAMERA_REQUEST);
			}
		}
	}
	String currentPath = null;
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		currentPath = image.getAbsolutePath();
		return image;
	}
}
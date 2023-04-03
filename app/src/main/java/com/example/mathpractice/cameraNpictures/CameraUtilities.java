package com.example.mathpractice.cameraNpictures;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUtilities {

	private static final int CAMERA_REQUEST = 1888;
	private static final int STORAGE_CAMERA_REQUEST = 188;
	private static final int SELECT_PICTURE = 200;


	public static Bitmap getPicture(Activity activity, int requestCode, int resultCode, Intent data){
		if (!(resultCode == RESULT_OK)) {
			System.out.println("Error in request " + requestCode + ", result " + resultCode + "\n" + data);
			return null;
		}
		if (requestCode == SELECT_PICTURE) {
			// Get the url of the image from data
			Uri selectedImageUri = data.getData();
			if (null != selectedImageUri) {
				try {
					return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
				} catch (IOException e) {
					Toast.makeText(activity, "Failed to load image", Toast.LENGTH_SHORT).show();
					return null;
				}
			}
		} else if (requestCode == CAMERA_REQUEST) {
			return (Bitmap) data.getExtras().get("data");
		} else if (requestCode == STORAGE_CAMERA_REQUEST) {
			return BitmapFactory.decodeFile(PreferenceManager.getDefaultSharedPreferences(activity).getString("currentPath", ""));
		}
		return null;
	}
	public static void pickPictureFromGallery(Activity activity) {
		// create an instance of the
		// intent of the type image
		Intent i = new Intent();
		i.setType("image/*");
		i.setAction(Intent.ACTION_GET_CONTENT);
		// pass the constant to compare it
		// with the returned requestCode
		activity.startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
	}
	@SuppressLint("QueryPermissionsNeeded")
	public static void dispatchTakePictureIntent(Activity activity) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile(activity);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(activity,
						"com.example.android.fileprovider",
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				activity.startActivityForResult(takePictureIntent, STORAGE_CAMERA_REQUEST);
			}
		}
	}
	private static File createImageFile(Activity activity) throws IOException {
		// Create an image file name
		@SuppressLint("SimpleDateFormat")
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("currentPath", image.getAbsolutePath()).commit();
		return image;
	}
}

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

/**
 * Static utility class for camera usage and integration.
 * */
public class CameraUtilities {

	/**
	 * @deprecated
	 * Request code for camera intent (no usage of public storage).
	 * */
	private static final int CAMERA_REQUEST_CODE = 1888;
	/**
	 * Request code for camera intent (usage of public storage, full size image).
	 * */
	private static final int STORAGE_CAMERA_REQUEST_CODE = 188;
	/**
	 * Request code for gallery intent.
	 * */
	private static final int PICK_FROM_GALLERY_REQUEST_CODE = 200;

	/**
	 * This method invoked from onActivityResult method and gets its params.
	 * @param activity the activity which invoked this method.
	 * @param requestCode the request code of what the method called from.
	 * @param resultCode the result code of what the method called from.
	 * @param data the intent the method got from the external activity.
	 * @return bitmap of the picture which the external activity gave.
	 * */
	public static Bitmap getPicture(Activity activity, int requestCode, int resultCode, Intent data){
		if (!(resultCode == RESULT_OK)) {
			System.out.println("Error in request " + requestCode + ", result " + resultCode + "\n" + data);
			return null;
		}
		if (requestCode == PICK_FROM_GALLERY_REQUEST_CODE) {
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
		} else if (requestCode == CAMERA_REQUEST_CODE) {
			return (Bitmap) data.getExtras().get("data");
		} else if (requestCode == STORAGE_CAMERA_REQUEST_CODE) {
			return BitmapFactory.decodeFile(PreferenceManager.getDefaultSharedPreferences(activity).getString("currentPath", ""));
		}
		return null;
	}

	/**
	 * This method open the system's gallery image picker.
	 * @param activity the activity which invoked the method.
	 * */
	public static void pickPictureFromGallery(Activity activity) {
		// create an instance of the
		// intent of the type image
		Intent i = new Intent();
		i.setType("image/*");
		i.setAction(Intent.ACTION_GET_CONTENT);
		// pass the constant to compare it
		// with the returned requestCode
		activity.startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_FROM_GALLERY_REQUEST_CODE);
	}

	/**
	 * This method open the system's camera.
	 * @param activity the activity which invoked the method.
	 * */
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
				activity.startActivityForResult(takePictureIntent, STORAGE_CAMERA_REQUEST_CODE);
			}
		}
	}

	/**
	 * This method create in=mage file to store the image which will be taken at.
	 * */
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

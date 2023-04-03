package com.example.mathpractice.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.cameraNpictures.PictureUtilities;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

/**
 * This activity is the activity to show the data for the current user and to create and login into other users.
 * */
public class UserPageActivity extends AppCompatActivity {

	/**
	 * The current user's username.
	 * */
	private String username;

	/**
	 * THe base user's profile image, with no hats.
	 * */
	private Bitmap baseUsersImage;
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_page);
		setTitle("User");
		username = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("user", "Local");
		((TextView)findViewById(R.id.username_field)).setText(username);
		((TextView) findViewById(R.id.user_level_field)).setText("Level: " + SettingsActivity.getUserGeneralLevel(this));
		baseUsersImage = null;
		if (!username.equals("Local")) {
			Cursor c = new DataBaseHelper(this).execSQLForReading(
					"SELECT profile_image FROM users WHERE username = '" + username + "';");
			c.moveToFirst();
			@SuppressLint("Range") byte[] bitmapArr = c.getBlob(c.getColumnIndex("profile_image"));
			c.close();
			baseUsersImage = BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length);
		}
		if (baseUsersImage != null) {
			updateUserImageNHat();
			int userLevel = SettingsActivity.getUserGeneralLevel(this);
			LinearLayout hatLinearLayout, parentHatsLinearLayout = findViewById(R.id.llchooseHat);
			int[] hats = new int[]{R.mipmap.ic_brown_hat_foreground, R.mipmap.ic_black_hat_foreground,
					R.mipmap.ic_pirates_hat_foreground, R.mipmap.ic_magician_hat_foreground,
					R.mipmap.ic_crown_foreground};
			for (int i = 0; i < parentHatsLinearLayout.getChildCount(); i++){
				hatLinearLayout = (LinearLayout) parentHatsLinearLayout.getChildAt(i);
				ImageView hatPicture =
						((ImageView)((FrameLayout)hatLinearLayout.getChildAt(0)).getChildAt(0));
				if (userLevel < i + 1) {
					PictureUtilities.makeBlackNwhite(hatPicture);
				} else {
					((FrameLayout)hatLinearLayout.getChildAt(0)).getChildAt(1).
							setVisibility(View.INVISIBLE);
					int finalI = i;
					hatLinearLayout.setOnClickListener(v ->
							changeUserHat(hats[finalI]));
				}
			}
		}
		Button register = findViewById(R.id.newUser);
		register.setOnClickListener(v ->
				startActivity(new Intent(UserPageActivity.this, RegisterActivity.class)));
		Button login = findViewById(R.id.login_button);
		login.setOnClickListener(v ->
				startActivity(new Intent(UserPageActivity.this, LoginActivity.class)));
	}

	/**
	 * Changes user's hat.
	 * Invoking setUserHatId and updateUserImageNHat.
	 * @param id the id for the hat to change to.
	 * */
	private void changeUserHat(int id) {
		setUserHatId(id);
		updateUserImageNHat();
	}

	/**
	 * Changes user's hat only on the database.
	 * @param id the id for the hat to change to.
	 * */
	private void setUserHatId(int id) {
		new DataBaseHelper(this).execSQLForWriting(
				"UPDATE users SET hat_ID = " + id + " WHERE username = '" + username + "';");
	}

	/**
	 * Update the user's profile image displayed with the required hat, as in the database.
	 * */
	private void updateUserImageNHat() {
		System.out.println("Updating hat");
		Bitmap imageToDisplay = PictureUtilities.putHat(this, username, baseUsersImage);
		ImageView profileImage = findViewById(R.id.profile_image);
		profileImage.setImageBitmap(imageToDisplay);
	}

	/**
	 * Method to go back to practice activity when back navigation button is pressed.
	 * */
	@Override
	public void onBackPressed() {
		gotoPracticeActivity();
	}

	/**
	 * Method to go back to practice activity when upper menu back button is pressed.
	 * */
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.back_menu_option) {
			gotoPracticeActivity();
		}
		return true;
	}

	/**
	 * Method to go back to practice activity, called when back button is pressed.
	 * */
	public void gotoPracticeActivity(){
		Intent i = new Intent(UserPageActivity.this, PracticeActivity.class);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.back_menu, menu);
		return true;
	}
}
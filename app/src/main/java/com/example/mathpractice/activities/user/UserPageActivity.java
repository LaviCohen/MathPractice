package com.example.mathpractice.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.cameraNpictures.PictureUtilities;
import com.example.mathpractice.reminder.MyAlarmManager;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;
import com.example.mathpractice.sqlDataBase.UsersHelper;

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
			Cursor c = new UsersHelper(this).execSQLForReading(
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
			boolean isLocalUser = username.equals("Local");
			for (int i = 0; i < parentHatsLinearLayout.getChildCount(); i++){
				hatLinearLayout = (LinearLayout) parentHatsLinearLayout.getChildAt(i);
				ImageView hatPicture =
						((ImageView)((FrameLayout)hatLinearLayout.getChildAt(0)).getChildAt(0));
				if (userLevel < i + 1 || isLocalUser) {
					PictureUtilities.makeBlackNwhite(hatPicture);
				} else {
					((FrameLayout)hatLinearLayout.getChildAt(0)).getChildAt(1).
							setVisibility(View.INVISIBLE);
					int finalI = i;
					hatLinearLayout.setOnClickListener(v ->
							{
								Cursor c = new UsersHelper(UserPageActivity.this).execSQLForReading(
										"SELECT hat_ID FROM users WHERE username = '" + username + "';");
								c.moveToFirst();
								@SuppressLint("Range") int id = c.getInt(c.getColumnIndex("hat_ID"));
								if (id == hats[finalI]) {
									changeUserHat(-1);
								} else {
									changeUserHat(hats[finalI]);
								}
							});
				}
			}
		}
		Button register = findViewById(R.id.newUser);
		register.setOnClickListener(v ->
				startActivity(new Intent(UserPageActivity.this, RegisterActivity.class)));
		Button login = findViewById(R.id.login_button);
		login.setOnClickListener(view -> {
					Dialog d = new Dialog(UserPageActivity.this);
					d.setTitle("Login");
					d.setContentView(R.layout.login_dialog_layout);
					Button loginDialogButton = d.findViewById(R.id.login_button);
					Button cancel = d.findViewById(R.id.cancel_button);
					EditText usernameEditText = d.findViewById(R.id.editTextUsername);
					EditText passwordEditText = d.findViewById(R.id.editTextPassword);

					loginDialogButton.setOnClickListener(v -> {
						UsersHelper usersHelper = new UsersHelper(UserPageActivity.this);
						if (usersHelper.execSQLForReading("SELECT * FROM users WHERE username = '" + usernameEditText.getText().toString() + "';").isAfterLast()) {
							Toast.makeText(UserPageActivity.this, "This username isn't exists", Toast.LENGTH_SHORT).show();
							return;
						}
						if (!tryToLogin(usernameEditText.getText().toString(), passwordEditText.getText().toString(), usersHelper)) {
							Toast.makeText(UserPageActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
							return;
						}
						setUser(this, usernameEditText.getText().toString());
						startActivity(new Intent(UserPageActivity.this, UserPageActivity.class));
						d.dismiss();
					});
					cancel.setOnClickListener(v -> d.dismiss());
					d.show();
				});
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
		new UsersHelper(this).execSQLForWriting(
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
	@SuppressLint("Range")
	public boolean tryToLogin(String username, String password, DataBaseHelper dbh) {
		Cursor c = dbh.execSQLForReading("SELECT password FROM users WHERE username = '" + username + "';");
		c.moveToFirst();
		if (!c.getString(c.getColumnIndex("password")).equals(password)) {
			c.close();
			return false;
		}
		c.close();
		return true;
	}
	public static void setUser(Context context, String username){
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("user", username);
		editor.apply();
	}
}
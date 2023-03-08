package com.example.mathpractice.activities.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.activities.settings.SettingsActivity;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;

public class UserPageActivity extends AppCompatActivity {

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_page);
		setTitle("User");
		String username = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("user", "Local");
		((TextView)findViewById(R.id.username_field)).setText(username);
		((TextView) findViewById(R.id.user_level_field)).setText("Level: " + SettingsActivity.getUserGeneralLevel(this));
		Bitmap usersImage = null;
		if (!username.equals("Local")) {
			Cursor c = new DataBaseHelper(this).execSQLForReading(
					"SELECT profile_image FROM users WHERE username = '" + username + "';");
			c.moveToFirst();
			@SuppressLint("Range") byte[] bitmapArr = c.getBlob(c.getColumnIndex("profile_image"));
			c.close();
			usersImage = BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length);
		}
		if (usersImage != null) {
			Cursor c = new DataBaseHelper(this).execSQLForReading(
					"SELECT hat_ID FROM users WHERE username = '" + username + "';");
			c.moveToFirst();
			@SuppressLint("Range") int id = c.getInt(c.getColumnIndex("hat_ID"));
			c.close();
			if (id != -1) {
				System.out.println(id);
				Drawable drawable = getResources().getDrawable(R.mipmap.ic_crown);
				Bitmap rawHat = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
				drawable.draw(new Canvas(rawHat));

				System.out.println(rawHat);
				Bitmap hat = Bitmap.createScaledBitmap(rawHat, 100, 100, false);
				Bitmap bmOverlay = Bitmap.createBitmap(usersImage.getWidth(), usersImage.getHeight(), usersImage.getConfig());
				Canvas canvas = new Canvas(bmOverlay);
				canvas.drawBitmap(usersImage, 0, 0, null);
				c = new DataBaseHelper(this).execSQLForReading(
						"SELECT hat_x FROM users WHERE username = '" + username + "';");
				c.moveToFirst();
				@SuppressLint("Range") int hatX = c.getInt(c.getColumnIndex("hat_x"));
				c.close();
				c = new DataBaseHelper(this).execSQLForReading(
						"SELECT hat_y FROM users WHERE username = '" + username + "';");
				c.moveToFirst();
				@SuppressLint("Range") int hatY = c.getInt(c.getColumnIndex("hat_y"));
				c.close();
				int x = hatX - hat.getWidth()/2;
				int y = hatY - hat.getHeight();
				System.out.println(hatX + "," + hatY + ", " +  x + ", " + y);
				canvas.drawBitmap(hat, x, y, null);
			}
			ImageView profileImage = findViewById(R.id.profile_image);
			profileImage.setImageBitmap(usersImage);
		}
		Button register = findViewById(R.id.newUser);
		register.setOnClickListener(v ->
				startActivity(new Intent(UserPageActivity.this, RegisterActivity.class)));
		Button login = findViewById(R.id.login_button);
		login.setOnClickListener(v ->
				startActivity(new Intent(UserPageActivity.this, LoginActivity.class)));
	}

	@Override
	public void onBackPressed() {
		gotoPracticeActivity();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.back_menu_option) {
			gotoPracticeActivity();
		}
		return true;
	}

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
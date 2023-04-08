package com.example.mathpractice.activities.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mathpractice.R;
import com.example.mathpractice.sqlDataBase.DataBaseHelper;
import com.example.mathpractice.sqlDataBase.UsersHelper;

public class LoginActivity extends AppCompatActivity {

	@SuppressLint("Range")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		EditText username = findViewById(R.id.editTextUsername);
		EditText password = findViewById(R.id.editTextPassword);
		Button login = findViewById(R.id.login_button);
		login.setOnClickListener(v -> {
			UsersHelper usersHelper = new UsersHelper(LoginActivity.this);
			if (usersHelper.execSQLForReading("SELECT * FROM users WHERE username = '" + username.getText().toString() + "';").isAfterLast()) {
				Toast.makeText(LoginActivity.this, "This username isn't exists", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!tryToLogin(username.getText().toString(), password.getText().toString(), usersHelper)) {
				Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
				return;
			}
			setUser(this, username.getText().toString());
			startActivity(new Intent(LoginActivity.this, UserPageActivity.class));
		});
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
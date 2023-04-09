package com.example.mathpractice.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;

/**
 * This activity is for opening animation only, and have no other use (except for the button to move to the next
 * activity - {@link PracticeActivity}).
 * */
public class OpenActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (PreferenceManager.getDefaultSharedPreferences(this).
				getString("skipOpeningScreen", "false").equals("true")) {
			startActivity(new Intent(OpenActivity.this, PracticeActivity.class));
			return;
		}
		setContentView(R.layout.activity_main);
		ImageView imageView = findViewById(R.id.ivAppLogo);
		ObjectAnimator ivAnimatorTransX = ObjectAnimator.ofFloat(imageView, "translationX", 0f);
		ObjectAnimator ivAnimatorAlpha = ObjectAnimator.ofFloat(imageView, "Alpha", 1f);
		ivAnimatorAlpha.setDuration(5000);
		ivAnimatorTransX.setDuration(5000);

		TextView textView = findViewById(R.id.tvSubText);
		ObjectAnimator tvAnimatorTransY = ObjectAnimator.ofFloat(textView, "translationY", 0f);
		ObjectAnimator tvAnimatorAlpha = ObjectAnimator.ofFloat(textView, "Alpha", 1f);
		tvAnimatorAlpha.setDuration(5000);
		tvAnimatorTransY.setDuration(5000);
		Button button = findViewById(R.id.bLetsGo);
		button.setOnClickListener(view -> startActivity(new Intent(OpenActivity.this, PracticeActivity.class)));
		ivAnimatorAlpha.start();
		ivAnimatorTransX.start();
		tvAnimatorAlpha.start();
		tvAnimatorTransY.start();
	}
}
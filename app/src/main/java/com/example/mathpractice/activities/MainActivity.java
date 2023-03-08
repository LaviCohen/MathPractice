package com.example.mathpractice.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mathpractice.R;
import com.example.mathpractice.activities.practice.PracticeActivity;
import com.example.mathpractice.service.ReminderService;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startActivity(new Intent(this, PracticeActivity.class));
	}
}
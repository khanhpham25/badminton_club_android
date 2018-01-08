package com.example.asus.badminton_club;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class AboutActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, AboutActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView image = findViewById(R.id.imgVAbout);
        image.setImageResource(R.drawable.hanoiuniversity);
    }
}

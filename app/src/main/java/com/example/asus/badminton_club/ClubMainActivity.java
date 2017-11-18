package com.example.asus.badminton_club;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.screen.authentication.LoginActivity;
import com.example.asus.badminton_club.utils.PassClubObjectInterface;

public class ClubMainActivity extends AppCompatActivity{

    public static Intent getInstance(Context context) {
        return new Intent(context, ClubMainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_main);

        Club selectedClub = (Club) getIntent().getSerializableExtra("selected_club");
        TextView tvClubName = findViewById(R.id.textViewShowClubName);
        tvClubName.setText(selectedClub.getName());
    }
}

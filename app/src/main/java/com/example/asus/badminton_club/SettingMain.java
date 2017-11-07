package com.example.asus.badminton_club;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.asus.badminton_club.screen.setting.SettingActivity;

public class SettingMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
    }
    public void accouasdfnt(View View) {
        Intent gotoAccount = new Intent(this, SettingActivity.class);
        startActivity(gotoAccount);
    }
}

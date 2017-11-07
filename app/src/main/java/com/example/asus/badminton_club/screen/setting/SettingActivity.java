package com.example.asus.badminton_club.screen.setting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.asus.badminton_club.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
    }
    public void edit(View View) {
        Intent gotoEdit = new Intent(this, SettingAccountEditActivity.class);
        startActivity(gotoEdit);
    }
}

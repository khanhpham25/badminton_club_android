package com.example.asus.badminton_club;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.asus.badminton_club.screen.setting.SettingActivity;

public class SettingMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_main);
    }
    public void accountClick(View View) {
        startActivity(SettingActivity.getInstance(SettingMain.this));
    }

    public void developingClick(View view) {
        Toast.makeText(SettingMain.this, "This function will be coming soon in our next update!", Toast.LENGTH_LONG);
    }
}

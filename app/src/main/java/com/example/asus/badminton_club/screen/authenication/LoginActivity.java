package com.example.asus.badminton_club.screen.authenication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.asus.badminton_club.CentralPageActivity;
import com.example.asus.badminton_club.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void signUp(View View) {
        Intent gotoSignup = new Intent(this, SignUp.class);
        startActivity(gotoSignup);
    }
    public void forgot(View View) {
        Intent gotoForgot = new Intent(this, ForgotPassword.class);
        startActivity(gotoForgot);
    }
    public void login(View View) {
        Intent gotoLogin = new Intent(this, CentralPageActivity.class);
        startActivity(gotoLogin);
    }
}

package com.example.asus.badminton_club.screen.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.screen.authentication.LoginActivity;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SettingActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    private CompositeSubscription mCompositeSubscription;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        mCompositeSubscription = new CompositeSubscription();

        currentUser = new UserLocalDataSource(SettingActivity.this).getCurrentUser();

        TextView txtVName = (TextView) findViewById(R.id.textViewName);
        TextView txtVEmail = (TextView) findViewById(R.id.textViewEmail);
        TextView txtVMobile = (TextView) findViewById(R.id.textViewMobile);
        TextView txtVBadmintonLevel = (TextView) findViewById(R.id.textViewBadmintonLevel);
        TextView txtVMainRacket = (TextView) findViewById(R.id.textViewMainRackquet);

        txtVName.setText("Name: " + currentUser.getName());
        txtVEmail.setText("Email: " + currentUser.getEmail());
        txtVMobile.setText("Mobile: " + currentUser.getMobile());
        txtVMainRacket.setText("Racket: " + currentUser.getMainRackquet());
        String level = "";
        if (currentUser.getBadmintonLevel() == null || currentUser.getBadmintonLevel() == 0) {
            level = "Beginner";
        } else if (currentUser.getBadmintonLevel() == 1) {
            level = "Amateur";
        } else if (currentUser.getBadmintonLevel() == 2) {
            level = "Professional";
        }
        txtVBadmintonLevel.setText("Skill Level:" + level);

    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    public void edit(View View) {
        Intent gotoEdit = new Intent(this, SettingAccountEditActivity.class);
        startActivity(gotoEdit);
    }

    public void logout(View View) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Logout")
                .setMessage("Do you really want to log out?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String auth_token = currentUser.getAuthToken();
                        Subscription subscription = AppServiceClient.getInstance().logout(auth_token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<BaseResponse>() {
                                    @Override
                                    public void call(BaseResponse response) {
                                        Toast.makeText(SettingActivity.this, "Sign out succesfully!", Toast.LENGTH_SHORT).show();
                                        new UserLocalDataSource(SettingActivity.this).clearUser();
                                        startActivity(LoginActivity.getInstance(SettingActivity.this));
                                        finish();
                                    }
                                }, new SafetyError() {
                                    @Override
                                    public void onSafetyError(BaseException error) {
                                        Toast.makeText(SettingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                        mCompositeSubscription.add(subscription);
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }
}

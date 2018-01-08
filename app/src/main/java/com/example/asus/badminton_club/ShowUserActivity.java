package com.example.asus.badminton_club;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.utils.Constant;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ShowUserActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, ShowUserActivity.class);
    }

    private CompositeSubscription mCompositeSubscription;
    private User currentUser;
    private ProgressDialog mProgressDialog;
    private ImageView imgViewUserAva;
    private TextView txtVName;
    private TextView txtVEmail;
    private TextView txtVMobile;
    private TextView txtVBadmintonLevel;
    private TextView txtVMainRacket;
    private TextView txtVGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        mCompositeSubscription = new CompositeSubscription();

        mProgressDialog = new ProgressDialog(ShowUserActivity.this);
        mProgressDialog.setTitle("Data");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);

        imgViewUserAva = findViewById(R.id.ivShowUserAvatar);
        txtVName = findViewById(R.id.tvShowUserName);
        txtVEmail = findViewById(R.id.tvShowUserEmail);
        txtVMobile = findViewById(R.id.tvShowUserMobile);
        txtVBadmintonLevel = findViewById(R.id.tvShowUserBadmintonLvl);
        txtVMainRacket = findViewById(R.id.tvShowUserMainRackquet);
        txtVGender = findViewById(R.id.tvShowUserGender);

        currentUser = (User) getIntent().getSerializableExtra("selected_user");

        loadUser();
    }

    public void loadUser() {
        mProgressDialog.show();

        Subscription subscription = AppServiceClient
                .getInstance()
                .getUserInfo(currentUser.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> user) {
                        currentUser = user.getData();

                        if (currentUser.getAvatar() == null || currentUser.getAvatar().getUrl() == null || currentUser.getAvatar().getUrl().equals("")) {
                            imgViewUserAva.setImageDrawable(getDrawable(R.drawable.loginpic));
                        } else {
                            Glide.with(ShowUserActivity.this).load(Constant.BASE_URL + currentUser.getAvatar().getUrl()).into(imgViewUserAva);
                        }

                        txtVName.setText(currentUser.getName());
                        txtVEmail.setText(currentUser.getEmail());
                        if (currentUser.getMobile() != null) {
                            txtVMobile.setText(currentUser.getMobile());
                        }

                        if (currentUser.getMainRackquet() != null) {
                            txtVMainRacket.setText(currentUser.getMainRackquet());
                        }
                        String level = "";
                        if (currentUser.getBadmintonLevel() == null || currentUser.getBadmintonLevel() == 1) {
                            level = "Beginner";
                        } else if (currentUser.getBadmintonLevel() == 2) {
                            level = "Amateur";
                        } else if (currentUser.getBadmintonLevel() == 3) {
                            level = "Professional";
                        }
                        txtVBadmintonLevel.setText(level);

                        String gender = "";
                        if (currentUser.getGender() == null || currentUser.getGender() == 0) {
                            gender = "";
                        }else if (currentUser.getGender() == 1){
                            gender = "Male";
                        } else if (currentUser.getGender() == 2) {
                            gender = "Female";
                        } else if (currentUser.getGender() == 3) {
                            gender = "Other";
                        }
                        txtVGender.setText(gender);

                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ShowUserActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mCompositeSubscription.add(subscription);
    }
}

package com.example.asus.badminton_club.screen.authentication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.badminton_club.CentralPageActivity;
import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class LoginActivity extends AppCompatActivity {

    public static Intent getInstance(Context context) {
        return new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    private CompositeSubscription mCompositeSubscription;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCompositeSubscription = new CompositeSubscription();
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "Huy" + loginResult.getAccessToken(), Toast.LENGTH_SHORT);
                Profile.getCurrentProfile().getFirstName();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Huy", Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        //Checked If Login Or Not
        User currentUser = new UserLocalDataSource(LoginActivity.this).getCurrentUser();

        if (currentUser != null) {
            Toast.makeText(LoginActivity.this, "Sign in succesfully!", Toast.LENGTH_SHORT).show();
            startActivity(CentralPageActivity.getInstance(LoginActivity.this));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void signUp(View View) {
        startActivity(SignUp.getInstance(LoginActivity.this));
    }

    public void forgot(View View) {
        Intent gotoForgot = new Intent(this, ForgotPassword.class);
        startActivity(gotoForgot);
    }

    public void login(View View) {
        EditText txtEmail = (EditText) findViewById(R.id.txtLoginEmail);
        EditText txtPassword = (EditText) findViewById(R.id.txtLoginPassword);

        final String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        Subscription subscription = AppServiceClient.getInstance().login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> user) {
                        Toast.makeText(LoginActivity.this, "Sign in succesfully!", Toast.LENGTH_SHORT).show();
                        new UserLocalDataSource(LoginActivity.this).saveUser(user.getData());
                        startActivity(CentralPageActivity.getInstance(LoginActivity.this));
                        finish();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }
}

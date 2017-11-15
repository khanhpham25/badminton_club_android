package com.example.asus.badminton_club.screen.authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCompositeSubscription = new CompositeSubscription();
        callbackManager = CallbackManager.Factory.create();
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setTitle("Login");
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.setIndeterminate(false);
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Bundle bFacebookData = getFacebookData(object);
                        String face_email = bFacebookData.getString("email");
                        String face_name = bFacebookData.getString("name");
                        String auth_token = loginResult.getAccessToken().getToken().trim();
                        mProgressDialog.show();
                        Subscription subscription = AppServiceClient.getInstance().omniauth_login(face_email,
                                face_name, "facebook", auth_token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<BaseResponse<User>>() {
                                    @Override
                                    public void call(BaseResponse<User> user) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Sign in succesfully!", Toast.LENGTH_SHORT).show();
                                        new UserLocalDataSource(LoginActivity.this).saveUser(user.getData());
                                        startActivity(CentralPageActivity.getInstance(LoginActivity.this));
                                        finish();
                                    }
                                }, new SafetyError() {
                                    @Override
                                    public void onSafetyError(BaseException error) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        mCompositeSubscription.add(subscription);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
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
        EditText txtEmail = findViewById(R.id.txtLoginEmail);
        EditText txtPassword = findViewById(R.id.txtLoginPassword);

        final String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        mProgressDialog.show();

        Subscription subscription = AppServiceClient.getInstance().login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> user) {
                        mProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Sign in succesfully!", Toast.LENGTH_SHORT).show();
                        new UserLocalDataSource(LoginActivity.this).saveUser(user.getData());
                        startActivity(CentralPageActivity.getInstance(LoginActivity.this));
                        finish();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("name"))
                bundle.putString("name", object.getString("name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        }
        catch(JSONException e) {
            Log.d("Error","Error parsing JSON");
        }
        return null;
    }
}

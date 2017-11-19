package com.example.asus.badminton_club.screen.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asus.badminton_club.CentralPageActivity;
import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.screen.authentication.LoginActivity;
import com.example.asus.badminton_club.utils.Constant;
import com.example.asus.badminton_club.utils.RealPathUtil;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class SettingActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    public final static int PICK_IMAGE_REQUEST = 1;
    public final static int READ_EXTERNAL_REQUEST = 2;

    private CompositeSubscription mCompositeSubscription;
    private AccessTokenTracker accessTokenTracker;
    private User currentUser;
    private ImageView imgViewUserAva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        mCompositeSubscription = new CompositeSubscription();
        currentUser = new UserLocalDataSource(SettingActivity.this).getCurrentUser();

        imgViewUserAva = findViewById(R.id.imageViewUserAvatar);
        TextView txtVName = findViewById(R.id.textViewName);
        TextView txtVEmail = findViewById(R.id.textViewEmail);
        TextView txtVMobile = findViewById(R.id.textViewMobile);
        TextView txtVBadmintonLevel = findViewById(R.id.textViewBadmintonLevel);
        TextView txtVMainRacket = findViewById(R.id.textViewMainRackquet);
        TextView txtVGender = findViewById(R.id.textViewGender);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
            }
        };
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (currentUser.getProvider() != null && currentUser.getProvider().equals("facebook")) {
            GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Bundle bFacebookData = getFacebookData(object);
                    Glide.with(SettingActivity.this).load(bFacebookData.getString("profile_pic")).into(imgViewUserAva);
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, email");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            if (currentUser.getAvatar() == null || currentUser.getAvatar().getUrl() == null || currentUser.getAvatar().getUrl().equals("")) {
                imgViewUserAva.setImageDrawable(getDrawable(R.drawable.loginpic));
            } else {
                Glide.with(SettingActivity.this).load(Constant.BASE_URL + currentUser.getAvatar().getUrl()).into(imgViewUserAva);
            }
        }

        
        Button buttonSelectImage = findViewById(R.id.button_select_image);
        if (currentUser.getProvider() != null && currentUser.getProvider().equals("facebook")) {
            buttonSelectImage.setVisibility(View.INVISIBLE);
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

    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    public void edit(View View) {
        startActivity(SettingAccountEditActivity.getInstance(SettingActivity.this));
        finish();
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
                                        if (currentUser.getProvider() != null && currentUser.getProvider().equals("facebook")) {
                                            LoginManager.getInstance().logOut();
                                            new UserLocalDataSource(SettingActivity.this).clearUser();
                                            startActivity(LoginActivity.getInstance(SettingActivity.this));
                                            finish();
                                        } else {
                                            Toast.makeText(SettingActivity.this, "Sign out succesfully!", Toast.LENGTH_SHORT).show();
                                            new UserLocalDataSource(SettingActivity.this).clearUser();
                                            startActivity(LoginActivity.getInstance(SettingActivity.this));
                                            finish();
                                        }
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

    public void uploadAvatar(View view) {
        requestPermissionAndPickImage();
    }

    private void requestPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            pickImage();
            return;
        }

        int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, READ_EXTERNAL_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != READ_EXTERNAL_REQUEST) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            Toast.makeText(SettingActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image to Upload"),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            Uri uri = data.getData();
            uploadFiles(uri);
        }
    }

    public void uploadFiles(Uri uri) {
        if (uri == null) return;

        File file = new File(RealPathUtil.getRealPathFromURI_API19(SettingActivity.this, uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                file);

        MultipartBody.Part filePart =
               MultipartBody.Part.createFormData("user[avatar]", file.getName(), requestBody);

        Subscription subscription = AppServiceClient.getInstance().uploadUserAvatar(currentUser.getId(), filePart,
                                                                                currentUser.getAuthToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> user) {
                        Toast.makeText(SettingActivity.this, "Upload succesfully!", Toast.LENGTH_SHORT).show();
                        Glide.with(SettingActivity.this).load(Constant.BASE_URL + user.getData().getAvatar().getUrl())
                                                                .into(imgViewUserAva);
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        Toast.makeText(SettingActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

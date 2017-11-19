package com.example.asus.badminton_club;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.screen.setting.SettingActivity;
import com.example.asus.badminton_club.utils.Constant;
import com.example.asus.badminton_club.utils.RealPathUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class ClubMainActivity extends AppCompatActivity{

    public static Intent getInstance(Context context) {
        return new Intent(context, ClubMainActivity.class);
    }

    public final static int PICK_IMAGE_REQUEST = 1;
    public final static int READ_EXTERNAL_REQUEST = 2;

    private CompositeSubscription mCompositeSubscription;
    private ImageView ivAvatar;
    private Club selectedClub;
    private TextView tvName;
    private TextView tvLocation;
    private TextView tvDescription;
    private TextView tvAvgLevel;
    private ImageView ivAllowMatch;
    private ImageView ivRecruiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_main);
        mCompositeSubscription = new CompositeSubscription();

        selectedClub = (Club) getIntent().getSerializableExtra("selected_club");
        tvName = findViewById(R.id.tv_club_name);
        tvLocation = findViewById(R.id.tv_location);
        tvDescription = findViewById(R.id.tv_description);
        tvAvgLevel = findViewById(R.id.tv_avg_level);
        ivAllowMatch = findViewById(R.id.iv_allow_match);
        ivRecruiting = findViewById(R.id.iv_recruiting);
        ivAvatar = findViewById(R.id.iv_club_avatar);

        setDataToView(selectedClub);
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    public void setDataToView(Club selectedClub) {
        tvName.setText(selectedClub.getName());
        tvLocation.setText(selectedClub.getLocation());
        tvDescription.setText(selectedClub.getDescription());

        String level = "";
        if(selectedClub.getAverageLevel() == null || selectedClub.getAverageLevel() == 1) {
            level = "Beginner";
        } else if (selectedClub.getAverageLevel() == 2) {
            level = "Amateur";
        } else if (selectedClub.getAverageLevel() == 3) {
            level = "Professional";
        }

        tvAvgLevel.setText(level);

        if (selectedClub.getAllowFriendlyMatch() == true) {
            ivAllowMatch.setImageDrawable(getDrawable(R.drawable.ic_active));
        } else {
            ivAllowMatch.setImageDrawable(getDrawable(R.drawable.ic_deny));
        }

        if (selectedClub.getRecruiting() == true) {
            ivRecruiting.setImageDrawable(getDrawable(R.drawable.ic_active));
        } else {
            ivRecruiting.setImageDrawable(getDrawable(R.drawable.ic_deny));
        }

        if (selectedClub.getAvatar() == null || selectedClub.getAvatar().getUrl() == null || selectedClub.getAvatar().getUrl().equals("")) {
            ivAvatar.setImageDrawable(getDrawable(R.drawable.loginpic));
        } else {
            Glide.with(ClubMainActivity.this).load(Constant.BASE_URL + selectedClub.getAvatar().getUrl()).into(ivAvatar);
        }
    }

    public void editClub(View view) {
        Intent intent = ClubSetting.getInstance(ClubMainActivity.this);
        intent.putExtra("current_club", selectedClub);
        startActivityForResult(intent, 0);
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
            Toast.makeText(ClubMainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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

        if (requestCode == 0 && data != null) {
            setDataToView((Club)data.getSerializableExtra("updatedClub"));
        }
    }

    public void uploadFiles(Uri uri) {
        if (uri == null) return;
        User currentUser = new UserLocalDataSource(ClubMainActivity.this).getCurrentUser();

        File file = new File(RealPathUtil.getRealPathFromURI_API19(ClubMainActivity.this, uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                file);

        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("club[avatar]", file.getName(), requestBody);

        Subscription subscription = AppServiceClient.getInstance().uploadClubAvatar(selectedClub.getId(), filePart,
                currentUser.getAuthToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<Club>>() {
                    @Override
                    public void call(BaseResponse<Club> club) {
                        Toast.makeText(ClubMainActivity.this, "Upload succesfully!", Toast.LENGTH_SHORT).show();
                        Glide.with(ClubMainActivity.this).load(Constant.BASE_URL + club.getData().getAvatar().getUrl())
                                .into(ivAvatar);
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        Toast.makeText(ClubMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }
}

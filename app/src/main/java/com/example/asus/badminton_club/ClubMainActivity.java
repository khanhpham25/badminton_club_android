package com.example.asus.badminton_club;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.asus.badminton_club.utils.Constant;
import com.example.asus.badminton_club.utils.RealPathUtil;

import java.io.File;
import java.util.List;

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
    private ProgressDialog mProgressDialog;
    private User currentUser;
    private ImageView ivAvatar;
    private Club selectedClub;
    private TextView tvName;
    private TextView tvLocation;
    private TextView tvDescription;
    private TextView tvAvgLevel;
    private ImageView ivAllowMatch;
    private ImageView ivRecruiting;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4, fab5;
    private LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4, fabLayout5;
    private View fabBGLayout;
    private boolean isFABOpen=false;
    private Button btnJoinClub;
    private Button btnOutClub;
    private Button btnCancelJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_main);
        mCompositeSubscription = new CompositeSubscription();

        mProgressDialog = new ProgressDialog(ClubMainActivity.this);

        currentUser = new UserLocalDataSource(ClubMainActivity.this).getCurrentUser();

        fabLayout1= findViewById(R.id.fabLayoutClubShow1);
        fabLayout2= findViewById(R.id.fabLayoutClubShow2);
        fabLayout3= findViewById(R.id.fabLayoutClubShow3);
        fabLayout4= findViewById(R.id.fabLayoutClubShow4);
        fabLayout5= findViewById(R.id.fabLayoutClubShow5);
        fab = findViewById(R.id.fabClubShow);
        fab1 = findViewById(R.id.fabClubShow1);
        fab2 = findViewById(R.id.fabClubShow2);
        fab3 = findViewById(R.id.fabClubShow3);
        fab4 = findViewById(R.id.fabClubShow4);
        fab5 = findViewById(R.id.fabClubShow5);
        fabBGLayout = findViewById(R.id.fabBGLayoutClubShow);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });


        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ClubRequestActivity.getInstance(ClubMainActivity.this);
                intent.putExtra("current_club", selectedClub);
                startActivityForResult(intent, 0);
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editClub();
                closeFABMenu();
            }
        });

        selectedClub = (Club) getIntent().getSerializableExtra("selected_club");
        tvName = findViewById(R.id.tv_club_name);
        tvLocation = findViewById(R.id.tv_location);
        tvDescription = findViewById(R.id.tv_description);
        tvAvgLevel = findViewById(R.id.tv_avg_level);
        ivAllowMatch = findViewById(R.id.iv_allow_match);
        ivRecruiting = findViewById(R.id.iv_recruiting);
        ivAvatar = findViewById(R.id.iv_club_avatar);

        btnJoinClub = findViewById(R.id.btnJoinClub);
        btnOutClub = findViewById(R.id.btnOutClub);
        btnCancelJoin = findViewById(R.id.btnCancelJoin);

        btnJoinClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinClub();
            }
        });

        btnCancelJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelJoin();
            }
        });

        if(currentUser.getOwnedClubIds().contains(selectedClub.getId()) ||
                currentUser.getMemberClubIds().contains(selectedClub.getId())) {
            btnJoinClub.setVisibility(View.GONE);
            btnCancelJoin.setVisibility(View.GONE);
            btnOutClub.setVisibility(View.VISIBLE);
        } else if (currentUser.getRequestedClubIds().contains(selectedClub.getId())) {
            btnJoinClub.setVisibility(View.GONE);
            btnOutClub.setVisibility(View.GONE);
            btnCancelJoin.setVisibility(View.VISIBLE);
        } else {
            btnJoinClub.setVisibility(View.VISIBLE);
            btnOutClub.setVisibility(View.GONE);
            btnCancelJoin.setVisibility(View.GONE);
        }

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

    public void editClub() {
        Intent intent = ClubSetting.getInstance(ClubMainActivity.this);
        intent.putExtra("current_club", selectedClub);
        startActivityForResult(intent, 0);
    }

    public void joinClub() {
        mProgressDialog.setTitle("Join Club");
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        Subscription subscription = AppServiceClient.getInstance().joinClub(currentUser.getId(), selectedClub.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> club) {
                        Toast.makeText(ClubMainActivity.this, club.getMessage(), Toast.LENGTH_LONG).show();
                        List<Integer> listRequest = currentUser.getRequestedClubIds();
                        listRequest.add(selectedClub.getId());
                        currentUser.setRequestedClubIds(listRequest);
                        btnJoinClub.setVisibility(View.GONE);
                        btnOutClub.setVisibility(View.GONE);
                        btnCancelJoin.setVisibility(View.VISIBLE);
                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    public void cancelJoin() {
        mProgressDialog.setTitle("Cancel Request");
        mProgressDialog.setMessage("Cancelling...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        Subscription subscription = AppServiceClient.getInstance().handleDeleteJoinRequest(currentUser.getId(),
                selectedClub.getId(), currentUser.getAuthToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> club) {
                        Toast.makeText(ClubMainActivity.this, "Request Cancelled", Toast.LENGTH_LONG).show();
                        List<Integer> listRequest = currentUser.getRequestedClubIds();
                        listRequest.remove(Integer.valueOf(selectedClub.getId()));
                        currentUser.setRequestedClubIds(listRequest);
                        btnJoinClub.setVisibility(View.VISIBLE);
                        btnOutClub.setVisibility(View.GONE);
                        btnCancelJoin.setVisibility(View.GONE);
                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    public void outClub() {
        mProgressDialog.setTitle("Out Club");
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
        Subscription subscription = AppServiceClient.getInstance().outClub(currentUser.getId(),
                selectedClub.getId(), currentUser.getAuthToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> club) {
                        Toast.makeText(ClubMainActivity.this, "See you next time", Toast.LENGTH_SHORT).show();
                        List<Integer> listOwnedClub = currentUser.getOwnedClubIds();
                        List<Integer> listMemberClub = currentUser.getMemberClubIds();
                        listOwnedClub.remove(Integer.valueOf(selectedClub.getId()));
                        listMemberClub.remove(Integer.valueOf(selectedClub.getId()));
                        currentUser.setRequestedClubIds(listOwnedClub);
                        currentUser.setRequestedClubIds(listMemberClub);
                        btnJoinClub.setVisibility(View.VISIBLE);
                        btnOutClub.setVisibility(View.GONE);
                        btnCancelJoin.setVisibility(View.GONE);
                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
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
        mProgressDialog.setTitle("Upload Avatar");
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setIndeterminate(false);
        File file = new File(RealPathUtil.getRealPathFromURI_API19(ClubMainActivity.this, uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                file);

        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("club[avatar]", file.getName(), requestBody);
        mProgressDialog.show();
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
                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    private void showFABMenu(){
        isFABOpen=true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabLayout4.setVisibility(View.VISIBLE);
        fabLayout5.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabLayout4.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
        fabLayout5.animate().translationY(-getResources().getDimension(R.dimen.standard_235));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0);
        fabLayout4.animate().translationY(0);
        fabLayout5.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    fabLayout4.setVisibility(View.GONE);
                    fabLayout5.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}

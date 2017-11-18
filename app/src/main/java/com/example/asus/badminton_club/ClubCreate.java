package com.example.asus.badminton_club;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by khanh on 15/11/2017.
 */

public class ClubCreate extends AppCompatActivity {
    private CompositeSubscription mCompositeSubscription;
    private ProgressDialog mProgressDialog;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_create);

        currentUser = new UserLocalDataSource(ClubCreate.this).getCurrentUser();
        mCompositeSubscription = new CompositeSubscription();
        mProgressDialog = new ProgressDialog(ClubCreate.this);
        mProgressDialog.setTitle("Create Club");
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setIndeterminate(false);
    }

    public void createClub(View view) {
        EditText edtClubName = findViewById(R.id.editTextClubName);
        EditText edtClubLocation = findViewById(R.id.editTextLocation);
        Switch switchIsRecruiting = findViewById(R.id.switchIsRecruiting);
        Switch switchAllowFriendlyMatch = findViewById(R.id.switchAllowMatch);

        String clubName = edtClubName.getText().toString();
        String clubLocation = edtClubLocation.getText().toString();
        Boolean isRecruiting= switchIsRecruiting.isChecked();
        Boolean allowFriendlyMatch= switchAllowFriendlyMatch.isChecked();

        if (clubName.equals("")) {
            Toast.makeText(ClubCreate.this, "Club name must be filled!", Toast.LENGTH_SHORT).show();
        } else if (clubLocation.equals("")) {
            Toast.makeText(ClubCreate.this, "Club location must be filled!", Toast.LENGTH_SHORT).show();
        } else {
            mProgressDialog.show();
            Subscription subscription = AppServiceClient.getInstance().createClub(clubName, clubLocation,
                    isRecruiting, allowFriendlyMatch, currentUser.getId(), true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<BaseResponse<Club>>() {
                        @Override
                        public void call(BaseResponse<Club> club) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubCreate.this, "Club created succesfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("newClub", club.getData());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }, new SafetyError() {
                        @Override
                        public void onSafetyError(BaseException error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubCreate.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            mCompositeSubscription.add(subscription);
        }
    }


}

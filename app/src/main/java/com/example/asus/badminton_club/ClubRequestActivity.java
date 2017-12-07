package com.example.asus.badminton_club;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.badminton_club.data.ClubRequestCustomAdapter;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.model.JoinRequest;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ClubRequestActivity extends AppCompatActivity {

    public static Intent getInstance(Context context) {
        return new Intent(context, ClubRequestActivity.class);
    }

    private CompositeSubscription mCompositeSubscription;
    private ProgressDialog mProgressDialog;
    private ArrayList<JoinRequest> listRequests;
    private ClubRequestCustomAdapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Club currentClub;
    private TextView tvNotifyNoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_request);
        currentClub = (Club) getIntent().getSerializableExtra("current_club");
        tvNotifyNoRequest = findViewById(R.id.txtViewInformNoRequest);

        mProgressDialog = new ProgressDialog(ClubRequestActivity.this);
        mProgressDialog.setTitle("Data");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);

        listRequests = new ArrayList<>();
        mRecyclerView = findViewById(R.id.cycler_club_requests);

        mLayoutManager = new LinearLayoutManager(ClubRequestActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mCompositeSubscription = new CompositeSubscription();
        loadClubRequests();

    }

    public void loadClubRequests() {
        mProgressDialog.show();

        Subscription subscription = AppServiceClient
                .getInstance()
                .getClubRequests(currentClub.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<ArrayList<JoinRequest>>>() {
                    @Override
                    public void call(BaseResponse<ArrayList<JoinRequest>> clubRequests) {
                        listRequests = new ArrayList<>(clubRequests.getData());
                        adapter = new ClubRequestCustomAdapter(listRequests, ClubRequestActivity.this, new ClubRequestCustomAdapter.ClubRequestCustomAdapterListener() {
                            @Override
                            public void btnAcceptOnClick(View v, int position) {
                                acceptRequest(position);
                            }

                            @Override
                            public void btnDenyOnClick(View v, int position) {
                                denyRequest(position);
                            }

                            @Override
                            public void imageViewAvatarOnClick(View v, int position) {
                                Toast.makeText(ClubRequestActivity.this, "Image" + position, Toast.LENGTH_SHORT).show();
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                        checkEmptyRequest();
                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    public void acceptRequest(final int position) {
        mProgressDialog.show();

        JoinRequest selectedRequest = listRequests.get(position);

        Subscription subscription = AppServiceClient.getInstance().acceptClubRequest(selectedRequest.getUserId(),
                selectedRequest.getClubId(), false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> response) {
                        listRequests.remove(position);
                        adapter.notifyDataSetChanged();
                        checkEmptyRequest();
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubRequestActivity.this, "Request Accepted", Toast.LENGTH_SHORT).show();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    public void denyRequest(final Integer position) {
        mProgressDialog.show();
        User currentUser = new UserLocalDataSource(ClubRequestActivity.this).getCurrentUser();
        JoinRequest selectedRequest = listRequests.get(position);

        Subscription subscription = AppServiceClient.getInstance().handleDeleteJoinRequest(selectedRequest.getUserId(),
                selectedRequest.getClubId(), currentUser.getAuthToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> response) {
                        listRequests.remove(position);
                        adapter.notifyDataSetChanged();
                        checkEmptyRequest();
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubRequestActivity.this, "Request Denied", Toast.LENGTH_SHORT).show();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubRequestActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    public void checkEmptyRequest() {
        if (listRequests.size() > 0) {
            tvNotifyNoRequest.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            tvNotifyNoRequest.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }
}

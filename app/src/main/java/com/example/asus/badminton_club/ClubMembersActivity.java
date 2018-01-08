package com.example.asus.badminton_club;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.asus.badminton_club.data.ClubMemberCustomAdapter;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.screen.setting.SettingActivity;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ClubMembersActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, ClubMembersActivity.class);
    }

    private CompositeSubscription mCompositeSubscription;
    private ProgressDialog mProgressDialog;
    private Club currentClub;
    private User currentUser;
    private ArrayList<User> listMembers;
    private ClubMemberCustomAdapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_members);

        currentUser = new UserLocalDataSource(ClubMembersActivity.this).getCurrentUser();
        currentClub = (Club) getIntent().getSerializableExtra("current_club");

        mProgressDialog = new ProgressDialog(ClubMembersActivity.this);
        mProgressDialog.setTitle("Data");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);

        listMembers = new ArrayList<>();
        mRecyclerView = findViewById(R.id.cycler_view_members);

        mLayoutManager = new LinearLayoutManager(ClubMembersActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mCompositeSubscription = new CompositeSubscription();
        loadClubMembers();
    }

    public void loadClubMembers() {
        mProgressDialog.show();

        Subscription subscription = AppServiceClient
                .getInstance()
                .getClubMembers(currentClub.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<ArrayList<User>>>() {
                    @Override
                    public void call(BaseResponse<ArrayList<User>> users) {
                        listMembers = new ArrayList<>(users.getData());
                        adapter = new ClubMemberCustomAdapter(listMembers, ClubMembersActivity.this, new ClubMemberCustomAdapter.ClubMemberCustomAdapterListener() {

                            @Override
                            public void btnDeleteOnClick(View v, int position) {
                                deleteMember(position);
                            }

                            @Override
                            public void imageViewAvatarOnClick(View v, int position) {
                                showUserInfo(position);
                            }
                        }, currentUser, currentClub.getId());
                        mRecyclerView.setAdapter(adapter);
                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubMembersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    public void showUserInfo(int position) {
        Intent intent = new Intent(ClubMembersActivity.this, ShowUserActivity.class);
        intent.putExtra("selected_user", listMembers.get(position));
        startActivity(intent);
    }

    public void deleteMember(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete")
                .setMessage("Do you really want to delete this member?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        mProgressDialog.show();
                        Subscription subscription = AppServiceClient
                                .getInstance()
                                .outClub(0, listMembers.get(position).getId(), currentClub.getId(), currentUser.getAuthToken())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<BaseResponse<User>>() {
                                    @Override
                                    public void call(BaseResponse<User> user) {
                                        listMembers.remove(position);
                                        mRecyclerView.setAdapter(adapter);
                                        mProgressDialog.dismiss();
                                        Toast.makeText(ClubMembersActivity.this, "Delete Member Success!", Toast.LENGTH_SHORT).show();
                                    }
                                }, new SafetyError() {
                                    @Override
                                    public void onSafetyError(BaseException error) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(ClubMembersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        mCompositeSubscription.add(subscription);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}

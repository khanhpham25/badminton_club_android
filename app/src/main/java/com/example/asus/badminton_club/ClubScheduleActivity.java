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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.asus.badminton_club.data.ClubScheduleCustomAdapter;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.model.WorkingSchedule;
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

public class ClubScheduleActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, ClubScheduleActivity.class);
    }

    private CompositeSubscription mCompositeSubscription;
    private ProgressDialog mProgressDialog;
    private Club currentClub;
    private User currentUser;
    private ArrayList<WorkingSchedule> listSchedule;
    private ClubScheduleCustomAdapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_schedule);

        currentUser = new UserLocalDataSource(ClubScheduleActivity.this).getCurrentUser();
        currentClub = (Club) getIntent().getSerializableExtra("current_club");

        btnCreate = findViewById(R.id.btnCreateDate);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDate();
            }
        });

        if(currentUser.getOwnedClubIds().contains(currentClub.getId())) {
            btnCreate.setVisibility(View.VISIBLE);
        } else {
            btnCreate.setVisibility(View.GONE);
        }

        mProgressDialog = new ProgressDialog(ClubScheduleActivity.this);
        mProgressDialog.setTitle("Data");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);

        listSchedule = new ArrayList<>();
        mRecyclerView = findViewById(R.id.cycler_view_club_schedules);

        mLayoutManager = new LinearLayoutManager(ClubScheduleActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mCompositeSubscription = new CompositeSubscription();
        loadClubSchedules();
    }

    public void loadClubSchedules() {
        mProgressDialog.show();

        Subscription subscription = AppServiceClient
                .getInstance()
                .getClubSchedules(currentClub.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<ArrayList<WorkingSchedule>>>() {
                    @Override
                    public void call(BaseResponse<ArrayList<WorkingSchedule>> data) {
                        listSchedule = new ArrayList<>(data.getData());
                        adapter = new ClubScheduleCustomAdapter(listSchedule, ClubScheduleActivity.this, new ClubScheduleCustomAdapter.ClubScheduleCustomAdapterListener() {

                            @Override
                            public void btnDeleteSchedule(View v, int position) {
                                deleteDate(position);
                            }

                            @Override
                            public void btnConfirmDate(View v, int position, ViewSwitcher switcher) {
                                confirmDate(position, switcher);
                            }

                            @Override
                            public void btnEditDate(View v, int position, ViewSwitcher switcher) {
                                editDate(position, switcher);
                            }
                        }, currentUser, currentClub.getId());

                        mRecyclerView.setAdapter(adapter);
                        mProgressDialog.dismiss();
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ClubScheduleActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    public void newDate() {
        listSchedule.add(new WorkingSchedule());
        adapter.notifyDataSetChanged();
    };

    public void editDate(int position, ViewSwitcher switcher) {
        switcher.showNext();
        TextView tvDate = switcher.findViewById(R.id.tvDateTime);
        EditText edtDate = switcher.findViewById(R.id.edtDateTime);
        edtDate.setText(tvDate.getText());
    }

    public void confirmDate(final int position, ViewSwitcher switcher) {
        switcher.showNext();
        final TextView tvDate = switcher.findViewById(R.id.tvDateTime);
        final EditText edtDate = switcher.findViewById(R.id.edtDateTime);

        mProgressDialog.show();

        if (listSchedule.get(position).getId() == null) {
            Subscription subscription = AppServiceClient
                    .getInstance()
                    .createClubSchedule(currentClub.getId(), edtDate.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<BaseResponse<WorkingSchedule>>() {
                        @Override
                        public void call(BaseResponse<WorkingSchedule> data) {
                            listSchedule.set(position, data.getData());
                            adapter.notifyDataSetChanged();
                            tvDate.setText(data.getData().getWorkingDate());
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubScheduleActivity.this, "Create Schedule Successully", Toast.LENGTH_SHORT).show();
                        }
                    }, new SafetyError() {
                        @Override
                        public void onSafetyError(BaseException error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubScheduleActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            mCompositeSubscription.add(subscription);
        } else {
            Subscription subscription = AppServiceClient
                    .getInstance()
                    .updateClubSchedule(listSchedule.get(position).getId(), edtDate.getText().toString(), currentUser.getAuthToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<BaseResponse<WorkingSchedule>>() {
                        @Override
                        public void call(BaseResponse<WorkingSchedule> data) {
                            listSchedule.set(position, data.getData());
                            adapter.notifyDataSetChanged();
                            tvDate.setText(data.getData().getWorkingDate());
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubScheduleActivity.this, "Update Schedule Successully", Toast.LENGTH_SHORT).show();
                        }
                    }, new SafetyError() {
                        @Override
                        public void onSafetyError(BaseException error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubScheduleActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            mCompositeSubscription.add(subscription);
        }
    }

    public void deleteDate(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Do you really want to delete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        mProgressDialog.show();

                        Subscription subscription = AppServiceClient
                                .getInstance()
                                .deleteClubSchedule(listSchedule.get(position).getId(), currentUser.getAuthToken())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<BaseResponse<WorkingSchedule>>() {
                                    @Override
                                    public void call(BaseResponse<WorkingSchedule> data) {
                                        listSchedule.remove(position);
                                        adapter.notifyDataSetChanged();
                                        mProgressDialog.dismiss();
                                        Toast.makeText(ClubScheduleActivity.this, "Delete Schedule Successully", Toast.LENGTH_SHORT).show();
                                    }
                                }, new SafetyError() {
                                    @Override
                                    public void onSafetyError(BaseException error) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(ClubScheduleActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        mCompositeSubscription.add(subscription);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}

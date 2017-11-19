package com.example.asus.badminton_club;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.asus.badminton_club.data.ClubCustomAdapter;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by asus on 11/5/2017.
 */

public class Tab2Fragment extends Fragment {
    private CompositeSubscription mCompositeSubscription;
    private ProgressDialog mProgressDialog;
    private ArrayList<Club> listClubs;
    private static ClubCustomAdapter adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton fab, fab1, fab2, fab3;
    private LinearLayout fabLayout1, fabLayout2, fabLayout3;
    private View fabBGLayout;
    private boolean isFABOpen=false;
    private boolean isLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2, container, false);

        fabLayout1= view.findViewById(R.id.fabLayout1);
        fabLayout2= view.findViewById(R.id.fabLayout2);
        fabLayout3= view.findViewById(R.id.fabLayout3);
        fab = view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab2= view.findViewById(R.id.fab2);
        fab3 = view.findViewById(R.id.fab3);
        fabBGLayout = view.findViewById(R.id.fabBGLayout);
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
                Intent gotoCreateclub = new Intent(getActivity(), ClubCreate.class);
                startActivityForResult(gotoCreateclub, 0);
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Data");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);

        mRecyclerView = view.findViewById(R.id.cycler_view_clubs);

        mLayoutManager = new ClubLinearLayoutManager(getActivity(), 1, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(
                new ClubRecyclerTouchListener(getActivity(), new ClubRecyclerTouchListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        isLoaded = false;
                        Intent intent = new Intent(getActivity(), ClubMainActivity.class);
                        intent.putExtra("selected_club", listClubs.get(position));
                        startActivity(intent);
                    }
                })
        );

        listClubs = new ArrayList<>();

        mCompositeSubscription = new CompositeSubscription();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && data != null) {
            listClubs.add((Club)data.getSerializableExtra("newClub"));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isLoaded ) {
            mProgressDialog.show();
            Subscription subscription = AppServiceClient.getInstance().getAllClubs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<BaseResponse<ArrayList<Club>>>() {
                        @Override
                        public void call(BaseResponse<ArrayList<Club>> clubs) {
                            listClubs = new ArrayList<>(clubs.getData());
                            adapter = new ClubCustomAdapter(listClubs, getActivity());
                            mRecyclerView.setAdapter(adapter);
                            mProgressDialog.dismiss();
                        }
                    }, new SafetyError() {
                        @Override
                        public void onSafetyError(BaseException error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            mCompositeSubscription.add(subscription);

            isLoaded = true;
        }
    }

    private void showFABMenu(){
        isFABOpen=true;
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
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

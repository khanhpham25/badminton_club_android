package com.example.asus.badminton_club;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.asus.badminton_club.screen.setting.SettingActivity;

/**
 * Created by asus on 11/5/2017.
 */

public class Tab3Fragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab3, container, false);
        final View button = view.findViewById(R.id.account);
        View btnAlert = view.findViewById(R.id.btnAlert);
        View btnTheme = view.findViewById(R.id.btnTheme);
        View btnAbout = view.findViewById(R.id.btnAbout);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gotoAccount = new Intent(getActivity(), SettingActivity.class);
                        startActivity(gotoAccount);
                    }
                }
        );

        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "This function will be coming soon in our next update!", Toast.LENGTH_LONG).show();
            }
        });

        btnTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "This function will be coming soon in our next update!", Toast.LENGTH_LONG).show();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(AboutActivity.getInstance(getActivity()));
            }
        });
        return view;
    }


}

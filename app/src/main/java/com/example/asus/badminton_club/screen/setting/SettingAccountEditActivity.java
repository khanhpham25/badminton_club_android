package com.example.asus.badminton_club.screen.setting;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.screen.authentication.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SettingAccountEditActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, SettingAccountEditActivity.class);
    }

    private User currentUser;
    private EditText editName;
    private EditText editMobile;
    private EditText editMainRacket;
    private Spinner spinnerSex;
    private Spinner spinnerSkill;
    private CompositeSubscription mCompositeSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account_edit);

        mCompositeSubscription = new CompositeSubscription();

        // Get reference of widgets from XML layout
        spinnerSex = findViewById(R.id.spinnerSex);
        spinnerSkill = findViewById(R.id.spinnerSkillLevel);

        // Initializing a String Array
        String[] skillLevels = new String[]{
                "Choose your skill level",
                "Beginner",
                "Amateur",
                "Professional"
        };

        String[] sex = new String[]{
                "Select your gender",
                "Male",
                "Female",
                "Other"
        };

        final List<String> skillLevelList = new ArrayList<>(Arrays.asList(skillLevels));
        final List<String> sexList = new ArrayList<>(Arrays.asList(sex));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerSkillLevelArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,skillLevelList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        final ArrayAdapter<String> spinnerSexArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,sexList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerSkillLevelArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerSkill.setAdapter(spinnerSkillLevelArrayAdapter);

        spinnerSexArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerSex.setAdapter(spinnerSexArrayAdapter);

        editName = findViewById(R.id.editName);
        editMobile = findViewById(R.id.editMobile);
        editMainRacket = findViewById(R.id.editMainRacket);

        currentUser = new UserLocalDataSource(SettingAccountEditActivity.this).getCurrentUser();

        editName.setText(currentUser.getName());
        editMobile.setText(currentUser.getMobile());
        editMainRacket.setText(currentUser.getMainRackquet());

        if(currentUser.getGender() == null || currentUser.getGender() == 0) {
            spinnerSex.setSelection(-1);
        } else {
            spinnerSex.setSelection(currentUser.getGender());
        }

        if(currentUser.getBadmintonLevel() == null || currentUser.getBadmintonLevel() == 0) {
            spinnerSkill.setSelection(-1);
        } else {
            spinnerSkill.setSelection(currentUser.getBadmintonLevel());
        }
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    public void save(View View) {
        String updateName = editName.getText().toString();
        String updateMobile = editMobile.getText().toString();
        String updateRacket = editMainRacket.getText().toString();
        Integer updateGender = spinnerSex.getSelectedItemPosition();
        Integer updateSkill = spinnerSkill.getSelectedItemPosition();

        if (!updateName.trim().equals("")) {
            Subscription subscription = AppServiceClient.getInstance().updateUserInfo(currentUser.getId(),
                    updateName, updateMobile, updateRacket, updateGender, updateSkill,
                    currentUser.getAuthToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<BaseResponse<User>>() {
                        @Override
                        public void call(BaseResponse<User> user) {
                            Toast.makeText(SettingAccountEditActivity.this, "Update succesfully!", Toast.LENGTH_SHORT).show();
                            new UserLocalDataSource(SettingAccountEditActivity.this).saveUser(user.getData());
                            startActivity(SettingActivity.getInstance(SettingAccountEditActivity.this));
                        }
                    }, new SafetyError() {
                        @Override
                        public void onSafetyError(BaseException error) {
                            Toast.makeText(SettingAccountEditActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            mCompositeSubscription.add(subscription);
        } else {
            Toast.makeText(SettingAccountEditActivity.this, "Name can't be blank", Toast.LENGTH_SHORT).show();
        }
    }
}

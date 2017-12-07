package com.example.asus.badminton_club;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.Club;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.local.UserLocalDataSource;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.screen.setting.SettingAccountEditActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by khanh on 15/11/2017.
 */

public class ClubSetting extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, ClubSetting.class);
    }

    private ProgressDialog mProgressDialog;
    private CompositeSubscription mCompositeSubscription;
    private Club currentClub;
    private EditText edtName;
    private EditText edtLocation;
    private EditText edtDescription;
    private Spinner spinnerClubLevel;
    private Switch swAllowMatch;
    private Switch swRecruiting;
    private Place selectedPlace;

    public static final int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_setting);
        mCompositeSubscription = new CompositeSubscription();

        currentClub = (Club) getIntent().getSerializableExtra("current_club");

        spinnerClubLevel = findViewById(R.id.spinnerClubLevel);

        String[] plants = new String[]{
                "Choose club average skill level",
                "Beginner",
                "Amateur",
                "Professional"
        };

        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item, plantsList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else {
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
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerClubLevel.setAdapter(spinnerArrayAdapter);

        edtName = findViewById(R.id.edtClubName);
        edtLocation = findViewById(R.id.edtClubLocation);
        edtDescription = findViewById(R.id.edtClubDescription);
        swAllowMatch = findViewById(R.id.swAllowMatch);
        swRecruiting = findViewById(R.id.swRecruiting);

        edtName.setText(currentClub.getName());
        edtLocation.setText(currentClub.getLocation());
        edtDescription.setText(currentClub.getDescription());
        swAllowMatch.setChecked(currentClub.getAllowFriendlyMatch());
        swRecruiting.setChecked(currentClub.getRecruiting());

        if(currentClub.getAverageLevel() == null || currentClub.getAverageLevel() == 0) {
            spinnerClubLevel.setSelection(-1);
        } else {
            spinnerClubLevel.setSelection(currentClub.getAverageLevel());
        }

        mProgressDialog = new ProgressDialog(ClubSetting.this);
        mProgressDialog.setTitle("Update Info");
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setIndeterminate(false);
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedPlace = PlacePicker.getPlace(data, this);
                edtLocation.setText(selectedPlace.getAddress().toString());
            }
        }
    }

    public void saveClubChanges(View view) {
        User currentUser = new UserLocalDataSource(ClubSetting.this).getCurrentUser();

        String updateName = edtName.getText().toString();
        String updateLocation = edtLocation.getText().toString();
        String updateDescription = edtDescription.getText().toString();
        Integer updateLevel = spinnerClubLevel.getSelectedItemPosition();
        Boolean updateAllowMatch = swAllowMatch.isChecked();
        Boolean updateRecruiting = swRecruiting.isChecked();
        Double latitude = null, longitude = null;

        if (selectedPlace != null) {
            latitude = selectedPlace.getLatLng().latitude;
            longitude = selectedPlace.getLatLng().longitude;
        }

        if (!updateName.trim().equals("")) {
            mProgressDialog.show();
            Subscription subscription = AppServiceClient.getInstance().updateClubInfo(currentClub.getId(),
                    updateName, updateLocation, latitude, longitude, updateDescription, updateLevel,
                    updateRecruiting, updateAllowMatch, currentUser.getAuthToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<BaseResponse<Club>>() {
                        @Override
                        public void call(BaseResponse<Club> club) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubSetting.this, "Update succesfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("updatedClub", club.getData());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }, new SafetyError() {
                        @Override
                        public void onSafetyError(BaseException error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ClubSetting.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            mCompositeSubscription.add(subscription);
        } else {
            Toast.makeText(ClubSetting.this, "Name can't be blank",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void pickClubLocation(View view) throws GooglePlayServicesNotAvailableException,
            GooglePlayServicesRepairableException {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        startActivityForResult(builder.build(ClubSetting.this), PLACE_PICKER_REQUEST);
    }
}

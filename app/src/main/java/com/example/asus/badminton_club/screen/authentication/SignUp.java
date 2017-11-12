package com.example.asus.badminton_club.screen.authentication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;

import org.json.JSONException;

import java.util.regex.Pattern;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SignUp extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, SignUp.class);
    }

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    //
//    public void onSignUpClick(View v) throws JSONException {
//        JSONObject postData = new JSONObject();
//
//        EditText txtName = (EditText) findViewById(R.id.txtName);
//        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
//        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
//        EditText txtCPassword = (EditText) findViewById(R.id.txtCPassword);
//
//        String name = txtName.getText().toString();
//        String email = txtEmail.getText().toString();
//        String password = txtPassword.getText().toString();
//        String cPassword = txtCPassword.getText().toString();
//        Log.d("name", name.toString());
//        Log.d("email", email.toString());
//        Log.d("password", password.toString());
//        Log.d("cPassword", cPassword.toString());
//        if (!password.equals(cPassword)) {
//            Toast errorToast = Toast.makeText(SignUp.this, getString(R.string.error_password_dont_match), Toast.LENGTH_SHORT);
//            errorToast.show();
//        } else {
//            try {
//                postData.put("user[name]", name);
//                postData.put("user[email]", email);
//                postData.put("user[password]", password);
//                postData.put("user[confirm_password]", cPassword);
//
//                SendDeviceDetails service = new SendDeviceDetails();
//                service.execute("http://localhost:3000/api/users", postData.toString());
//            }  catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    public void onSignUpClick(View v) throws JSONException {
        EditText txtName = (EditText) findViewById(R.id.txtName);
        EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        EditText txtCPassword = (EditText) findViewById(R.id.txtCPassword);

        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String cPassword = txtCPassword.getText().toString();

        if(!isValidEmaillId(email.trim())){
          Toast.makeText(getApplicationContext(), "InValid Email Address.", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
           Toast.makeText(SignUp.this, getString(R.string.error_password_dont_match), Toast.LENGTH_SHORT).show();
        } else {
            Subscription subscription = AppServiceClient.getInstance().register(name, email, password, cPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseResponse<User>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(BaseResponse<User> userResponse) {
                            Toast.makeText(SignUp.this, userResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(LoginActivity.getInstance(SignUp.this));
                        }
                    });

            mCompositeSubscription.add(subscription);
        }
    }

    private boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}

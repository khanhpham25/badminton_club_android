package com.example.asus.badminton_club.screen.authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.badminton_club.ClubMainActivity;
import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.ResetPasswordActivity;
import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ForgotPassword extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, ForgotPassword.class);
    }

    private ProgressDialog mProgressDialog;
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mCompositeSubscription = new CompositeSubscription();
        mProgressDialog = new ProgressDialog(ForgotPassword.this);
        mProgressDialog.setTitle("Reset Password");
        mProgressDialog.setMessage("Submitting...");
        mProgressDialog.setIndeterminate(false);
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    public void submitEmail(View view) {
        mProgressDialog.show();
        EditText edtRecoverEmail = findViewById(R.id.edtRecoveryEmail);
        final String recoverEmail = edtRecoverEmail.getText().toString();

        Subscription subscription = AppServiceClient.getInstance().submitEmail(recoverEmail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse<User>>() {
                    @Override
                    public void call(BaseResponse<User> user) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ForgotPassword.this, user.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = ResetPasswordActivity.getInstance(ForgotPassword.this);
                        intent.putExtra("recover_email", recoverEmail);
                        startActivity(intent);
                    }
                }, new SafetyError() {
                    @Override
                    public void onSafetyError(BaseException error) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ForgotPassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }
}

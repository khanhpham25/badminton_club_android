package com.example.asus.badminton_club;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.badminton_club.data.model.BaseResponse;
import com.example.asus.badminton_club.data.model.User;
import com.example.asus.badminton_club.data.source.remote.api.error.BaseException;
import com.example.asus.badminton_club.data.source.remote.api.error.SafetyError;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;
import com.example.asus.badminton_club.screen.authentication.ForgotPassword;
import com.example.asus.badminton_club.screen.authentication.LoginActivity;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ResetPasswordActivity extends AppCompatActivity {
    public static Intent getInstance(Context context) {
        return new Intent(context, ResetPasswordActivity.class);
    }

    private ProgressDialog mProgressDialog;
    private CompositeSubscription mCompositeSubscription;
    private String recoverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        recoverEmail = getIntent().getStringExtra("recover_email");
        mCompositeSubscription = new CompositeSubscription();
        mProgressDialog = new ProgressDialog(ResetPasswordActivity.this);
        mProgressDialog.setTitle("Reset Password");
        mProgressDialog.setMessage("Processing...");
        mProgressDialog.setIndeterminate(false);
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    public void resetPassword(View view) {
        EditText edtResetToken = findViewById(R.id.edtResetToken);
        EditText edtResetPassword = findViewById(R.id.edtResetPassword);
        EditText edtConfirmResetPassword = findViewById(R.id.edtConfirmResetPassword);
        String resetToken = edtResetToken.getText().toString();
        String password = edtResetPassword.getText().toString();
        String cPassword = edtConfirmResetPassword.getText().toString();

        if (resetToken.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Reset Token must be filled" , Toast.LENGTH_SHORT).show();
        } else if (password.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Password can't be blank" , Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
            Toast.makeText(ResetPasswordActivity.this, "Password must be the same" , Toast.LENGTH_SHORT).show();
        } else {
            mProgressDialog.show();
            Subscription subscription = AppServiceClient.getInstance().resetPassword(recoverEmail, resetToken,
                    password, cPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<BaseResponse<User>>() {
                        @Override
                        public void call(BaseResponse<User> user) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ResetPasswordActivity.this, "Password has been reset", Toast.LENGTH_LONG).show();
                            startActivity(LoginActivity.getInstance(ResetPasswordActivity.this));
                            finish();
                        }
                    }, new SafetyError() {
                        @Override
                        public void onSafetyError(BaseException error) {
                            mProgressDialog.dismiss();
                            Toast.makeText(ResetPasswordActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            mCompositeSubscription.add(subscription);
        }
    }

}

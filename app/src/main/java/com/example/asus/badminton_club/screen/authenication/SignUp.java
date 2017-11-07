package com.example.asus.badminton_club.screen.authenication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.badminton_club.R;
import com.example.asus.badminton_club.data.model.UserResponse;
import com.example.asus.badminton_club.data.source.remote.api.service.AppServiceClient;

import org.json.JSONException;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SignUp extends AppCompatActivity {
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

        Subscription subscription = AppServiceClient.getInstance().register(name, email, password, cPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(UserResponse userResponse) {
                        Toast.makeText(SignUp.this, userResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    private class SendDeviceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }
}

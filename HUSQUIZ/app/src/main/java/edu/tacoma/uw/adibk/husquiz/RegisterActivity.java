/** This class creates the reigster activity */
package edu.tacoma.uw.adibk.husquiz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.prefs.Preferences;

public class RegisterActivity extends AppCompatActivity
{
    /** A EditText widget that holds the email */
    EditText mTextEmail;
    /** A EditText widget that holds the password */
    EditText mTextPassword;
    /** A EditText widget that confirms the password */
    EditText mTextCnfPassword;
    /** A button for registering */
    Button mButtonRegister;
    /** A EditText widget that holds the login */
    TextView mTextViewLogin;
    /** The Login for the backend */
    JSONObject mRegisterJSON;
    /** A string for email */
    private static final String EMAIL = "email";
    /** A string for the password */
    private static final String PASSWORD = "password";

    private SharedPreferences mSharedPreferences;

    /**
     * This method overwrites the onCreate to set the buttons & text widgets to their ID's
     * @param savedInstanceState default bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createLoadingScreen();
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        mTextEmail = (EditText) findViewById(R.id.edittext_email);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mTextCnfPassword = (EditText) findViewById(R.id.edittext_cnf_password);
        mTextCnfPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mButtonRegister = (Button) findViewById(R.id.button_register);
        mTextViewLogin = (TextView) findViewById(R.id.textview_login);
        mTextViewLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        });
        mButtonRegister.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method sets the intent to another class
             * @param view default view
             */
            @Override
            public void onClick(View view)
            {
                String email = mTextEmail.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();
                if (pwd.equals(cnf_pwd))
                {
                    try
                    {
                        mRegisterJSON = new JSONObject();
                        mRegisterJSON.put(RegisterActivity.EMAIL, mTextEmail.getText().toString());
                        mRegisterJSON.put(RegisterActivity.PASSWORD, mTextPassword.getText().toString());
                        new RegisterTask().execute(getString(R.string.add_login_info));
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createLoadingScreen() {
        final ProgressDialog nDialog;

        nDialog = new ProgressDialog(RegisterActivity.this);
        nDialog.setMax(100);
        nDialog.setMessage("Please wait...");
        nDialog.setTitle("My Application");
        nDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        nDialog.show();
        @SuppressLint("HandlerLeak") final Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                nDialog.incrementProgressBy(1);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (nDialog.getProgress() <= nDialog
                            .getMax()) {
                        Thread.sleep(30);
                        handle.sendMessage(handle.obtainMessage());
                        if (nDialog.getProgress() == nDialog
                                .getMax()) {
                            nDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /** This class handles the backend of storing user login information */
    private class RegisterTask extends AsyncTask<String, Void, String>
    {
        /**
         * This method downloads & checks the new usernames & passwords
         * @param urls
         * @return response string
         */
        @Override
        protected String doInBackground(String... urls)
        {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setDoOutput(true);
                    OutputStreamWriter wr =
                            new OutputStreamWriter(urlConnection.getOutputStream());
                    wr.write(mRegisterJSON.toString());
                    wr.flush();
                    wr.close();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null)
                    {
                        response += s;
                    }
                } catch (Exception e) {
                    response = "Unable to download the list of logins, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }
        /**
         * This method handles what happens when the new login info is incorrect or not
         * @param s default string
         */
        @Override
        protected void onPostExecute(String s)
        {
            if (s.startsWith("Unable to"))
            {
                Toast.makeText(getApplicationContext(), "Unable to download" + s, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try
            {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success"))
                {
                    Toast.makeText(getApplicationContext(), "Successfully created account and logged in",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, CourseListActivity.class);
                    mSharedPreferences
                            .edit()
                            .putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Email exists already",
                            Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e)
            {
                Toast.makeText(getApplicationContext(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

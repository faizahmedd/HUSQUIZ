/** This class creates the Login Activity */
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
import android.provider.ContactsContract;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

public class LoginActivity extends AppCompatActivity
{
    /** A EditText widget for the username */
    EditText mTextUsername;
    /** A EditText widget for the password */
    EditText mTextPassword;
    /** A button for the login */
    Button mButtonLogin;
    /** A Textview widget for the register */
    TextView mTextViewRegister;
    /** The Login for the backend */
    private JSONObject mLoginJSON;
    /** A string for the email */
    public static final String EMAIL = "email";
    /** A string for the password */
    public static final String PASSWORD = "password";

    public static final String MY_LOGIN_INFO = "my_login_info";

    public static final String SIGNED_OUT = "signed_out";

    private SharedPreferences mSharedPreferences;

    /**
     * This method overwrites the onCreate to set the buttons & text widgets to their ID's
     * @param savedInstanceState default bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        //createLoadingScreen();
        if (getIntent().getBooleanExtra(SIGNED_OUT, false))
        {
            mSharedPreferences
                    .edit()
                    .remove(getString(R.string.LOGGEDIN))
                    .commit();
        }

        if (mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false))
        {
            Intent intent = new Intent(this, CourseListActivity.class);
            startActivity(intent);
            finish();
        }
        mTextUsername = (EditText) findViewById(R.id.edittext_email);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mTextViewRegister = (TextView) findViewById(R.id.textview_register);
        mButtonLogin = (Button) findViewById(R.id.button_login);

        mLoginJSON = new JSONObject();
        mTextViewRegister.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method sets the intent to another class
             * @param view default view
             */
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        mButtonLogin.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method puts the login info & password in the server
             * @param view default login
             */
            @Override
            public void onClick(View view)
            {
                try
                {
                    mLoginJSON.put(LoginActivity.EMAIL, mTextUsername.getText().toString());
                    mLoginJSON.put(LoginActivity.PASSWORD, mTextPassword.getText().toString());
                    new LoginTask().execute(getString(R.string.get_login_info));
                    // Store user login info
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }
    private void createLoadingScreen() {
        final ProgressDialog nDialog;

        nDialog = new ProgressDialog(LoginActivity.this);
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

    /** A inner class that creates the backend for logging in */
    private class LoginTask extends AsyncTask<String, Void, String>
    {
        /**
         * This method downloads & checks the usernames & passwords
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
                    wr.write(mLoginJSON.toString());
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
         * This method handles what happens when the login info is incorrect or not
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
                    // Store user login info
                    mSharedPreferences
                            .edit()
                            .putBoolean(getString(R.string.LOGGEDIN), true)
                            .commit();
                    sendMessage(mTextUsername.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, CourseListActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Email or Password is Incorrect",
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

    private void sendMessage(final String email) {
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("HUSQUIZAPP@gmail.com", "Husquiz123");
                    sender.sendMail("EmailSender App",
                            "You have logged in to the Husquiz App",
                            "HUSQUIZAPP@gmail.com",
                            email);
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }
}


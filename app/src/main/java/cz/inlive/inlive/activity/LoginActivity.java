package cz.inlive.inlive.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;

import cz.inlive.inlive.InLiveApplication;
import cz.inlive.inlive.R;
import cz.inlive.inlive.network.JSONObjectResponse;
import cz.inlive.inlive.utils.Constants;
import cz.inlive.inlive.utils.Log;


public class LoginActivity extends Activity {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private SharedPreferences mPrefs;
    private Context mContext;

    private TextView mUsername;
    private TextView mPassword;
    private TextView mLogin;

    private boolean mCheckingServer;
    private String mToken = "";

    GoogleCloudMessaging mGcm;
    String regid;

    private String TAG = "LOGIN ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        // hide Actionbar
        final ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        mUsername = (TextView) findViewById(R.id.username);
        mPassword = (TextView) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login_btn);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCheckingServer = true;

                // Check device for Play Services APK. If check succeeds, proceed with
                //  GCM registration.
                mGcm = GoogleCloudMessaging.getInstance(mContext);
                regid = getRegistrationId(mContext);

                if (regid.isEmpty()) {
                    registerInBackground();
                }else{
                    mToken = regid;
                    login();
                }
            }
        });
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            android.util.Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            android.util.Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected void onPostExecute(Object o) {
                    Log.d(TAG, o.toString());
            }

            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    regid = mGcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(mContext, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    onServerCheckEnd();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, R.string.service_not_available, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                return msg;
            }
        }.execute(null, null, null);

    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regid) {
        // login on server
        mToken = regid;
        login();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        android.util.Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    private void login(){
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        ((InLiveApplication) getApplication()).getNetworkHandler().handleLogin(new JSONObjectResponse() {
            @Override
            public void onResponse(long id, JSONObject result) {

                if( result.has("result") && !result.isNull("result") ){
                    // 200 OK, run normal activity

                    // no longer request login action
                    mPrefs.edit().putBoolean(getResources().getString(R.string.first_run_pref_key), false).commit();
                    //run normal activity
                    Intent i = new Intent(mContext, LandingPageActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }else{
                    // other,
                    onServerCheckEnd();
                    Toast.makeText(mContext, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }

                Log.d("LoginActivity", result.toString());
            }

            @Override
            public void onError(long id, VolleyError volleyError) {

                onServerCheckEnd();
                Toast.makeText(mContext, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }
        }, mToken, username, password);
    }

    private void onServerCheckEnd(){
        mCheckingServer = false;

        //TODO stop spinner

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}

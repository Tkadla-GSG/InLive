package cz.inlive.inlive.modules;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import cz.inlive.inlive.InLiveApplication;
import cz.inlive.inlive.R;
import cz.inlive.inlive.database.DatabaseHandler;
import cz.inlive.inlive.network.JSONObjectResponse;
import cz.inlive.inlive.network.NetworkHandler;
import cz.inlive.inlive.utils.Log;

/**
 * Created by Tkadla on 8. 10. 2014.
 */
public class UpdateIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpdateIntentService(String name) {
        super(name);
    }

    public UpdateIntentService() {
        super("UpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        new UpdateTask().execute();

    }

    private class UpdateTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {

            Log.d("UpdateServie", "Service called");

            final SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            final String updatePrefKey = getApplicationContext().getResources().getString(R.string.last_update_pref_key);

            long update = mPrefs.getLong(updatePrefKey, System.currentTimeMillis());
            String username = mPrefs.getString(getApplicationContext().getResources().getString(R.string.username_pref_key), "");
            String password = mPrefs.getString(getApplicationContext().getResources().getString(R.string.password_pref_key), "");

            // DO update
            final DatabaseHandler db = ((InLiveApplication) getApplication()).getDatabaseHandler();
            final NetworkHandler ntw = ((InLiveApplication) getApplication()).getNetworkHandler();

            ntw.handleBetsUpdate(new JSONObjectResponse() {
                @Override
                public void onResponse(long id, JSONObject result) {
                    try {

                        Log.d("UpdateServie", "Save bets");
                        db.saveBets( db.betsFromJSON(result) );

                        // update last successful update
                        mPrefs.edit().putLong(updatePrefKey, System.currentTimeMillis()).apply();
                        Log.d("UpdateServie", "last update refreshed at " + System.currentTimeMillis());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(long id, VolleyError volleyError) {
                    Log.e("UpdateServie", "Network error");
                }
            }, update, username, password);

            return false;
        }
    }
}

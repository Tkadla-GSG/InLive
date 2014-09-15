package cz.inlive.inlive.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import cz.inlive.inlive.utils.Constants;
import cz.inlive.inlive.utils.Log;

/**
 * Created by Tkadla on 13. 9. 2014.
 */
public class NetworkHandler {

    private Context mContext;
    private RequestQueue mRequestQueue;
    private SharedPreferences mPreferences;

    private static final String TAG = "NetworkHandler";

    public NetworkHandler(Context context) {
        this.mRequestQueue = Volley.newRequestQueue(context);
        this.mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
    }


   public void handleLogin( final JSONObjectResponse resp, String token, String username, String password ){

       String url = Constants.URL_BASE;

       JSONObject request = new JSONObject();
       JSONObject payload = new JSONObject();

       try {

           payload.put("token", token);
           payload.put("source", "Android");

           request.put("method", "login");
           request.put("payload", payload);

       } catch (JSONException e) {
           e.printStackTrace();
       }

       JsonObjectRequest jsonObjectRequest = null;

       // create request with specified credentials
       jsonObjectRequest = new ApiRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               resp.onResponse(0, response);
               Log.d(TAG, "Response: " + response.toString());
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               resp.onError(0, error);
               Log.e(TAG, error.toString());
           }
       }
       ,username, password );

       Log.d(TAG, request.toString());

       // try to get answer for max 10000 milies with maximum retries
       jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
               DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

       // add request to queue
       this.mRequestQueue.add(jsonObjectRequest);
   }


}

package cz.inlive.inlive.network;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Vaclav Krejza on 18. 4. 2014.
 */
public interface JSONObjectResponse {

    public void onResponse(long id, JSONObject result);
    public void onError(long id, VolleyError volleyError);

}

package cz.inlive.inlive.network;

/**
 * Created by Tkadla on 13. 9. 2014.
 */

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.inlive.inlive.utils.Log;

/**
     * Creates network request
     *
     * On top of JsonObjectRequest adds authorization for every request
     * On top of JsonObjectRequest adds UTF 8 response conversion
     */
    public class ApiRequest extends JsonObjectRequest {

    public ApiRequest(int method, String url, JSONObject jsonRequest,
                      Response.Listener<JSONObject> listener,
                      Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public ApiRequest(String url, JSONObject jsonRequest,
                      Response.Listener<JSONObject> listener,
                      Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    /**
     * Convert response to UTF8 charset, two solution added, only one simple should suffice
     * @param response - network responce to be converted
     * @return converted responce
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            // simple fix
            String jsonString = new String(response.data, "UTF-8");
            // harder fix
            //response.headers.put(HTTP.CONTENT_TYPE,response.headers.get("content-type"));
            //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            //
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
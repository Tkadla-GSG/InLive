package cz.inlive.inlive.database.objects;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tkadla on 19. 9. 2014.
 */
public class Info {

    private long id;
    private String message = "";
    private String type = "";
    private long received;

    public Info() {
    }

    public void parseJSON(JSONObject object) throws JSONException{
        if(object.has("id") && !object.isNull("id") ){
            setId(object.getLong("id"));
        }

        if(object.has("message") && !object.isNull("message") ){
            setMessage(object.getString("message"));
        }

        if(object.has("type") && !object.isNull("type") ){
            setType(object.getString("type"));
        }

        if(object.has("received") && !object.isNull("received") ){
            setReceived(object.getLong("received"));
        }
    }

    public void parseCursor(Cursor cursor) {
        if(cursor.getColumnIndex("id") > -1) {
            setId(cursor.getLong(cursor.getColumnIndex("id")));
        }

        if(cursor.getColumnIndex("message") > -1) {
            setMessage(cursor.getString(cursor.getColumnIndex("message")));
        }

        if(cursor.getColumnIndex("type") > -1) {
            setType(cursor.getString(cursor.getColumnIndex("type")));
        }

        if(cursor.getColumnIndex("received") > -1) {
            setReceived(cursor.getLong(cursor.getColumnIndex("received")));
        }

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getReceived() {
        return received;
    }

    public void setReceived(long received) {
        this.received = received;
    }
}

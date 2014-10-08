package cz.inlive.inlive.database.objects;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tkadla on 19. 9. 2014.
 */
public class Bet {

    private long id = -1;
    private String inLiveId = "";
    private String message = "";
    private long start_timestamp;
    private String league = "";
    private String match = "";
    private String tip = "";
    private String score = " - : -";
    private String odd = "";
    private String type = "";
    private String status;
    private long received;

    public Bet() {
    }

    public void parseJSON(JSONObject object) throws JSONException{

        if( jsonParamExist(object, "id") ){
            setInLiveId( object.getString("id") );
        }

        if( jsonParamExist(object, "status") ){
            setStatus( object.getString("status") );
        }

        if( jsonParamExist(object, "score") ){
            setScore( object.getString("score") );
        }

        if( jsonParamExist(object, "match") ){
            setMatch( object.getString("match") );
        }

        if( jsonParamExist(object, "odd") ){
            setOdd( "" + object.getDouble("odd") );
        }

        if( jsonParamExist(object, "league") ){
            setLeague(object.getString("league") );
        }

        if( jsonParamExist(object, "type") ){
            setType(object.getString("type") );
        }

        if( jsonParamExist(object, "start_timestamp") ){
            setStart_timestamp(object.getLong("start_timestamp") );
        }

        if( jsonParamExist(object, "tip") ){
            setTip(object.getString("tip") );
        }

        // at this time has been bet received
        setReceived(System.currentTimeMillis());
    }

    public void parseCursor(Cursor cursor){
        if(cursor.getColumnIndex("id") > -1) {
            setId(cursor.getLong(cursor.getColumnIndex("id")));
        }

        if(cursor.getColumnIndex("inlive_id") > -1) {
            setInLiveId("" + cursor.getLong(cursor.getColumnIndex("inlive_id")));
        }

        if(cursor.getColumnIndex("message") > -1) {
            setMessage(cursor.getString(cursor.getColumnIndex("message")));
        }

        if(cursor.getColumnIndex("start_timestamp") > -1) {
            setStart_timestamp(cursor.getLong(cursor.getColumnIndex("start_timestamp")));
        }

        if(cursor.getColumnIndex("league") > -1) {
            setLeague(cursor.getString(cursor.getColumnIndex("league")));
        }

        if(cursor.getColumnIndex("match") > -1) {
            setMatch(cursor.getString(cursor.getColumnIndex("match")));
        }

        if(cursor.getColumnIndex("tip") > -1) {
            setTip(cursor.getString(cursor.getColumnIndex("tip")));
        }

        if(cursor.getColumnIndex("score") > -1) {
            setScore(cursor.getString(cursor.getColumnIndex("score")));
        }

        if(cursor.getColumnIndex("odd") > -1) {
            setOdd(cursor.getString(cursor.getColumnIndex("odd")));
        }

        if(cursor.getColumnIndex("status") > -1) {
            setStatus(cursor.getString(cursor.getColumnIndex("status")));
        }

        if(cursor.getColumnIndex("received") > -1) {
            setReceived(cursor.getLong(cursor.getColumnIndex("received")));
        }

        if(cursor.getColumnIndex("type") > -1) {
            setType(cursor.getString(cursor.getColumnIndex("type")));
        }

    }

    private boolean jsonParamExist(JSONObject object, String param){

        if( object.has(param) && !object.isNull(param) ){
            return  true;
        }

        return false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInLiveId() {
        return inLiveId;
    }

    public void setInLiveId(String inLiveId) {
        this.inLiveId = inLiveId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getStart_timestamp() {
        return start_timestamp;
    }

    public void setStart_timestamp(long start_timestamp) {
        this.start_timestamp = start_timestamp;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getOdd() {
        return odd;
    }

    public void setOdd(String odd) {
        this.odd = odd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getReceived() {
        return received;
    }

    public void setReceived(long received) {
        this.received = received;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

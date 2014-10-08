package cz.inlive.inlive.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cz.inlive.inlive.database.objects.Bet;
import cz.inlive.inlive.database.objects.Info;
import cz.inlive.inlive.utils.Log;
import cz.inlive.inlive.utils.Constants;

/**
 * Created by Tkadla on 15. 9. 2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    //Tables
    private String TABLE_BET = "bet";
    private String TABLE_INFO = "info";

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private String mFilePath;

    /**
     * Constructor
     *
     * @param context Context
     * @param dir External path to dir
     * @param sqlFile SQLite filename
     * @param mode false - internal storage access, true - external
     */
    public DatabaseHandler(Context context, String dir, String sqlFile, boolean mode) {
        super(context, sqlFile, null, DATABASE_VERSION);

        mContext = context;

        File sqlDbFile;

        if(mode) {
            sqlDbFile = new File(context.getFilesDir(), "/" + sqlFile);
        } else {
            sqlDbFile = new File(dir + sqlFile);
        }

        mFilePath = sqlDbFile.getAbsolutePath();

        if(sqlDbFile.exists()) {
            Log.d("DatabaseHandler", "Database exists");
            mDatabase = SQLiteDatabase.openOrCreateDatabase(sqlDbFile, null); //sqlFile, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            if(mDatabase.needUpgrade(DATABASE_VERSION)) {
                upgradeDatabase(mDatabase, mDatabase.getVersion(), DATABASE_VERSION);
            }
        } else {
            try {
                Log.d("DatabaseHandler", "Extrahuji DB " + sqlFile + " z assetu");
                sqlDbFile = extractDbFromAssets(context, dir, sqlFile, mode);
                mDatabase = SQLiteDatabase.openOrCreateDatabase(sqlDbFile, null);

                if(mDatabase.needUpgrade(DATABASE_VERSION)) {
                    upgradeDatabase(mDatabase, mDatabase.getVersion(), DATABASE_VERSION);
                }
            } catch (IOException e) {
                Log.e("DatabaseHandler", e.toString());
            }
        }
    }

    // extracts DB from app assets directory
    private File extractDbFromAssets(Context context, String dir, String sqlFile, boolean privateMode) throws IOException {

        if(privateMode) {
            InputStream is = context.getAssets().open(sqlFile);
            OutputStream out = context.openFileOutput(sqlFile, Context.MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            is.close();
            out.close();

            return new File(context.getFilesDir(), "/" + sqlFile);
        } else {
            File f = new File(dir);

            if (f.mkdirs() || f.isDirectory()) {

                InputStream is = context.getAssets().open(sqlFile);
                OutputStream out = new FileOutputStream(dir + sqlFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                is.close();
                out.close();

                return new File(dir + sqlFile);
            }
        }
        return null;
    }

    private static int executeSqlScript(SQLiteDatabase db, String queries, boolean transactional)
            throws IOException, SQLException, SQLiteException {
        byte[] bytes = queries.getBytes();
        String sql = new String(bytes, "UTF-8");
        String[] lines = sql.split(";");
        int count;
        if (transactional) {
            count = executeSqlStatementsInTx(db, lines);
        } else {
            count = executeSqlStatements(db, lines);
        }

        return count;
    }

    private static int executeSqlStatementsInTx(SQLiteDatabase db, String[] statements) throws SQLException {
        db.beginTransaction();
        try {
            int count = executeSqlStatements(db, statements);
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    private static int executeSqlStatements(SQLiteDatabase db, String[] statements) {
        int count = 0;
        for (String line : statements) {
            line = line.trim();
            if (line.length() > 0) {

                try {
                    db.execSQL(line);
                } catch(SQLException e) {
                    if(e.getMessage() != null && !e.getMessage().contains("duplicate column name:")) {
                        throw new SQLException(e.getMessage());
                    } else if(e.getMessage() != null) {
                        Log.e("DatabaseHandler:executeSqlStatements", "Detekovan SQL duplikatni sloupec: " + e.getMessage());
                    } else {
                        Log.e("DatabaseHandler:executeSqlStatements", e.toString());
                    }
                }

                count++;
            }
        }
        return count;
    }

    private void upgradeDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = "";

        query = Migrations.migrate(oldVersion, newVersion);

        try {
            executeSqlScript(db, query, true);
        } catch (IOException e) {
            Log.e("DatabaseHelper", e.getMessage());
        }

        db.setVersion(newVersion);
    }

    public synchronized void closeDB() {

        if(mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Save info from server (Via GCM)
     * @param info
     */
    public void saveInfo( Info info ){
        String query = " INSERT INTO " + TABLE_INFO + "(`message`, `type`, `received`) VALUES ( '" + info.getMessage() + "','" + info.getType() +"','" + info.getReceived() +"' )";
        mDatabase.execSQL(query);

        //keep DB size in check
        deleteOldInfos();
    }

    /**
     * Persist all best in DB
     * @param bets
     */
    public void saveBets(List<Bet> bets){
        for(Bet bet: bets){
            saveBet(bet);
        }

        // keep DB size in check
        deleteOldBets();
    }

    private void deleteOldInfos(){
        String query = "DELETE FROM "+ TABLE_INFO +" WHERE `id` NOT IN (SELECT `id` FROM "+ TABLE_INFO +" ORDER BY `id` DESC LIMIT "+ Constants.MAX_INFO_IN_DB +")";
        mDatabase.execSQL(query);
    }

    private void deleteOldBets(){
        String query = "DELETE FROM "+ TABLE_BET +" WHERE `id` NOT IN (SELECT `id` FROM "+ TABLE_BET +" ORDER BY `id` DESC LIMIT "+ Constants.MAX_BETS_IN_DB +")";
        mDatabase.execSQL(query);
    }

    /**
     * Retreive given number of infos from DB
     * @param offset - number of pages from start
     * @param limit - maximum of records to return
     * @return
     */
    public ArrayList<Info> getInfo ( int offset, int limit ){
        ArrayList<Info> infos = new ArrayList<Info>(limit);

        String query = "SELECT * FROM " + TABLE_INFO + " ORDER BY datetime(`received`) ASC LIMIT " + (offset * Constants.INFO_PER_PAGE) + "," + limit;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                infos.add(infoFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return infos;
    }

    /**
     * Construct Bets fronm cursor
     * @param cursor
     * @return
     */
    public ArrayList<Bet> betsFromCursor(Cursor cursor){

        ArrayList<Bet> bets = new ArrayList<Bet>();

        if(cursor.moveToFirst()) {

            do {
                bets.add(betFromCursor(cursor));
            }while (cursor.moveToNext());

        }

        return bets;
    }

    public ArrayList<Bet> betsFromJSON(JSONObject object) throws JSONException{

        ArrayList<Bet> bets = new ArrayList<Bet>();


        if( !object.has("result") || object.isNull("result")){
            return bets;
        }

        JSONObject result = object.getJSONObject("result");

        if( !result.has("matches") || result.isNull("matches") ){
            return bets;
        }

        JSONArray matches = result.getJSONArray("matches");

        for(int index = 0; index < matches.length(); index++){
            Bet bet = new Bet();
            bet.parseJSON(matches.getJSONObject(index));

            bets.add(bet);
        }

        return bets;
    }

    /**
     * Construct Bet from cursor
     * @param cursor
     * @return
     */
    private Bet betFromCursor(Cursor cursor) {

        Bet bet = new Bet();
        bet.parseCursor(cursor);

        return bet;
    }


    private Info infoFromCursor(Cursor cursor) {
        Info i = new Info();
        i.parseCursor(cursor);
        return i;
    }

    /**
     * Save or update bat in DB
     * @param bet
     */
    public void saveBet(Bet bet){

        String query = " INSERT OR REPLACE INTO " + TABLE_BET + " (inlive_id, message, start_timestamp, league, match, tip, score, odd, status, received, type ) " +
                "VALUES ('" + bet.getInLiveId() +  "'," + DatabaseUtils.sqlEscapeString( bet.getMessage() ) + ",'" + bet.getStart_timestamp() + "'," + DatabaseUtils.sqlEscapeString( bet.getLeague() ) + "," + DatabaseUtils.sqlEscapeString( bet.getMatch() ) + ",'" + bet.getTip() + "','" + bet.getScore() + "','" + bet.getOdd() + "','" + bet.getStatus() + "','" + bet.getReceived() + "','" + bet.getType() + "') ";

        mDatabase.execSQL(query);
    }

    /**
     * Get bets with limit
     * @return
     */
    public ArrayList<Bet> getBets( int offset, int limit ){
        ArrayList<Bet> bets = new ArrayList<Bet>(limit);

        String query = "SELECT * FROM " + TABLE_BET + " ORDER BY `id` ASC LIMIT " + (offset * Constants.INFO_PER_PAGE) + "," + limit;

        Cursor cursor = mDatabase.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                bets.add(betFromCursor(cursor));
            } while (cursor.moveToNext());
        }


        cursor.close();

        return bets;
    }
}

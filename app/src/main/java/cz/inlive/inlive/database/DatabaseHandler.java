package cz.inlive.inlive.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cz.inlive.inlive.utils.Log;

/**
 * Created by Tkadla on 15. 9. 2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

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
}

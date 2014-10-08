package cz.inlive.inlive;

import android.app.Application;
import android.os.Environment;

import cz.inlive.inlive.database.DatabaseHandler;
import cz.inlive.inlive.network.NetworkHandler;

/**
 * Created by Tkadla on 13. 9. 2014.
 */
public class InLiveApplication extends Application {

    private NetworkHandler mNetworkHandler;

    private DatabaseHandler mDatabaseHandler;
    private String mSqlPath = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //Init all singletons

        mNetworkHandler = new NetworkHandler(this);

        String state = Environment.getExternalStorageState();
        boolean privateMode;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mSqlPath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + this.getPackageName() + "/files/";
            privateMode = false;
        } else {
            mSqlPath = "";
            privateMode = true;
        }

        mDatabaseHandler = new DatabaseHandler(this, mSqlPath, "inlive.sqlite", privateMode);

    }

    public NetworkHandler getNetworkHandler() {
        return mNetworkHandler;
    }

    public DatabaseHandler getDatabaseHandler() {return mDatabaseHandler; }
}

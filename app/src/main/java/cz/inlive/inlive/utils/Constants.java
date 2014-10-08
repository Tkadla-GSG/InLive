package cz.inlive.inlive.utils;

import android.app.AlarmManager;

/**
 * Created by Tkadla on 13. 9. 2014.
 */
public class Constants {

    //API
    public static String URL_BASE = "http://inlive.deevy.eu/api/1/";

    //GCM
    public static String SENDER_ID = "1061629046123";

    //Message types
    public static String TYPE_INFO = "info";
    public static String TYPE_UPDATE = "update";
    public static String TYPE_BET = "bet";

    //UI
    public static final int INFO_PER_PAGE = 10;

    public static final int MAX_INFO_IN_DB = 50;

    public static final int MAX_BETS_IN_DB = 300;

    //Default slack for first update
    public static final long UPDATE_SLACK = 1 * 7 * 24 * 60 * 60 * 1000; // 1 week in milies

    //Default time delay for update service
    public static final long UPDATE_DELAY = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
}

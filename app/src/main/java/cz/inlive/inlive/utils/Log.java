package cz.inlive.inlive.utils;


/**
 * Class replaces standard std.out so it can be easily turn on and off for debugging and deployment
 *
 */
public class Log {
    public static final boolean LOG = true;
    public static final boolean DEBUG = true;

    public static void i(String tag, String string, Throwable tr) {
        if (LOG)
            android.util.Log.i(tag, string, tr);
    }

    public static void i(String tag, String string) {
        if (LOG)
            android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string, Throwable tr) {
        if (LOG) {
            android.util.Log.e(tag, string, tr);
        }
    }

    public static void e(String tag, String string) {
        if (LOG)
            android.util.Log.e(tag, string);
    }

    public static void d(String tag, String string, Throwable tr) {
        if (LOG)
            android.util.Log.d(tag, string, tr);
    }

    public static void d(String tag, String string) {
        if (LOG)
            android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string, Throwable tr) {
        if (LOG)
            android.util.Log.v(tag, string, tr);
    }

    public static void v(String tag, String string) {
        if (LOG)
            android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string, Throwable tr) {
        if (LOG)
            android.util.Log.w(tag, string, tr);
    }

    public static void w(String tag, String string) {
        if (LOG)
            android.util.Log.w(tag, string);
    }
}

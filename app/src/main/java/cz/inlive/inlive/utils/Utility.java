package cz.inlive.inlive.utils;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tkadla on 5. 10. 2014.
 */
public class Utility {

    public static String miliesToDateString(Context context, long ts){

        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date(ts));// all done

        String date = "";

        if(DateUtils.isToday(cal.getTimeInMillis())) {
            // Omit date, if date is today
            date = DateUtils.formatDateTime(context, cal.getTimeInMillis(), DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);
        }else{
            date = DateUtils.formatDateTime(context, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);
        }

        return date;

    }

}

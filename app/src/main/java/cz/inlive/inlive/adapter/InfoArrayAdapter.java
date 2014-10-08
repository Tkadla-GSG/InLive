package cz.inlive.inlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.inlive.inlive.R;
import cz.inlive.inlive.database.objects.Info;
import cz.inlive.inlive.utils.Utility;

/**
 * Created by Tkadla on 5. 10. 2014.
 */
public class InfoArrayAdapter extends ArrayAdapter<Info> {

    private Context mContext;
    private int mLayoutResource;
    private List<Info> mInfos;

    private LayoutInflater mLayoutInflater;

    public InfoArrayAdapter(Context context, int resource, List<Info> objects) {
        super(context, resource, objects);

        mContext = context;
        mLayoutResource = resource;
        mInfos = objects;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        InfoHolder holder = null;

        if(row == null)
        {
            row = mLayoutInflater.inflate(mLayoutResource, parent, false);

            holder = new InfoHolder();
            holder.text = (TextView)row.findViewById(android.R.id.text1);
            holder.date = (TextView)row.findViewById(android.R.id.text2);

            row.setTag(holder);
        }
        else
        {
            holder = (InfoHolder)row.getTag();
        }

        Info info = mInfos.get(position);
        holder.text.setText(info.getMessage());
        holder.date.setText("" + Utility.miliesToDateString(mContext, info.getReceived()));

        return row;
    }

    static class InfoHolder
    {
        TextView text;
        TextView date;

    }
}

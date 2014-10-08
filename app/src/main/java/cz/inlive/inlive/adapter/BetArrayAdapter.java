package cz.inlive.inlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import cz.inlive.inlive.R;
import cz.inlive.inlive.database.objects.Bet;
import cz.inlive.inlive.database.objects.Info;
import cz.inlive.inlive.utils.Utility;

/**
 * Created by Tkadla on 5. 10. 2014.
 */
public class BetArrayAdapter extends ArrayAdapter<Bet> {

    private Context mContext;
    private int mLayoutResource;
    private List<Bet> mBets;

    private LayoutInflater mLayoutInflater;

    public BetArrayAdapter(Context context, int resource, List<Bet> objects) {
        super(context, resource, objects);

        mContext = context;
        mLayoutResource = resource;
        mBets = objects;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BetHolder holder = null;

        if(row == null)
        {
            row = mLayoutInflater.inflate(mLayoutResource, parent, false);

            holder = new BetHolder();
            holder.match = (TextView)row.findViewById(android.R.id.text1);
            holder.status = (TextView)row.findViewById(R.id.status);
            holder.score = (TextView)row.findViewById(R.id.score);
            holder.league = (TextView)row.findViewById(R.id.league);
            holder.tip = (TextView)row.findViewById(R.id.tip);
            holder.odd = (TextView)row.findViewById(R.id.odd);
            holder.date = (TextView)row.findViewById(android.R.id.text2);
            holder.bg = (LinearLayout)row.findViewById(R.id.bet_list_bg);

            // every other item will have
            if(position % 2 == 0){
                //holder.bg.setBackgroundColor(mContext.getResources().getColor(R.color.akcent_light_grey));
            }

            row.setTag(holder);
        }
        else
        {
            holder = (BetHolder)row.getTag();
        }

        Bet bet = mBets.get(position);

        holder.match.setText(bet.getMatch());
        holder.league.setText(bet.getLeague());
        holder.status.setText(bet.getStatus());
        holder.score.setText(bet.getScore());
        holder.tip.setText(bet.getTip());
        holder.odd.setText(bet.getOdd());

        holder.date.setText("" + Utility.miliesToDateString(mContext, bet.getStart_timestamp() * 1000));


        return row;
    }

    static class BetHolder
    {
        TextView match;
        TextView status;
        TextView league;
        TextView score;
        TextView tip;
        TextView odd;
        TextView date;
        LinearLayout bg;

    }
}

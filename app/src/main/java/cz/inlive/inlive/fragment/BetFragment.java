package cz.inlive.inlive.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import cz.inlive.inlive.InLiveApplication;
import cz.inlive.inlive.R;
import cz.inlive.inlive.adapter.InfoArrayAdapter;
import cz.inlive.inlive.database.DatabaseHandler;
import cz.inlive.inlive.database.objects.Info;
import cz.inlive.inlive.listeners.BetFragmentInteractionListener;
import cz.inlive.inlive.network.JSONObjectResponse;
import cz.inlive.inlive.utils.Log;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * BetFragmentInteractionListener interface
 * to handle interaction events.
 * Use the {@link BetFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class BetFragment extends Fragment {

    private BetFragmentInteractionListener mListener;

    private DatabaseHandler mDatabaseHandler;

    private ListView mList;
    private ArrayAdapter<Info> mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BetFragment.
     */
    public static BetFragment newInstance() {
        BetFragment fragment = new BetFragment();
        return fragment;
    }
    public BetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseHandler = ((InLiveApplication)(getActivity().getApplication())).getDatabaseHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v =  inflater.inflate(R.layout.fragment_bet, container, false);

       mList = ( ListView ) v.findViewById(android.R.id.list);

       //TODO remake as extedable adapter
       ArrayList<Info> infos = mDatabaseHandler.getInfo(0, 20);

        // hide empty
       if(infos.size() > 0){
           ((TextView)v.findViewById(android.R.id.empty)).setVisibility(View.GONE);
       }

       // infos are in array in wrong order
       Collections.reverse(infos);

       mAdapter = new InfoArrayAdapter( getActivity(), R.layout.layout_info_list_item, infos );
       mList.setAdapter(mAdapter);

       return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (BetFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

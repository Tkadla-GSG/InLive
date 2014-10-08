package cz.inlive.inlive.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.inlive.inlive.InLiveApplication;
import cz.inlive.inlive.R;

import cz.inlive.inlive.adapter.BetArrayAdapter;
import cz.inlive.inlive.database.DatabaseHandler;
import cz.inlive.inlive.database.objects.Bet;
import cz.inlive.inlive.listeners.BetFragmentInteractionListener;
import cz.inlive.inlive.network.JSONObjectResponse;
import cz.inlive.inlive.network.NetworkHandler;
import cz.inlive.inlive.utils.Log;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the Callbacks
 * interface.
 */
public class HistoryFragment extends Fragment {

    private BetFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private ArrayList<Bet> mBets;

    private DatabaseHandler mDatabaseHandler;
    private NetworkHandler mNetworkHandler;

    // TODO: Rename and change types of parameters
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNetworkHandler = ((InLiveApplication)getActivity().getApplication()).getNetworkHandler();
        mDatabaseHandler = ((InLiveApplication)getActivity().getApplication()).getDatabaseHandler();        mDatabaseHandler = ((InLiveApplication)getActivity().getApplication()).getDatabaseHandler();

        mBets = mDatabaseHandler.getBets(0, 60);

        // TODO make this expandable
        mAdapter = new BetArrayAdapter(getActivity(),
                R.layout.layout_bet_list_item, mBets );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        final Context mContext = getActivity();

        // hide empty
        if(mBets.size() > 0){
            ((TextView)view.findViewById(android.R.id.empty)).setVisibility(View.GONE);
        }

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        return view;
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

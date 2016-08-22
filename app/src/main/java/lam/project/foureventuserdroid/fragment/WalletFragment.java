package lam.project.foureventuserdroid.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vipul.hp_hp.timelineview.TimelineView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.fragment.TimeLine.TimeLineAdapter;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.RecordListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {

    private float mBalance;
    private String mEmail;

    private static final String NAME = "Portafoglio";

    private FloatingActionButton fab;

    private RecyclerView mRecyclerView;

    private TimeLineAdapter mTimeLineAdapter;

    private List<Record> mDataList = new ArrayList<>();


    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        this.mBalance = getArguments().getFloat(User.Keys.BALANCE);
        this.mEmail = getArguments().getString(User.Keys.EMAIL);

        setTitle();

        ((TextView) rootView.findViewById(R.id.user_balance)).setText(Float.toString(mBalance));

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_wallet);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);

        setModel();

        return rootView;

    }

    private void setTitle () {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }

    private void setModel() {

        String uri = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                .appendEncodedPath(mEmail).getUri();

        RecordListRequest recordListRequest = new RecordListRequest(uri, null, new Response.Listener<List<Record>>() {
            @Override
            public void onResponse(List<Record> response) {

                mDataList.clear();
                mDataList.addAll(response);

                mTimeLineAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error.toString());
            }
        });

        VolleyRequest.get(getContext()).add(recordListRequest);
    }
}

package lam.project.foureventuserdroid.fragment;


import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.TicketDetailsActivity;
import lam.project.foureventuserdroid.fragment.recyclerView.TicketAdapter;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.EventListRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.RecordListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

import static android.view.View.INVISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketsFragment extends Fragment {

    private List<Record> mRecords = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TicketAdapter mAdapter;

    private ImageView sadEmoticon;
    private TextView notEvents;

    private final static String NAME = "Biglietti";

    public TicketsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setModel();

        final View rootView = inflater.inflate(R.layout.fragment_participation, container, false);

        setTitle();

        sadEmoticon = (ImageView) rootView.findViewById(R.id.sad_emoticon);
        notEvents = (TextView) rootView.findViewById(R.id.not_events);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tickets_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new TicketAdapter(getActivity(), mRecords);

        mRecyclerView.setAdapter(mAdapter);

        return rootView;

    }

    private void setTitle () {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }

    private void setModel(){

        String url = FourEventUri.Builder.create(FourEventUri.Keys.TICKET)
                .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

        RecordListRequest request = new RecordListRequest(url, null,
                new Response.Listener<List<Record>>() {
                    @Override
                    public void onResponse(List<Record> response) {

                        mRecords.clear();
                        mRecords.addAll(response);

                        mAdapter.notifyDataSetChanged();

                        mRecyclerView.setVisibility(View.VISIBLE);
                        sadEmoticon.setVisibility(INVISIBLE);
                        notEvents.setVisibility(INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        sadEmoticon.setVisibility(View.VISIBLE);
                        notEvents.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(INVISIBLE);

                    }
                });

        VolleyRequest.get(getContext()).add(request);
    }
}

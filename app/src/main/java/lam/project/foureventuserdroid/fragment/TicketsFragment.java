package lam.project.foureventuserdroid.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.fragment.recyclerView.TicketAdapter;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.RecordListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

import static android.view.View.INVISIBLE;

public class TicketsFragment extends Fragment {

    private List<Record> mRecords = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TicketAdapter mAdapter;

    private ImageView sadEmoticon;
    private TextView notEvents;

    private final static String NAME = "Biglietti";

    public TicketsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setModel();

        setTitle();

        return initView(inflater.inflate(R.layout.fragment_participation, container, false));

    }

    /**
     * Metodo per inizializzare i campi del fragment
     * @param view la view del tickets fragment
     * @return la view completa di tutti i campi
     */
    private View initView(View view) {

        sadEmoticon = (ImageView) view.findViewById(R.id.sad_emoticon);
        notEvents = (TextView) view.findViewById(R.id.not_events);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.tickets_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new TicketAdapter(getActivity(), mRecords);

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    /**
     * Si setta il titolo del fragment
     */
    private void setTitle () {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }

    /**
     * Si raccolgono dal server tutti i biglietti acquistati dall'utente
     */
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

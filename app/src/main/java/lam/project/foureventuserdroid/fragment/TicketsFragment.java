package lam.project.foureventuserdroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.RecordListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketsFragment extends Fragment {

    private final static String NAME = "Biglietti";

    private List<Record> mRecords = new ArrayList<>();
    private TextView mTickets;

    public TicketsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_participation, container, false);
        mTickets = (TextView) rootView.findViewById(R.id.ticket_list);

        setTitle();
        setmRecords();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setmRecords() {

        String url = FourEventUri.Builder.create(FourEventUri.Keys.TICKET)
                .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

        RecordListRequest getAllRecords = new RecordListRequest(url, null,
                new Response.Listener<List<Record>>() {
                    @Override
                    public void onResponse(List<Record> response) {

                        mRecords.clear();
                        mRecords.addAll(response);
                        updateList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println(error.toString());
                    }
                }
        );

        VolleyRequest.get(getContext()).add(getAllRecords);
    }

    private void updateList() {

        mTickets.setText(mRecords.get(0).mId);
    }

    private void setTitle() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }

}

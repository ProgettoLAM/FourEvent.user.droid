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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.TicketDetailsActivity;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.RecordListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketsFragment extends Fragment {

    private List<Record> mRecords = new ArrayList<>();
    private TextView mTickets;


    public TicketsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        startActivity(new Intent(getContext(), TicketDetailsActivity.class));
        return inflater.inflate(R.layout.fragment_participation, container, false);
    }
}

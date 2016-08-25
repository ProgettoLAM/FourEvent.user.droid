package lam.project.foureventuserdroid;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.RecordListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

public class TicketDetailsActivity extends AppCompatActivity {

    private List<Record> mRecords = new ArrayList<>();
    private TextView mTickets;
    private Button mSyncNfc;

    private ProgressDialog mProgressDialog;
    private boolean isSearching;

    NfcAdapter mNfcAdapter;
    Context context;

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_ticket);

        context = this;

        mTickets = (TextView) findViewById(R.id.ticket_list);

        mSyncNfc = (Button)findViewById(R.id.ticket_sync);
        mSyncNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mNfcAdapter.isEnabled()) {

                    Toast.makeText(getApplicationContext(), "Per proseguire attivare la comunicazione NFC", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

                } else {

                    mProgressDialog = ProgressDialog.show(context,"Ricerca braccialetto",
                            "Ricerca del braccialetto FourEvent in corso ...",true,true);

                    isSearching = true;
                }
            }
        });


        setmRecords();

        //handleIntent(getIntent());
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

        VolleyRequest.get(this).add(getAllRecords);
    }

    private void updateList() {

        mTickets.setText(mRecords.get(0).mId);
    }
}

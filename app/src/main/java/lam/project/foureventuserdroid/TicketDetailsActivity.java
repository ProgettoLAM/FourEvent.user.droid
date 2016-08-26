package lam.project.foureventuserdroid;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class TicketDetailsActivity extends AppCompatActivity {

    public static final String TAG = "NfcDemo";

    private final String ID = "57bf19e25dfa260b9dd83edd";

    private ProgressDialog mProgressDialog;
    private TextView mTextView;
    private Button mButton;
    private NfcAdapter mNfcAdapter;
    private boolean mIsSearching;
    private boolean mIsSynced;

    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_ticket);

        mTextView = (TextView) findViewById(R.id.ticket_list);
        mTextView.setText(ID);

        mButton = (Button) findViewById(R.id.ticket_sync);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mIsSearching = true;

                mProgressDialog = ProgressDialog.show(v.getContext(),"Ricerca braccialetto","Ricerca braccialetto FourEvent in corso...",true,true);
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this,"NFC non supportato, impossibile continuare",Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        if(mIsSearching && intent.hasExtra(NfcAdapter.EXTRA_TAG)) {


            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NdefMessage ndefMessage = createNdefMessage(ID);

            writeNdefMessage(tag,ndefMessage);

            mProgressDialog.dismiss();
            mIsSearching = false;
        }
    }

    @Override
    protected void onResume() {

        findViewById(R.id.ticket_synced).setVisibility(View.VISIBLE);

        if(!mIsSynced) {

            findViewById(R.id.ticket_synced).setVisibility(View.INVISIBLE);
        }

        enableForegroundDispatchSystem();
        super.onResume();
    }

    @Override
    protected void onPause() {

        disableForegroundDispatchSystem();
        super.onPause();
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {


        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if(ndefFormatable == null) {

                Toast.makeText(this,"Tag is not NDEF formatable",Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

        } catch (IOException | FormatException e) {

            e.printStackTrace();

        }

    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {
            if (tag == null) {

                Toast.makeText(this, "Tag is null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if (ndef == null) {

                formatTag(tag, ndefMessage);

            } else {

                ndef.connect();

                if(!ndef.isWritable()) {

                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Snackbar.make(mTextView,"Braccialetto sincronizzato con successo",Snackbar.LENGTH_LONG).show();
                mIsSynced = true;
            }

        } catch (IOException | FormatException ex) {

            Toast.makeText(this, ex.getLocalizedMessage()   , Toast.LENGTH_SHORT).show();
        }
    }

    private NdefRecord createTextRecord (String content) {

        try{

            byte[] language;

            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1+languageSize+textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language,0,languageSize);
            payload.write(text,0,textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT,new byte[0],payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private NdefMessage createNdefMessage (String content) {

        return new NdefMessage(new NdefRecord[]{ createTextRecord(content) });
    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, TicketDetailsActivity.class);
        intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilters = new IntentFilter[]{};

        mNfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    private void disableForegroundDispatchSystem() {

        mNfcAdapter.disableForegroundDispatch(this);
    }
}

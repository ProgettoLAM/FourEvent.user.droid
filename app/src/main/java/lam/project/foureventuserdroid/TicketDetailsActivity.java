package lam.project.foureventuserdroid;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.qr_code.Contents;
import lam.project.foureventuserdroid.utils.qr_code.QRCodeEncoder;

public class TicketDetailsActivity extends AppCompatActivity {
    private final String NAME = "Dettagli biglietto";

    private ProgressDialog mProgressDialog;
    private NfcAdapter mNfcAdapter;
    private boolean mIsSearching;
    private boolean mIsSynced;
    private Button mButtonNFC;

    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(NAME);
        setSupportActionBar(toolbar);


        mId = getIntent().getStringExtra(Record.Keys.ID);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mButtonNFC = (Button) findViewById(R.id.button_nfc);

        mButtonNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mNfcAdapter.isEnabled()) {

                    Toast.makeText(v.getContext(),"Perfavore attiva l'NFC e torna indietro per tornare all'applicazione!",Toast.LENGTH_LONG).show();

                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

                } else if(mNfcAdapter.isEnabled()) {

                    mIsSearching = true;
                    mProgressDialog = ProgressDialog.show(v.getContext(),"Ricerca braccialetto","Ricerca braccialetto NFC in corso...",true,true);
                }
            }
        });

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this,"NFC non supportato, Utilizzare codice QR",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        if(mIsSearching && intent.hasExtra(NfcAdapter.EXTRA_TAG)) {


            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NdefMessage ndefMessage = createNdefMessage(mId);

            writeNdefMessage(tag,ndefMessage);

            mProgressDialog.dismiss();
            mIsSearching = false;
        }
    }

    public void qrButton(final View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Scansiona il codice, partecipa all'evento!");

        View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_qrcode, (ViewGroup) getWindow().getDecorView(), false);
        ImageView imgQr = (ImageView) viewInflated.findViewById(R.id.img_qrcode);
        builder.setView(viewInflated);

        //Generazione di un codice Qr con l'id dell'evento
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(mId,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), 600);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            imgQr.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }


        builder.setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    @Override
    protected void onResume() {

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
                mButtonNFC.setEnabled(false);
                Snackbar.make(mButtonNFC,"Braccialetto sincronizzato con successo",Snackbar.LENGTH_LONG).show();
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

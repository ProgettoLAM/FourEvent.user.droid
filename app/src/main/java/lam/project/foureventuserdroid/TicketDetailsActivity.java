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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

    private ProgressDialog mProgressDialog;
    private NfcAdapter mNfcAdapter;
    private boolean mIsSearching;
    private Button mButtonNFC;

    private boolean mNfcGo;

    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_ticket);

        initView();

    }

    /**
     * Metodo per inizializzare i campi del dettaglio di un biglietto
     */
    private void initView() {

        //Si setta il titolo del dettaglio di un biglietto
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String NAME = "Dettagli biglietto";
        toolbar.setTitle(NAME);
        setSupportActionBar(toolbar);

        //Si recupera l'id del record proveniente dalla recycler view
        mId = getIntent().getStringExtra(Record.Keys.ID);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mButtonNFC = (Button) findViewById(R.id.button_nfc);

        //Al click del bottone per la sincronizzazione del NFC
        mButtonNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nfcButton(v);
            }
        });

        //Se l'Nfc non è disponibile, viene mostrato un messaggio
        if (mNfcAdapter == null) {

            Toast.makeText(this,"NFC non supportato, Utilizzare codice QR",Toast.LENGTH_SHORT).show();

        } else
            mNfcGo = true;
    }

    /**
     * Click del bottone per leggere il codice QR del biglietto
     * @param view view del bottone
     */
    public void qrButton(final View view) {

        //Si crea un dialog con la visualizzazione del codice QR
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Scansiona il codice, partecipa all'evento!");

        View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_qrcode, (ViewGroup) getWindow().getDecorView(), false);
        ImageView imgQr = (ImageView) viewInflated.findViewById(R.id.img_qrcode);
        builder.setView(viewInflated);

        //Generazione di un codice Qr con l'id del record
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(mId,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), 600);
        try {

            //Si setta il codice nell'immagine del dialog
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

    //Region NFC

    private void nfcButton(View v) {

        //Se l'Nfc non è attivato, si viene rediretti alle opzioni del dispositivo per attivarlo
        if(!mNfcAdapter.isEnabled()) {

            Toast.makeText(v.getContext(),"Perfavore attiva l'NFC e torna indietro per tornare all'applicazione!",Toast.LENGTH_LONG).show();

            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

        //Altrimenti, se l'Nfc è attivato, si inizia la sincronizzazione
        } else if(mNfcAdapter.isEnabled()) {

            mIsSearching = true;
            mProgressDialog = ProgressDialog.show(v.getContext(),"Ricerca braccialetto","Ricerca braccialetto NFC in corso...",true,true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        //Se si sta sincronizzando il tag, si crea un messaggio Ndef con l'id del record
        if(mIsSearching && intent.hasExtra(NfcAdapter.EXTRA_TAG)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NdefMessage ndefMessage = createNdefMessage(mId);

            writeNdefMessage(tag,ndefMessage);

            mProgressDialog.dismiss();
            mIsSearching = false;
        }
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

    /**
     * Formattazione del tag NFC
     * @param tag tag nfc
     * @param ndefMessage messaggio da scrivere all'interno
     */
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

    /**
     * Scrittura del messaggio nel tag NFC
     * @param tag tag NFC
     * @param ndefMessage messaggio da scrivere all'interno
     */
    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        try {

            //Se il tag è nullo, viene mostrato un messaggio di errore
            if (tag == null) {

                Toast.makeText(this, "Tag is null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            //Si formatta il tag se la tecnologia NDEF non è presente
            if (ndef == null) {

                formatTag(tag, ndefMessage);

            } else {

                //Si connette al Ndef e si scrive il messaggio nel tag
                ndef.connect();

                if(!ndef.isWritable()) {

                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                mButtonNFC.setEnabled(false);

                Snackbar.make(mButtonNFC,"Braccialetto sincronizzato con successo",Snackbar.LENGTH_LONG)
                        .show();

            }

        } catch (IOException | FormatException ex) {

            Toast.makeText(this, ex.getLocalizedMessage()   , Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creazione di un testo di record nel tag
     * @param content contenuto
     * @return record Ndef
     */
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

    /**
     * Creazione del messaggio Ndef
     * @param content contenuto del messaggio
     * @return messaggio Ndef
     */
    private NdefMessage createNdefMessage (String content) {

        return new NdefMessage(new NdefRecord[]{ createTextRecord(content) });
    }

    private void enableForegroundDispatchSystem() {

        if(mNfcGo) {

            Intent intent = new Intent(this, TicketDetailsActivity.class);
            intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
            IntentFilter[] intentFilters = new IntentFilter[]{};

            mNfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
        }

    }

    private void disableForegroundDispatchSystem() {

        if(mNfcGo)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    //Endregion
}

package lam.project.foureventuserdroid;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.DateConverter;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.HandlerManager;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;


public class DetailsEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private final static float DEFAULT_ZOOM = 15.0f;

    private Event mCurrentEvent;

    private boolean isFabOpen;

    private AlertDialog mAlertDialog;

    private TextView detailTickets;

    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    public static String OPEN_FRAGMENT_WALLET = "Portafoglio";

    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    //Region fabListener

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int id = v.getId();

            switch (id) {
                case R.id.fab:

                    animateFAB();
                    break;

                case R.id.fab1:

                    shareEvent();
                    break;

                case R.id.fab2:

                    animateFAB();
                    if (mCurrentEvent.isFree()) {

                        addParticipation();

                    } else {

                        buyTicket();

                    }
                    break;

                case R.id.fab3:

                    try {
                        shareInCalendar();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    //Endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

        initView();
    }

    /**
     * Metodo per inizializzare i campi del dettaglio di un evento
     */
    private void initView() {

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        fab.setOnClickListener(fabClickListener);
        fab1.setOnClickListener(fabClickListener);
        fab2.setOnClickListener(fabClickListener);
        fab3.setOnClickListener(fabClickListener);

        detailTickets = (TextView) findViewById(R.id.detail_tickets);

        //Per disabilitare autofocus all'apertura della Activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Si salva in una variabile l'evento corrente preso dalla recycler view
        mCurrentEvent = getIntent().getParcelableExtra(Event.Keys.EVENT);

        setInfo(mCurrentEvent);

        //Inserimento del mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.anchor_map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Si settano le informazioni, facendo il bind tra le informazioni ricavate dal server e quelle
     * della view
     * @param event evento di cui si vogliono conoscere i dettagli
     */
    private void setInfo(Event event) {

        //Si settano le partecipazioni controllando se ha un max numero di tickets o no
        String participations = (event.mMaxTicket > 0) ? event.mParticipation + "/" + event.mMaxTicket
                : "" + event.mParticipation;

        updateParticipations(participations);

        //Si setta il prezzo controllando se è gratuito o no
        String price = (event.isFree()) ? event.mPrice : event.mPrice + "€";

        //Si setta la data e ora (se sono presenti anche quelli di fine)
        String time = DateConverter.getTime(event.mStartDate, event.mEndDate);
        String date = DateConverter.getDate(event.mStartDate, event.mEndDate);

        //Si setta l'indirizzo con via e città
        String address = event.mStreetAddress +", "+event.mAddress;

        //Se la data di fine è presente
        if(event.mEndDate != null) {

            final SpannableStringBuilder str = new SpannableStringBuilder(date);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 4, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 19, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            ((TextView) findViewById(R.id.detail_date)).setText(str);

        } else {

            ((TextView) findViewById(R.id.detail_date)).setText(date);
        }

        ((TextView) findViewById(R.id.detail_title)).setText(event.mTitle);
        ((TextView) findViewById(R.id.detail_distance)).setText(address);
        ((TextView) findViewById(R.id.detail_desc)).setText(event.mDescription);
        ((TextView) findViewById(R.id.detail_price)).setText(price);
        ((TextView) findViewById(R.id.detail_time)).setText(time);

        //Si setta l'autore e la relativa immagine del profilo
        TextView nameAuthor = (TextView) findViewById(R.id.profile_owner_name);
        nameAuthor.setText(mCurrentEvent.mAuthor);

        //TODO aggiungere immagine profilo planner, per ricercarla

        CircleImageView imgUser = (CircleImageView) findViewById(R.id.profile_image);

        String url = FourEventUri.Builder.create(FourEventUri.Keys.PLANNER)
                .appendPath("img").appendEncodedPath(mCurrentEvent.mAuthor).getUri();

        Picasso.with(this).load(url).into(imgUser);

    }

    /**
     * Animazione dei fab, all'apertura di quello principale
     */
    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;

        }
    }

    /**
     * Aggiornamento delle partecipazioni
     * @param participations la partecipazione
     */
    private void updateParticipations(String participations) {

        //Se l'evento è gratuito si controlla la partecipazione, per settare il riempimento dell'icona
        if (mCurrentEvent.isFree()) {

            if (mCurrentEvent.willPartecipate()) {

                fab2.setImageResource(R.drawable.ic_participation_full);

            } else {

                fab2.setImageResource(R.drawable.ic_participation_line);
            }

        }


        detailTickets.setText(participations);
    }

    //Region dei comportamenti dei fab

    /**
     * Acquisto del biglietto, al click del fab corrispondente
     */
    private void buyTicket() {

        //Creazione di un dialog con il Builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(fab2.getContext());

        String message;
        String title;

        DialogInterface.OnClickListener positiveListener;
        String positiveListenerText;

        //Se il costo del biglietto è inferiore al bilancio del portafoglio dell'utente
        if (Float.parseFloat(mCurrentEvent.mPrice) <= MainActivity.mCurrentUser.balance
                && !mCurrentEvent.willPartecipate()
                && mCurrentEvent.mParticipation < mCurrentEvent.mMaxTicket) {

            message = "Il biglietto ha un costo di " + mCurrentEvent.mPrice + " €." +
                    "\n\nHai un totale di " + MainActivity.mCurrentUser.balance + " €.\nVuoi acquistarlo?";

            title = "Acquisto biglietto";

            positiveListenerText = "Acquista";
            positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {

                    //Creo l'url della richiesta
                    String url = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                            .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

                    try {

                        //Creo un record con il prezzo pagato, l'email dell'utente e l'id dell'evento
                        JSONObject record = Record.Builder
                                .create(-Float.parseFloat(mCurrentEvent.mPrice), Record.Keys.BUY, MainActivity.mCurrentUser.email)
                                .withEvent(mCurrentEvent.mId)
                                .build().toJson();

                        CustomRequest createRecordRequest = new CustomRequest(Request.Method.PUT,
                                url, record, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        handleResponse(response);

                                        mCurrentEvent.willPartecipate();

                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Snackbar snackbarError = Snackbar.make(fab, HandlerManager.
                                        handleError(error), Snackbar.LENGTH_LONG);

                                snackbarError.getView().setBackgroundColor(ContextCompat.
                                        getColor(getApplicationContext(), R.color.lightRed));

                                snackbarError.show();
                            }
                        });

                        animateFAB();

                        VolleyRequest.get(fab2.getContext()).add(createRecordRequest);

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            };


        }
        //Se il numero di partecipazioni è uguale al numero massimo di ticket
        else if(mCurrentEvent.mParticipation == mCurrentEvent.mMaxTicket) {

            title = "Disponibilità terminata";
            message = "Mi dispiace, ma sono finiti tutti i biglietti disponibili!";

            positiveListener = null;
            positiveListenerText = null;
        }
        //Altrimenti, se l'utente non ha credito a sufficienza e non partecipa all'evento
        else if(!mCurrentEvent.willPartecipate()) {

            title = "Credito insufficiente";
            message = "Non hai abbastanza crediti per acquistare questo biglietto, ricarica il portafoglio!!";

            positiveListenerText = "Ricarica portafoglio";
            positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //L'utente viene rediretto sul portafoglio per ricaricarlo
                    Intent openFragmentBIntent = new Intent(getApplicationContext(), MainActivity.class);
                    openFragmentBIntent.putExtra(OPEN_FRAGMENT_WALLET, "Portafoglio");
                    startActivity(openFragmentBIntent);
                }
            };
        }
        //Se l'utente partecipa già all'evento, viene mostrato un messaggio
        else {

            title = "Partecipi già all'evento!";
            message = "Ehi tigre, sicuro di voler comprare il biglietto di nuovo?";

            positiveListener = null;
            positiveListenerText = null;
        }

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.setPositiveButton(positiveListenerText, positiveListener);
        mAlertDialog = builder.show();

    }

    /**
     * Aggiunta della partecipazione, in caso di evento gratuito
     */
    private void addParticipation() {

        try {

            //Si salva in una stringa se l'utente partecipa o no
            String participation = mCurrentEvent.willPartecipate() ? "notparticipate" : "participate";

            //Creo l'url della richiesta
            String url = FourEventUri.Builder.create(FourEventUri.Keys.EVENT)
                    .appendPath(participation).appendEncodedPath(mCurrentEvent.mId).getUri();

            //Invio al server l'email corrente dell'utente
            JSONObject userEmail = new JSONObject("{'email':'" + MainActivity.mCurrentUser.email + "'}");

            CustomRequest updateParticipations = new CustomRequest(Request.Method.POST, url, userEmail,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                Snackbar responseSnackbar = Snackbar.make(fab,
                                        response.getString("message"), Snackbar.LENGTH_SHORT);

                                responseSnackbar.getView().setBackgroundColor(ContextCompat
                                        .getColor(getApplicationContext(), R.color.lightGreen));

                                responseSnackbar.show();

                                //Si aggiunge la partecipazione dell'utente all'evento
                                mCurrentEvent.updateWillParticipate();

                                updateParticipations("" + response.getInt("participations"));


                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                        }
                    });

            VolleyRequest.get(this).add(updateParticipations);

        } catch (JSONException e) { e.printStackTrace();}
    }

    /**
     * Condivisione dell'evento su social/messaggistica
     */
    private void shareEvent() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        String address = mCurrentEvent.mStreetAddress;

        //Messaggio inviato all'applicazione esterna
        String text = "Hey, Guarda questo fantastico evento con FourEvent!\n"+mCurrentEvent.mTitle+
                " @ "+address+"\nhttp://annina.cs.unibo.it:8080/api/event";


        intent.putExtra(Intent.EXTRA_TEXT,text);

        startActivity(Intent.createChooser(intent, "Condividi l'evento"));


    }

    /**
     * Condivisione dell'evento sul calendario del proprio dispositivo
     * @throws ParseException
     */
    private void shareInCalendar() throws ParseException {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        //Si inviano al calendario il titolo, l'indirizzo e la descrizione
        intent.putExtra(CalendarContract.Events.TITLE, mCurrentEvent.mTitle);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mCurrentEvent.mAddress);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, mCurrentEvent.mDescription);

        GregorianCalendar calDate = new GregorianCalendar();

        //Si setta la data di inizio
        calDate.setTimeInMillis(Long.valueOf(DateConverter.dateToMillis(mCurrentEvent.mStartDate)));
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis());

        //Se è presente anche la data di fine, viene settata al calendario
        if (mCurrentEvent.mEndDate != null) {

            calDate.setTimeInMillis(Long.valueOf(DateConverter.dateToMillis(mCurrentEvent.mEndDate)));
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calDate.getTimeInMillis());
        }

        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);

        startActivity(intent);
    }

    //Endregion

    //Region handle response + error

    /**
     * Gestione della risposta, quando si acquista un biglietto
     * @param response la risposta dal server
     */
    private void handleResponse(JSONObject response) {

        try {

            //Si inserisce il record nel modello e si aggiorna l'importo del portafoglio
            Record insertedRecord = Record.fromJson(response.getJSONObject(Record.Keys.RECORD));
            MainActivity.mCurrentUser.updateBalance(insertedRecord.mAmount);

            //Si salva l'utente corrente, con il nuovo importo
            UserManager.get().save(MainActivity.mCurrentUser);

            mAlertDialog.dismiss();

            //Si aggiorna il numero di partecipazioni nel dettaglio dell'evento
            updateParticipations(response.getString("participations") + "/" +mCurrentEvent.mMaxTicket);

            Snackbar responseSnackBar = Snackbar.make(fab2,
                    response.getString("message"), Snackbar.LENGTH_LONG);

            responseSnackBar.getView().setBackgroundColor(ContextCompat
                    .getColor(getApplicationContext(), R.color.lightGreen));
            responseSnackBar.show();

        } catch (JSONException e) {

            e.printStackTrace();
            mAlertDialog.dismiss();
        }

        animateFAB();
    }

    //Endregion

    //Region Google map

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mGoogleMap.setMyLocationEnabled(true);

        showMap();
    }

    /**
     * Visualizzazione della mappa, con l'indirizzo dell'evento
     */
    private void showMap() {

        if (mGoogleMap == null || mCurrentEvent == null) {

            return;
        }

        //Si salva in una variabile la latitudine e longitudine dell'evento
        LatLng mLocationEvent = new LatLng(mCurrentEvent.mLatitude,
                mCurrentEvent.mLongitude);

        //Si aggiunge un marker personalizzato
        mGoogleMap.addMarker(new MarkerOptions()
                .position(mLocationEvent)
                .title(mCurrentEvent.mTitle))
                .setIcon(BitmapDescriptorFactory.defaultMarker());

        //Si fa lo zoom della fotocamera sul luogo
        final CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(mLocationEvent, DEFAULT_ZOOM);

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMinZoomPreference(6.0f);

        mGoogleMap.moveCamera(cameraUpdate);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Si mostra la mappa al resume della Activity
        showMap();
    }

    //Endregion
}
package lam.project.foureventuserdroid;


import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;
import lam.project.foureventuserdroid.fragment.WalletFragment;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.DateConverter;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;


public class DetailsEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private final static float DEFAULT_ZOOM = 15.0f;

    private Event mCurrentEvent;

    private boolean isFabOpen;

    private AlertDialog mAlertDialog;

    private TextView detailTickets;

    //Status del fab -> close
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    public static String OPEN_FRAGMENT_WALLET = "Portafoglio";

    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    //region create + setView

    //region fabListener

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

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

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

        mCurrentEvent = getIntent().getParcelableExtra(Event.Keys.EVENT);

        setInfo(mCurrentEvent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.anchor_map);

        mapFragment.getMapAsync(this);
    }

    private void setInfo(Event event) {

        String participations = (event.mMaxTicket > 0) ? event.mParticipation + "/" + event.mMaxTicket
                : "" + event.mParticipation;

        updateParticipations(participations);

        String price = (event.isFree()) ? event.mPrice : event.mPrice + "€";

        String time = DateConverter.getTime(event.mStartDate, event.mEndDate);
        String date = DateConverter.getDate(event.mStartDate, event.mEndDate);

        ((TextView) findViewById(R.id.detail_title)).setText(event.mTitle);
        ((TextView) findViewById(R.id.detail_date)).setText(date);
        ((TextView) findViewById(R.id.detail_distance)).setText(event.mAddress);
        ((TextView) findViewById(R.id.detail_desc)).setText(event.mDescription);
        ((TextView) findViewById(R.id.detail_price)).setText(price);
        ((TextView) findViewById(R.id.detail_time)).setText(time);


        TextView nameAuthor = (TextView) findViewById(R.id.profile_owner_name);
        nameAuthor.setText(mCurrentEvent.mAuthor);

        //TODO aggiungere immagine profilo planner, per ricercarla
        /*

        CircleImageView imgUser = (CircleImageView) findViewById(R.id.profile_image);

        String url = FourEventUri.Builder.create(FourEventUri.Keys.USER)
                .appendPath("img").appendEncodedPath(mCurrentEvent.mAuthor).getUri();

        Picasso.with(this).load(url).into(imgUser);

        */
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateParticipations(String participations) {

        if (mCurrentEvent.isFree()) {

            if (mCurrentEvent.willPartecipate()) {

                fab2.setImageResource(R.drawable.ic_participation_full);

            } else {

                fab2.setImageResource(R.drawable.ic_participation_line);
            }

        }


        detailTickets.setText(participations);
    }

    //endregion

    //region fab behaviours

    private void buyTicket() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(fab2.getContext());

        String message;
        String title;

        DialogInterface.OnClickListener positiveListener;
        String positiveListenerText;

        //se il costo del biglietto è inferiore al bilancio del portafoglio dell'utente
        if (Float.parseFloat(mCurrentEvent.mPrice) <= MainActivity.mCurrentUser.balance) {

            message = "Il biglietto ha un costo di " + mCurrentEvent.mPrice + " €." +
                    "\n\nHai un totale di " + MainActivity.mCurrentUser.balance + " €.\nVuoi acquistarlo?";

            title = "Acquisto biglietto";

            positiveListenerText = "Acquista";
            positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {

                    String url = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                            .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

                    try {

                        JSONObject record = Record.Builder
                                .create(-Float.parseFloat(mCurrentEvent.mPrice), Record.Keys.BUY, MainActivity.mCurrentUser.email)
                                .withEvent(mCurrentEvent.mId)
                                .build().toJson();

                        CustomRequest createRecordRequest = new CustomRequest(Request.Method.PUT,
                                url, record,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        handleResponse(response);

                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                                String json;

                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {
                                    switch (response.statusCode) {
                                        case 403:
                                            json = new String(response.data);
                                            json = trimMessage(json, "message");
                                            if (json != null) displayMessage(json);
                                            break;

                                        default:
                                            break;
                                    }
                                }
                            }
                        });

                        animateFAB();
                        VolleyRequest.get(fab2.getContext()).add(createRecordRequest);

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            };

        } else {

            title = "Credito insufficiente";
            message = "Non hai abbastanza crediti per acquistare questo biglietto, ricarica il portafoglio!!";

            positiveListenerText = "Ricarica portafoglio";
            positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent openFragmentBIntent = new Intent(getApplicationContext(), MainActivity.class);
                    openFragmentBIntent.putExtra(OPEN_FRAGMENT_WALLET, "Portafoglio");
                    startActivity(openFragmentBIntent);
                }
            };
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

    private void addParticipation() {

        try {

            String participation = mCurrentEvent.willPartecipate() ? "notparticipate" : "participate";

            String url = FourEventUri.Builder.create(FourEventUri.Keys.EVENT)
                    .appendPath(participation).appendEncodedPath(mCurrentEvent.mId).getUri();

            JSONObject userEmail = new JSONObject("{'email':'" + MainActivity.mCurrentUser.email + "'}");

            CustomRequest updateParticipations = new CustomRequest(Request.Method.POST, url, userEmail,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                Snackbar responseSnackbar = Snackbar.make(fab,
                                        response.getString("message"), Snackbar.LENGTH_SHORT);

                                responseSnackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                                        R.color.lightGreen));

                                responseSnackbar.show();

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

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    private void shareEvent() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        intent.putExtra(Intent.EXTRA_TEXT,
                "Guarda questo evento su FourEvent: http://annina.cs.unibo.it:8080/api/event"
                        + mCurrentEvent.mId);

        startActivity(Intent.createChooser(intent, "Condividi l'evento"));


    }

    private void shareInCalendar() throws ParseException {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, mCurrentEvent.mTitle);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, mCurrentEvent.mAddress);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, mCurrentEvent.mDescription);

        // Setting dates
        GregorianCalendar calDate = new GregorianCalendar();
        long startDate = DateConverter.addYear(mCurrentEvent.mStartDate);
        calDate.setTimeInMillis(startDate);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());

        if (mCurrentEvent.mEndDate != null) {

            long endDate = DateConverter.addYear(mCurrentEvent.mEndDate);
            calDate.setTimeInMillis(endDate);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                    calDate.getTimeInMillis());
        }

        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);

        startActivity(intent);
    }

    //endregion

    //region handle response + error

    private String trimMessage(String json, String key) {
        String trimmedString;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    private void displayMessage(String snackBarString) {

        Snackbar snackbarError = Snackbar.make(fab, snackBarString,
                Snackbar.LENGTH_LONG);

        View snackbarView = snackbarError.getView();

        snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightRed));

        snackbarError.show();
    }

    private void handleResponse(JSONObject response) {

        try {

            //TODO modificare in questo modo anche wallet per le ricariche
            Record insertedRecord = Record.fromJson(response.getJSONObject(Record.Keys.RECORD));

            MainActivity.mCurrentUser.updateBalance(insertedRecord.mAmount);

            UserManager.get().save(MainActivity.mCurrentUser);

            mAlertDialog.dismiss();

            updateParticipations(response.getString("participations") + "/" +mCurrentEvent.mMaxTicket);

            Snackbar responseSnackBar = Snackbar.make(fab2,
                    response.getString("message"), Snackbar.LENGTH_LONG);

            responseSnackBar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightGreen));

            responseSnackBar.show();

        } catch (JSONException e) {

            e.printStackTrace();
            mAlertDialog.dismiss();
        }

        animateFAB();
    }

    //endregion

    //region Google map

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

    private void showMap() {

        if (mGoogleMap == null || mCurrentEvent == null) {

            return;
        }

        LatLng mLocationEvent = new LatLng(mCurrentEvent.mLatitude,
                mCurrentEvent.mLongitude);

        mGoogleMap.addMarker(new MarkerOptions()
                .position(mLocationEvent)
                .title(mCurrentEvent.mTitle))
                .setIcon(BitmapDescriptorFactory.defaultMarker());
        final CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(mLocationEvent, DEFAULT_ZOOM);

        mGoogleMap.moveCamera(cameraUpdate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMap();
    }

    //endregion
}
package lam.project.foureventuserdroid;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.model.Record;
import lam.project.foureventuserdroid.utils.DateConverter;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;


public class DetailsEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private Animation show_fab_1;
    private Animation show_fab_2;
    private Animation hide_fab_1;
    private Animation hide_fab_2;

    private Event mCurrentEvent;

    //Status del fab -> close
    private boolean FAB_Status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

        //Per disabilitare autofocus all'apertura della Activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mCurrentEvent = getIntent().getParcelableExtra(Event.Keys.EVENT);

        FloatingActionButton fab_detail = (FloatingActionButton) findViewById(R.id.fab_detail);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);


        fab_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!FAB_Status) {
                        //Display FAB menu
                        expandFAB();
                        FAB_Status = true;
                    } else {
                        //Close FAB menu
                        hideFAB();
                        FAB_Status = false;
                    }
                }
            });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                String message;

                //se il costo del biglietto è inferiore al bilancio del portafoglio dell'utente
                if(Float.parseFloat(mCurrentEvent.mPrice) <= MainActivity.mCurrentUser.balance) {

                    message = "Il biglietto ha un costo di " + mCurrentEvent.mPrice + " €." +
                            "\n\nHai un totale di "+MainActivity.mCurrentUser.balance+" crediti.\n\nVuoi acquistarlo?";

                    builder.setTitle("Acquisto biglietto");
                    builder.setMessage(message);

                    builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton("Acquista", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            String url = FourEventUri.Builder.create(FourEventUri.Keys.RECORD)
                                    .appendEncodedPath(MainActivity.mCurrentUser.email).getUri();

                            try {

                                JSONObject record = Record.createRecord((-Float.parseFloat(mCurrentEvent.mPrice)),
                                        Record.Keys.BUY, mCurrentEvent.mId);

                                CustomRequest createRecordRequest = new CustomRequest(Request.Method.PUT,
                                        url, record,
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {

                                                try {

                                                    Record insertedRecord = Record.fromJson(response);

                                                    //update balance
                                                    MainActivity.mCurrentUser.updateBalance(insertedRecord.mAmount);

                                                    UserManager.get().save(MainActivity.mCurrentUser);

                                                    dialog.dismiss();

                                                    Snackbar.make(v,
                                                            "Biglietto acquistato con successo!", Snackbar.LENGTH_SHORT)
                                                            .show();

                                                    hideFAB();

                                                } catch (JSONException e) {

                                                    e.printStackTrace();
                                                    dialog.dismiss();
                                                }


                                            }
                                        }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        error.printStackTrace();
                                    }
                                }
                                );

                                VolleyRequest.get(v.getContext()).add(createRecordRequest);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {

                    builder.setTitle("Credito insufficiente");
                    builder.setMessage("Non hai abbastanza credito per aquistare questo biglietto!");

                    builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }


                builder.show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                intent.putExtra(Intent.EXTRA_TEXT,
                        "Guarda questo evento su FourEvent: http://annina.cs.unibo.it:8080/api/event"
                                + mCurrentEvent);

                startActivity(Intent.createChooser(intent, "Condividi l'evento"));

            }
        });

        setInfo(mCurrentEvent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.anchor_map);

        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMap();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

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


    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

    }

    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);
    }

    private void showMap() {

        if(mGoogleMap == null || mCurrentEvent == null) {

            return;
        }

        LatLng mLocationEvent = new LatLng(mCurrentEvent.mLatitude,
                mCurrentEvent.mLongitude);

        mGoogleMap.addMarker(new MarkerOptions()
                .position(mLocationEvent)
                .title(mCurrentEvent.mTitle))
                .setIcon(BitmapDescriptorFactory.defaultMarker());

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mLocationEvent));
    }

    private void setInfo(Event event) {

        String participations = event.mParticipation + "/" + event.mMaxTicket;
        String price = event.mPrice + "€";

        String time = DateConverter.getTime(event.mStartDate,event.mEndDate);
        String date = DateConverter.getDate(event.mStartDate,event.mEndDate);

        ((TextView) findViewById(R.id.detail_title)).setText(event.mTitle);
        ((TextView) findViewById(R.id.detail_date)).setText(date);
        ((TextView) findViewById(R.id.detail_distance)).setText(event.mAddress);
        ((TextView) findViewById(R.id.detail_desc)).setText(event.mDescription);
        ((TextView) findViewById(R.id.detail_tickets)).setText(participations);
        ((TextView) findViewById(R.id.detail_price)).setText(price);
        ((TextView) findViewById(R.id.detail_time)).setText(time);


        final View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Snackbar.make(v.getRootView(), "Bought", Snackbar.LENGTH_LONG)
                        .setAction("action", null)
                        .show();
            }
        };
    }
}

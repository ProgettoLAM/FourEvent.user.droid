package lam.project.foureventuserdroid;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lam.project.foureventuserdroid.model.Event;


public class DetailsEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    private FloatingActionButton fab_detail;
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

        fab_detail = (FloatingActionButton) findViewById(R.id.fab_detail);
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
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), TicketActivity.class);
                startActivity(intent);
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

        /*
        final ScrollView scrollView = (ScrollView) findViewById(R.id.layout_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollX > 0) {

                        if(fab_detail.isShown()) {

                            fab_detail.hide();
                        }

                    } else if (scrollY > 0) {

                        if(!fab_detail.isShown()) {

                            fab_detail.show();
                        }
                    }
                }
            });
        }
        */

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
        String startTime = event.mStartDate.split(" - ")[1];
        String endTime = event.mEndDate.split(" - ")[1];
        String time = startTime + " - " + endTime;
        String startDate = event.mStartDate.split(" - ")[0];
        String endDate = event.mEndDate.split(" - ")[0];
        String date;

        if (startDate.equals(endDate)) {
            date = startDate;
        } else {
            date = startDate + " - " + endDate;
        }
        ((TextView) findViewById(R.id.detail_title)).setText(event.mTitle);
        ((TextView) findViewById(R.id.detail_date)).setText(date);
        ((TextView) findViewById(R.id.detail_distance)).setText(event.mAddress);
        ((TextView) findViewById(R.id.detail_desc)).setText(event.mDescription);
        ((TextView) findViewById(R.id.detail_tickets)).setText(participations);
        ((TextView) findViewById(R.id.detail_price)).setText(event.mPrice + " €");
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

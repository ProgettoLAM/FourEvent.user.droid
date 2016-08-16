package lam.project.foureventuserdroid;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import lam.project.foureventuserdroid.model.Event;

public class DetailsEventActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    private LatLng HOME = new LatLng (42.5034442, 14.1723788);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_details);
        setSupportActionBar(toolbar);

        //Per la visualizzazione del button back sulla toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Event currentEvent = (Event) getIntent().getParcelableExtra(Event.Keys.EVENT);

        setInfo(currentEvent);
    }

    private void initMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }

        CameraPosition homePosition = new CameraPosition.Builder().target(HOME).zoom(16).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(homePosition));

        MarkerOptions marker = new MarkerOptions().position(HOME);
        googleMap.addMarker(marker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();
    }

    private void setInfo(Event event){

        String participations = event.mCurrentTicket+"/"+event.mMaxTicket;

        ((TextView) findViewById(R.id.detail_title)).setText(event.mTitle);
        ((TextView) findViewById(R.id.detail_date)).setText(event.mStartDate);
        ((TextView) findViewById(R.id.detail_distance)).setText(event.mAddress);
        ((TextView) findViewById(R.id.detail_desc)).setText(event.mDescription);
        ((TextView) findViewById(R.id.detail_tickets)).setText(participations);

        final View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Snackbar.make(v.getRootView(),"Bought",Snackbar.LENGTH_LONG)
                        .setAction("action", null)
                        .show();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

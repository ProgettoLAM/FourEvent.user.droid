package lam.project.foureventuserdroid;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailsEventActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    private LatLng HOME = new LatLng (42.5034442, 14.1723788);

    FloatingActionButton fab1;

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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}

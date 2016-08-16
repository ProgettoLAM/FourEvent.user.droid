package lam.project.foureventuserdroid;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.util.List;

import lam.project.foureventuserdroid.complete_profile.StepManager;
import lam.project.foureventuserdroid.complete_profile.CompleteProfileActivity;
import lam.project.foureventuserdroid.fragment.EventsFragment;
import lam.project.foureventuserdroid.fragment.FavouriteFragment;
import lam.project.foureventuserdroid.fragment.ParticipationFragment;
import lam.project.foureventuserdroid.fragment.ProfileFragment;
import lam.project.foureventuserdroid.fragment.SettingsFragment;
import lam.project.foureventuserdroid.fragment.WalletFragment;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static final int REQUEST_RESOLVE_ERROR = 0;
    private GoogleApiClient googleApiClient;
    private boolean resolvingError;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //se il profilo è completo
        if (StepManager.get(this).getStep() == StepManager.COMPLETE) {
            setContentView(R.layout.activity_main);

            //Istanziare un oggetto GoogleApiClient
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.title_events);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //Setto la pagina principale come quella di ricerca degli eventi
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.anchor_point, new EventsFragment())
                    .commit();

        } else {

            Intent completeProfileIntent = new Intent(this, CompleteProfileActivity.class);
            startActivity(completeProfileIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        showFragment(item);

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragment(final MenuItem menuItem) {

        final int itemId = menuItem.getItemId();
        final Fragment nextFragment;

        switch (itemId) {

            case R.id.nav_events:

                nextFragment = new EventsFragment();
                break;

            case R.id.nav_favourites:

                nextFragment = new FavouriteFragment();
                break;

            case R.id.nav_partecipations:

                nextFragment = new ParticipationFragment();
                break;

            case R.id.nav_profile:

                nextFragment = new ProfileFragment();
                break;

            case R.id.nav_wallett:

                nextFragment = new WalletFragment();
                break;

            case R.id.nav_settings:

                nextFragment = new SettingsFragment();
                break;

            //Da qui in poi non si hanno modifiche

            case R.id.nav_vote:

                nextFragment = new EventsFragment();
                break;

            case R.id.nav_logout:

                nextFragment = new EventsFragment();
                break;

            default:
                throw new IllegalArgumentException("No fragment given");
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.anchor_point, nextFragment)
                .commit();
    }

    private View.OnClickListener buttonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            int itemId = v.getId();

            switch (itemId) {

                default:
                    break;
            }

        }
    };

    //Avviare la connessione dai Google Play Services
    @Override
    protected void onStart() {
        super.onStart();
        if (!resolvingError) {
            googleApiClient.connect();
        }
    }

    //Stoppare la connessione dai Google Play Services
    @Override
    protected void onStop() {

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    /*Per sapere con certezza che la connessione ai Google Play Services è stata attivata
      ed avviare la ricezione di informazioni di localizzazione da parte del provider
     */
    @Override
    public void onConnected(Bundle arg0) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setSmallestDisplacement(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (resolvingError)
            return;
        if (result.hasResolution()) {
            try {
                resolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            }
            catch (IntentSender.SendIntentException e)
            {
                googleApiClient.connect();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),this, REQUEST_RESOLVE_ERROR).show();
            resolvingError = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            resolvingError = false;
            if (resultCode == RESULT_OK)
            {
                if (!googleApiClient.isConnecting() &&
                        !googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
            }
        }
    }

    //Gestione delle informazioni di localizzazione
    @Override
    public void onLocationChanged(final Location location) {
        double latitude = location.getLatitude();
        updateText(R.id.latitude, String.valueOf(latitude));
        double longitude = location.getLongitude();
        updateText(R.id.longitude, String.valueOf(longitude));

        //Metodo per evitare che i tempi di latenza bloccano l'Activity
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... voids)
            {
                //Per convertire le coordinate in informazioni stradali
                Geocoder coder = new Geocoder(MainActivity.this);
                try {
                    List<Address> l = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (l.size()>0)
                        return l.get(0).getLocality();
                } catch (IOException e) {
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                String location;
                String name;

                if (result!=null)
                    updateText(R.id.location, result);
                else {
                    location = UserManager.get().getUser().location;
                    updateText(R.id.location, location);
                }

                name = UserManager.get().getUser().name;
                updateText(R.id.name, name);

            }
        }.execute();
    }

    private void updateText(int id, String text)
    {
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
    }


    @Override
    public void onConnectionSuspended(int i) {}

}

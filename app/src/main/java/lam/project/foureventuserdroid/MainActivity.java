package lam.project.foureventuserdroid;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleApiClient mGoogleApiClient;

    final static String DIALOG_ERROR_TAG = "DIALOG_ERROR_TAG";
    private static final int REQUEST_RESOLVE_ERROR = 2;
    private static final String RESOLVING_ERROR_STATE_KEY = "RESOLVING_ERROR_STATE_KEY";

    private static final int REQUEST_ACCESS_LOCATION = 2;
    private static final int MAX_GEOCODER_RESULTS = 5;

    View headerView;

    private boolean mResolvingError;

    private Location mCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //se il profilo è completo
        if (StepManager.get(this).getStep() == StepManager.COMPLETE) {

            setContentView(R.layout.activity_main);

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

            //V
            //Setto la pagina principale come quella di ricerca degli eventi
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.anchor_point, new EventsFragment())
                    .commit();

            User user = UserManager.get(getApplicationContext()).getUser();

            this.headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);

            TextView name = (TextView) headerView.findViewById(R.id.name);
            TextView location = (TextView) headerView.findViewById(R.id.location);
            name.setText(user.name);
            location.setText(user.location);


            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                        int CAUSE_SERVICE_DISCONNECTED = 1;
                        int CAUSE_NETWORK_LOST = 2;

                        @Override
                        public void onConnected(Bundle bundle) {

                            manageLocationPermission();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            if (mResolvingError) {

                                return;
                            } else if (connectionResult.hasResolution()) {

                                try {

                                    mResolvingError = true;
                                    connectionResult.startResolutionForResult(MainActivity.this,
                                            REQUEST_RESOLVE_ERROR);

                                } catch (IntentSender.SendIntentException e) {

                                    mGoogleApiClient.connect();
                                }
                            } else {

                                mResolvingError = true;
                            }
                        }
                    })
                    .build();

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

    @Override
    protected void onStart() {
        super.onStart();

        if (!mResolvingError) {

            mGoogleApiClient.connect();
        }

        if(mCurrentLocation != null){

            getGeocodeLocation(mCurrentLocation);
        }
    }

    @Override
    protected void onStop() {

        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public static class ErrorDialogFragment extends DialogFragment {

        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int errorCode = this.getArguments().getInt(DIALOG_ERROR_TAG);

            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR
            );
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }

    private void onDialogDismissed() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(RESOLVING_ERROR_STATE_KEY, mResolvingError);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {

            mResolvingError = false;

            if (resultCode == RESULT_OK) {

                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {

                    mGoogleApiClient.connect();
                }
            }
        }
    }

    public void manageLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("title")
                        .setMessage("message")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        REQUEST_ACCESS_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_LOCATION);
            }

        } else if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("title")
                        .setMessage("message")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_ACCESS_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_LOCATION);
            }

        } else {

            mCurrentLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            getGeocodeLocation(mCurrentLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_ACCESS_LOCATION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startLocationListener();
            } else {

                new AlertDialog.Builder(this)
                        .setTitle("Title")
                        .setMessage("Message")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                finish();
                            }
                        }).create().show();
            }
        }
    }

    public void startLocationListener() {

        updateLocation();
    }

    private void updateLocation() {

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1)
                .setExpirationDuration(500L);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        mCurrentLocation = location;

                        getGeocodeLocation(mCurrentLocation);
                    }
                });
    }

    public void getGeocodeLocation(final Location location) {

        if (location != null) {

            if(Geocoder.isPresent()) {

                final GeoCoderAsyncTask geoCoderAsyncTask = new GeoCoderAsyncTask(this,MAX_GEOCODER_RESULTS);

                geoCoderAsyncTask.execute(location);

            } else {

                Toast.makeText(this,"Geocoder not aviabile", Toast.LENGTH_SHORT).show();
            }

        } else {

            Toast.makeText(this,"Geocoder not aviabile", Toast.LENGTH_SHORT).show();
        }
    }

    public class GeoCoderAsyncTask extends AsyncTask<Location, Object, List<android.location.Address>> {

        private int mMaxResult;
        private MainActivity mActivityRef;

        GeoCoderAsyncTask(MainActivity mActivityRef, final int MAX_GEOCODE_RESULT){

            this.mActivityRef = mActivityRef;
            this.mMaxResult = MAX_GEOCODE_RESULT;
        }

        @Override
        protected List<android.location.Address> doInBackground(Location... params) {

            //context null

            MainActivity activity = mActivityRef;

            if(activity == null) {

                return null;
            }

            final Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

            final Location location = params[0];

            List<android.location.Address> geoAddress = null;

            try{

                geoAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), mMaxResult);

            } catch (IOException e) {

                e.printStackTrace();
            }

            return geoAddress;
        }

        @Override
        protected void onPostExecute(List<android.location.Address> addresses) {

            if(addresses != null) {

                updateDrawerHeader(addresses.get(0).getLocality());
            }
        }
    }

    private void updateDrawerHeader(final String addressText) {

        TextView txtLocation = (TextView) headerView.findViewById(R.id.location);

        txtLocation.setText(addressText);
    }
}

package lam.project.foureventuserdroid;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import lam.project.foureventuserdroid.complete_profile.StepManager;
import lam.project.foureventuserdroid.complete_profile.CompleteProfileActivity;
import lam.project.foureventuserdroid.fragment.EventsFragment;
import lam.project.foureventuserdroid.fragment.FavouriteFragment;
import lam.project.foureventuserdroid.fragment.ParticipationFragment;
import lam.project.foureventuserdroid.fragment.ProfileFragment;
import lam.project.foureventuserdroid.fragment.SettingsFragment;
import lam.project.foureventuserdroid.fragment.WalletFragment;
import lam.project.foureventuserdroid.fragment.eventFragment.AllEventsFragment;
import lam.project.foureventuserdroid.model.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

            //Setto la pagina principale come quella di ricerca degli eventi
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.anchor_point, new EventsFragment())
                    .commit();

        }
        else{

            Intent completeProfileIntent = new Intent(this,CompleteProfileActivity.class);
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

    private void showFragment(final MenuItem menuItem){

        final int itemId = menuItem.getItemId();
        final Fragment nextFragment;

        switch (itemId){

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

            default: throw new IllegalArgumentException("No fragment given");
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.anchor_point, nextFragment)
                .commit();
    }

    private View.OnClickListener buttonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            int itemId = v.getId();

            switch (itemId){

                default: break;
            }

        }
    };

}

package lam.project.foureventuserdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;

import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

public class DetailsEventActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    private LatLng position;
    FloatingActionButton fab_detail;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    Animation show_fab_1;
    Animation show_fab_2;
    Animation show_fab_3;
    Animation hide_fab_1;
    Animation hide_fab_2;
    Animation hide_fab_3;


    //Status del fab -> close
    private boolean FAB_Status = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_details);
        setSupportActionBar(toolbar);

        //Per la visualizzazione del button back sulla toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        final Event currentEvent = getIntent().getParcelableExtra(Event.Keys.EVENT);
        position = new LatLng(currentEvent.mLatitude, currentEvent.mLongitude);

        fab_detail = (FloatingActionButton) findViewById(R.id.fab_detail);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        //fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        /*show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);*/

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

                Intent intent = new Intent(v.getContext(),TicketActivity.class);
                startActivity(intent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                intent.putExtra(Intent.EXTRA_TEXT, "Guarda questo evento su FourEvent: http://annina.cs.unibo.it:8080/api/event"+currentEvent);

                startActivity(Intent.createChooser(intent, "Condividi l'evento"));

            }
        });

        final ScrollView scrollView = (ScrollView) findViewById(R.id.layout_main);

        //TODO gestire meglio visualizzazione fab
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN)
                    fab_detail.show();
                else
                    fab_detail.hide();

                return false;
            }
        });

        /*final ScrollView scrollView = (ScrollView) findViewById(R.id.layout_main);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();

                if (scrollY) {
                    if (fab_detail.isShown()) {
                        fab_detail.hide();
                    }
                } else if (scrollY ) {
                    if (!fab_detail.isShown()) {
                        fab_detail.show();
                    }
                }
            }
        });*/

        /*fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Commento sull'evento");

                View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_comment, null);
                builder.setView(viewInflated);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });*/

        setInfo(currentEvent);
    }

    private void initMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }

        CameraPosition homePosition = new CameraPosition.Builder().target(position).zoom(16).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(homePosition));

        MarkerOptions marker = new MarkerOptions().position(position);
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

        /*Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);*/
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

        /*Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);*/
    }


}

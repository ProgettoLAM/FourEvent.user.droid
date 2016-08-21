package lam.project.foureventuserdroid;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lam.project.foureventuserdroid.model.Event;


public class DetailsEventActivity extends AppCompatActivity {

    private SupportMapFragment mMapFragment;

    private GoogleMap googleMap;
    private LatLng position;
    FloatingActionButton fab_detail;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    Animation show_fab_1;
    Animation show_fab_2;
    Animation hide_fab_1;
    Animation hide_fab_2;

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;


    //Status del fab -> close
    private boolean FAB_Status = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_event);


        /*
        mMapFragment = SupportMapFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(R.id.anchor_map,mMapFragment)
                .commit();
        */

        //Per disabilitare autofocus all'apertura della Activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final Event currentEvent = getIntent().getParcelableExtra(Event.Keys.EVENT);
        position = new LatLng(currentEvent.mLatitude, currentEvent.mLongitude);

        /*mRecyclerView = (RecyclerView) findViewById(R.id.comments_rv);
        mAdapter = new CommentAdapter(currentEvent);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);*/

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


        setInfo(currentEvent);
    }

    /*
    private void initMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }

        CameraPosition homePosition = new CameraPosition.Builder().target(position).zoom(16).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(homePosition));

        MarkerOptions marker = new MarkerOptions().position(position);
        googleMap.addMarker(marker);
    }
    */

    @Override
    protected void onResume() {
        super.onResume();
        //initMap();
    }

    private void setInfo(Event event){

        String participations = event.mCurrentTicket+"/"+event.mMaxTicket;
        String startTime = event.mStartDate.split(" - ")[1];
        String endTime = event.mEndDate.split(" - ")[1];
        String time = startTime + " - "+ endTime;
        String startDate = event.mStartDate.split(" - ")[0];
        String endDate = event.mEndDate.split(" - ")[0];
        String date;

        if(startDate.equals(endDate)) {
            date = startDate;
        }
        else {
            date = startDate + " - "+endDate;
        }
        ((TextView) findViewById(R.id.detail_title)).setText(event.mTitle);
        ((TextView) findViewById(R.id.detail_date)).setText(date);
        ((TextView) findViewById(R.id.detail_distance)).setText(event.mAddress);
        ((TextView) findViewById(R.id.detail_desc)).setText(event.mDescription);
        ((TextView) findViewById(R.id.detail_tickets)).setText(participations);
        ((TextView) findViewById(R.id.detail_price)).setText(event.mPrice+ " €");
        ((TextView) findViewById(R.id.detail_time)).setText(time);


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

    public final class CommentsViewHolder extends RecyclerView.ViewHolder{

        private TextView mNameComment;
        private TextView mTextComment;

        private CircleImageView mImageComment;

        private CommentsViewHolder(final View itemView) {

            super(itemView);

            mNameComment = (TextView) itemView.findViewById(R.id.name_comment);
            mTextComment = (TextView) itemView.findViewById(R.id.text_comment);
            mImageComment = (CircleImageView) itemView.findViewById(R.id.profile_image);

            Picasso.with(itemView.getContext()).load("http://annina.cs.unibo.it:8080/api/event/img/img00.jpg").resize(1200,600).into(mImageComment);

        }

        public void bind(Event event){

            mNameComment.setText(event.mTitle);
            //mImageComment.setImageResource(event.mAddress);
            mTextComment.setText(event.mStartDate);

        }
    }

    public final class CommentAdapter extends RecyclerView.Adapter<CommentsViewHolder>{

        private final List<Event> mModel;

        CommentAdapter(final List<Event> model){

            this.mModel = model;
        }

        @Override
        public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View layout = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_events_list,parent,false);

            return new CommentsViewHolder(layout);

        }

        @Override
        public void onBindViewHolder(CommentsViewHolder holder, int position) {

            holder.bind(mModel.get(position));

        }

        @Override
        public int getItemCount() {
            return mModel.size();
        }
    }


}

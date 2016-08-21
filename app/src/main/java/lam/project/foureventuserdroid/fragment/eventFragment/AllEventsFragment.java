package lam.project.foureventuserdroid.fragment.eventFragment;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.DetailsEventActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.utils.connection.EventListRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllEventsFragment extends Fragment {

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    AllEventsAdapter mAdapter;

    public static List<Event> mModel;
    public static List<Event> favouriteEvents;

    ImageView mSadImageEmoticon;
    TextView mEventNotFound;
    ProgressBar mProgressBar;


    ImageView imgEvent;


    FloatingActionButton fab;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mModel = new ArrayList<>();

        setModel();

        FavouriteManager.get(getContext());

        return initViews(inflater.inflate(R.layout.fragment_all_events, container, false));
    }

    private View initViews(View rootView) {

        mSadImageEmoticon = (ImageView) rootView.findViewById(R.id.sad_emoticon);
        mEventNotFound = (TextView) rootView.findViewById(R.id.not_events);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.all_events_progress_bar);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_events_recycler_view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mAdapter = new AllEventsAdapter(mModel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy){

                super.onScrolled(recyclerView, dx, dy);

                if (dy >0) {
                    if (fab.isShown()) {
                        fab.hide();
                    }
                }
                else if (dy <0) {
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                setModel();
            }
        });

        ObjectAnimator animation = ObjectAnimator.ofInt (mProgressBar, "progress", 0, 500);
        animation.setDuration (1000);
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();


        //mostro progress bar e nascondo tutto il resto
        mProgressBar.setVisibility(View.VISIBLE);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mSadImageEmoticon.setVisibility(View.INVISIBLE);
        mEventNotFound.setVisibility(View.INVISIBLE);

        return rootView;
    }

    public final void showAndHideViews() {

        //nascondo sempre la progress bar
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.clearAnimation();

        if(mModel != null && mModel.size() > 0) {

            //mostro la recyclerview
            mRecyclerView.setVisibility(View.VISIBLE);

            //nascondo icone e testo
            mSadImageEmoticon.setVisibility(View.INVISIBLE);
            mEventNotFound.setVisibility(View.INVISIBLE);

        } else {

            //nascondo la recyclerview
            mRecyclerView.setVisibility(View.INVISIBLE);

            //mostro icone e testo
            mSadImageEmoticon.setVisibility(View.VISIBLE);
            mEventNotFound.setVisibility(View.VISIBLE);
        }

    }

    private void setModel(){

        EventListRequest request = new EventListRequest(getString(R.string.url_service),
                new Response.Listener<List<Event>>() {
                    @Override
                    public void onResponse(List<Event> response) {

                        mModel.clear();
                        mModel.addAll(response);

                        List<Event> favouriteEvents = FavouriteManager.get().getFavouriteEvents();

                        for (Event event : mModel) {

                            for (Event favouriteEvent : favouriteEvents) {

                                if (favouriteEvent.mTitle.equals(event.mTitle)) {

                                    event.mIsPreferred = true;
                                }
                            }
                        }

                        mAdapter.notifyDataSetChanged();

                        if(mSwipeRefreshLayout.isRefreshing()) {

                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        showAndHideViews();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorText = error.networkResponse.toString();

                        Snackbar.make(getView(), "Error: " + errorText, Snackbar.LENGTH_SHORT)
                                .setAction("action", null)
                                .show();

                        showAndHideViews();

                    }
        });

        VolleyRequest.get(getContext()).add(request);
    }

    public final class AllEventsViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitleList;
        private TextView mAddressList;
        private TextView mDateList;
        private TextView mTagList;

        private ImageView mFavouriteList;
        private TextView mPriceList;


        private AllEventsViewHolder(final View itemView) {

            super(itemView);

            mTitleList = (TextView) itemView.findViewById(R.id.title_list);
            mAddressList = (TextView) itemView.findViewById(R.id.address_list);
            mDateList = (TextView) itemView.findViewById(R.id.date_list);
            mTagList = (TextView) itemView.findViewById(R.id.tag_list);

            mFavouriteList = (ImageView) itemView.findViewById(R.id.favourite_list);
            mPriceList = (TextView) itemView.findViewById(R.id.price_list);

            imgEvent = (ImageView) itemView.findViewById(R.id.img_event);

            Picasso.with(itemView.getContext()).load("http://annina.cs.unibo.it:8080/api/event/img/img00.jpg").resize(1200,600).into(imgEvent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(itemView.getContext(),DetailsEventActivity.class);

                    intent.putExtra(Event.Keys.EVENT,mModel.get(getAdapterPosition()));
                    startActivity(intent);
                }
            });

            mFavouriteList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Event selectedEvent = mModel.get(getAdapterPosition());

                    selectedEvent = FavouriteManager.get(getContext()).saveOrRemoveEvent(selectedEvent);

                    if(selectedEvent.mIsPreferred) {

                        v.setBackgroundResource(R.drawable.ic_star);

                    } else {

                        v.setBackgroundResource(R.drawable.ic_star_empty);
                    }
                }
            });
        }

        public void bind(Event event){

            mTitleList.setText(event.mTitle);
            mAddressList.setText(event.mAddress);
            mDateList.setText(event.mStartDate);
            mTagList.setText(event.mTag);

            if(event.mPrice.equals("FREE")){
                mPriceList.setText(event.mPrice);
                mPriceList.setTextColor(Color.parseColor("#4CAF50"));
            }
            else
                mPriceList.setText(event.mPrice+ "€");

            if(event.mIsPreferred) {

                mFavouriteList.setBackgroundResource(R.drawable.ic_star);

            } else {

                mFavouriteList.setBackgroundResource(R.drawable.ic_star_empty);
            }
        }
    }

    public final class AllEventsAdapter extends RecyclerView.Adapter<AllEventsViewHolder>{

        private final List<Event> mModel;

        AllEventsAdapter(final List<Event> model){

            this.mModel = model;
        }

        @Override
        public AllEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View layout = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_events_list,parent,false);

            return new AllEventsViewHolder(layout);

        }

        @Override
        public void onBindViewHolder(AllEventsViewHolder holder, int position) {

            holder.bind(mModel.get(position));

        }

        @Override
        public int getItemCount() {
            return mModel.size();
        }
    }

}

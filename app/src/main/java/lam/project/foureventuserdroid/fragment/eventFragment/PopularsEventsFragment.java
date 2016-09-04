package lam.project.foureventuserdroid.fragment.eventFragment;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.fragment.EventsFragment;
import lam.project.foureventuserdroid.fragment.WalletFragment;
import lam.project.foureventuserdroid.fragment.recyclerView.EventAdapter;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.utils.connection.EventListRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.HandlerManager;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopularsEventsFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    public PopularsEventsFragment() {
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;

    private ImageView mSadImageEmoticon;
    private TextView mEventNotFound;
    private ProgressBar mProgressBar;

    private List<Event> mModel = new ArrayList<>();

    public static PopularsEventsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PopularsEventsFragment fragment = new PopularsEventsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = initView(inflater.inflate(R.layout.fragment_populars_event, container, false));

        setModel();

        FavouriteManager.get(getContext());

        return view;
    }

    /**
     * @param rootView view su cui viene fatto l'inflate
     * @return la stessa view
     **/

    private View initView(View rootView) {

        //Inizializzazione delle view e
        mSadImageEmoticon = (ImageView) rootView.findViewById(R.id.events_sad_emoticon);
        mEventNotFound = (TextView) rootView.findViewById(R.id.events_not_found);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.events_recycler_view);

        mAdapter = new EventAdapter(getActivity(), mModel);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);


        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.events_swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                setModel();
            }
        });

        //Inizio l'animazione della progress bar
        ObjectAnimator animation = ObjectAnimator.ofInt(mProgressBar, "progress", 0, 500);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        //mostro progress bar e nascondo tutto il resto
        mProgressBar.setVisibility(View.VISIBLE);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mSadImageEmoticon.setVisibility(View.INVISIBLE);
        mEventNotFound.setVisibility(View.INVISIBLE);

        return rootView;
    }

    /**
     * Mostro/Nascondo le view
     */

    public final void showAndHideViews() {

        //nascondo sempre la progress bar
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.clearAnimation();

        if (mModel != null && mModel.size() > 0) {

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

    private void setModel() {

        String url = FourEventUri.Builder.create(FourEventUri.Keys.EVENT)
                .appendEncodedPath(MainActivity.mCurrentUser.email)
                .appendQueryParameter(EventListRequest.QUERY_TYPE, EventListRequest.TYPE_POPULAR)
                .getUri();

        EventListRequest request = new EventListRequest(url, new Response.Listener<List<Event>>() {
            @Override
            public void onResponse(List<Event> response) {

                mModel.clear();
                mModel.addAll(response);
                Collections.reverse(mModel);


                List<Event> favouriteEvents = FavouriteManager.get().getFavouriteEvents();

                for (Event event : mModel) {

                    for (Event favouriteEvent : favouriteEvents) {

                        if (favouriteEvent.mId != null && favouriteEvent.mId.equals(event.mId)) {

                            event.mIsPreferred = true;
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();

                if (mSwipeRefreshLayout.isRefreshing()) {

                    mSwipeRefreshLayout.setRefreshing(false);
                }

                showAndHideViews();

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        mEventNotFound.setText(R.string.events_not_found);

                        showAndHideViews();
                    }
                });

        VolleyRequest.get(getContext()).add(request);
    }
}
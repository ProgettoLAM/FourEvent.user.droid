package lam.project.foureventuserdroid.fragment.eventFragment;


import android.animation.ObjectAnimator;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.fragment.recyclerView.EventAdapter;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.utils.connection.EventListRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;

public class NearEventsFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    public NearEventsFragment() {}

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;

    private ImageView mSadImageEmoticon;
    private TextView mEventNotFound;
    private ProgressBar mProgressBar;

    private List<Event> mModel = new ArrayList<>();

    /**
     * Creazione del fragment del tab layout, assegnandogli una pagina
     * @param page numero della pagina
     * @return fragment degli eventi filtrati per vicinanza
     */
    public static NearEventsFragment newInstance(int page) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NearEventsFragment fragment = new NearEventsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = initView(inflater.inflate(R.layout.fragment_near_events, container, false));

        setModel();
        FavouriteManager.get(getContext());

        return view;
    }

    /**
     * Metodo per inizializzare i campi necessari per la visualizzazione della lista di eventi
     * @param rootView view dalla quale ricercare gli id degli items
     * @return view completa dei vari riferimenti
     * @see View nell'onCreateView
     */
    private View initView(View rootView) {

        mSadImageEmoticon = (ImageView) rootView.findViewById(R.id.events_sad_emoticon);
        mEventNotFound = (TextView) rootView.findViewById(R.id.events_not_found);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.events_recycler_view);

        mAdapter = new EventAdapter(getActivity(),mModel);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.events_swipe_refresh_layout);

        //Quando si effettua lo swipe refresh del fragment, viene settata nuovamente la recycler view
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                setModel();
            }
        });

        setModel();

        //Inizio l'animazione della progress bar
        ObjectAnimator animation = ObjectAnimator.ofInt (mProgressBar, "progress", 0, 500);
        animation.setDuration (1000);
        animation.setInterpolator (new DecelerateInterpolator());
        animation.start ();

        //Mostro progress bar e nascondo tutto il resto
        mProgressBar.setVisibility(View.VISIBLE);

        mRecyclerView.setVisibility(View.INVISIBLE);
        mSadImageEmoticon.setVisibility(View.INVISIBLE);
        mEventNotFound.setVisibility(View.INVISIBLE);

        return rootView;
    }

    /**
     * Metodo per mostrare/nascondere gli elementi del fragment (progress bar e recycler view)
     */
    public final void showAndHideViews() {

        //Nascondo sempre la progress bar
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.clearAnimation();

        //Se la recycler view ha qualche elemento
        if(mModel != null && mModel.size() > 0) {

            //Mostro la recycler view
            mRecyclerView.setVisibility(View.VISIBLE);

            //Nascondo icone e testo di eventi non trovati
            mSadImageEmoticon.setVisibility(View.INVISIBLE);
            mEventNotFound.setVisibility(View.INVISIBLE);

        } else {

            //Nascondo la recycler view
            mRecyclerView.setVisibility(View.INVISIBLE);

            //Mostro icone e testo di eventi non trovati
            mSadImageEmoticon.setVisibility(View.VISIBLE);
            mEventNotFound.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Si prendono dal server tutti gli eventi che sono vicini alla localizzazione dell'utente
     */
    private void setModel(){

        //Se si conosce la localizzazione dell'utente, si prendono le coordinate
        if(MainActivity.mCurrentLocation != null) {

            double lng = MainActivity.mCurrentLocation.getLongitude();
            double lat = MainActivity.mCurrentLocation.getLatitude();

            //Creo l'url per la richiesta
            String url = FourEventUri.Builder.create(FourEventUri.Keys.EVENT)
                    .appendEncodedPath(MainActivity.mCurrentUser.email)
                    .appendQueryParameter(EventListRequest.QUERY_TYPE,EventListRequest.TYPE_NEAR)
                    .appendQueryParameter("lng",String.valueOf(lng))
                    .appendQueryParameter("lat",String.valueOf(lat))
                    .getUri();

            EventListRequest request = new EventListRequest(url,
                    new Response.Listener<List<Event>>() {
                        @Override
                        public void onResponse(List<Event> response) {

                            //Rimpiazzo il modello con tutti gli eventi e li ordino dal più recente
                            mModel.clear();
                            mModel.addAll(response);
                            Collections.reverse(mModel);

                            List<Event> favouriteEvents = FavouriteManager.get().getFavouriteEvents();

                            //Setto gli eventi segnati come preferiti
                            for (Event event : mModel) {

                                for (Event favouriteEvent : favouriteEvents) {

                                    if (favouriteEvent.mId != null && favouriteEvent.mId.equals(event.mId)) {

                                        event.mIsPreferred = true;
                                    }
                                }
                            }

                            mAdapter.notifyDataSetChanged();

                            //Se lo swipe refresh è attivo, si disattiva
                            if(mSwipeRefreshLayout.isRefreshing()) {

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

        } else {

            showAndHideViews();
        }
    }
}
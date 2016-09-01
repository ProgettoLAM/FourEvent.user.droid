package lam.project.foureventuserdroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.fragment.recyclerView.EventAdapter;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    private static final String NAME = "Preferiti";

    private RecyclerView mRecyclerView;
    private EventAdapter mAdapter;

    private ImageView mSadEmoticon;
    private TextView mEventsNotFound;

    private List<Event> mModel = new ArrayList<>();
    private List<Event> mFavourite = new ArrayList<>();;

    public FavouriteFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        setTitle();

        mSadEmoticon = (ImageView) rootView.findViewById(R.id.events_sad_emoticon);
        mEventsNotFound = (TextView) rootView.findViewById(R.id.events_not_found);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.favourite_recycler_view);

        initRecycler();

        return rootView;
    }

    private void initRecycler () {

        mAdapter = new EventAdapter(getActivity(), mModel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        updateRecycler();

        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateRecycler();
            }
        });

    }

    private void updateRecycler () {

        setModel();

        if(mFavourite.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);

            mSadEmoticon.setVisibility(View.INVISIBLE);
            mEventsNotFound.setVisibility(View.INVISIBLE);

            mModel.clear();
            mModel.addAll(mFavourite);

            mAdapter.notifyDataSetChanged();
        }
        else {
            mSadEmoticon.setVisibility(View.VISIBLE);
            mEventsNotFound.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private void setModel () {

        mFavourite = FavouriteManager.get(getContext()).getFavouriteEvents();

        for(Event ev : mFavourite) {

            ev.mIsPreferred = true;
        }
    }

    private void setTitle() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }
}

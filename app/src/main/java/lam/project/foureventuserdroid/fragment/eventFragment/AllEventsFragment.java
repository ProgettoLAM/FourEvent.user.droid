package lam.project.foureventuserdroid.fragment.eventFragment;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Currency;
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

    RecyclerView mRecyclerView;
    AllEventsAdapter mAdapter;

    public static List<Event> mModel;
    public static List<Event> favouriteEvents;

    ImageView sadEmoticon;
    TextView notEvents;
    ImageView favourite;

    FloatingActionButton fab;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mModel = new ArrayList<>();
        setModel();

        final View rootView = inflater.inflate(R.layout.fragment_all_events, container, false);

        sadEmoticon = (ImageView) rootView.findViewById(R.id.sad_emoticon);
        notEvents = (TextView) rootView.findViewById(R.id.not_events);
        favourite = (ImageView) rootView.findViewById(R.id.favourite_list);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_events_recycler_view);
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

        return rootView;
    }

    private void setModel(){

        EventListRequest request = new EventListRequest(getString(R.string.url_service),
                new Response.Listener<List<Event>>() {
                    @Override
                    public void onResponse(List<Event> response) {
                        mRecyclerView.setVisibility(View.VISIBLE);

                        mModel.clear();
                        mModel.addAll(response);
                        mAdapter.notifyDataSetChanged();

                        sadEmoticon.setVisibility(View.INVISIBLE);
                        notEvents.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(getView(), "Error: " + error.getLocalizedMessage(), Snackbar.LENGTH_SHORT)
                                .setAction("action", null)
                                .show();

                        sadEmoticon.setVisibility(View.VISIBLE);
                        notEvents.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.INVISIBLE);

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

                    Event event = mModel.get(getAdapterPosition());

                    Context context = v.getContext();

                    ImageView icon = (ImageView) v;
                    Object tag = icon.getTag();
                    int ic_star = R.drawable.ic_star_empty;

                    if( tag != null && ((Integer)tag).intValue() == ic_star) {
                        ic_star = R.drawable.ic_star;

                        //Salvataggio evento nei preferiti, se cliccata la stella
                        FavouriteManager.get(context).save(event);
                        List<Event> events = FavouriteManager.get(context).getFavouriteEvents();
                        for(Event evt : events) {
                            Log.d("evento aggiunto", evt.mTitle);
                        }
                    }
                    else {
                        FavouriteManager.get(context).removeEvent(event);
                        List<Event> events = FavouriteManager.get(context).getFavouriteEvents();
                        for(Event evt : events) {
                            Log.d("eventi rimasti", evt.mTitle);
                        }
                    }
                    icon.setTag(ic_star);
                    icon.setBackgroundResource(ic_star);
                }
            });

            //TODO far visualizzare all'apertura della app solo le stelle degli eventi salvati
            favouriteEvents = FavouriteManager.get(itemView.getContext()).getFavouriteEvents();
            if (!favouriteEvents.isEmpty()) {
                for (Event evt : favouriteEvents) {
                    for (Event evt2 : mModel) {
                        if(evt.mTitle.equals(evt2.mTitle)) {
                            mFavouriteList.setTag(R.drawable.ic_star);
                            mFavouriteList.setBackgroundResource(R.drawable.ic_star);
                        }
                    }
                }
            }

        }

        public void bind(Event event){

            mTitleList.setText(event.mTitle);
            mAddressList.setText(event.mAddress);
            mDateList.setText(event.mDate);
            mTagList.setText(event.mTag);

            if(event.mPrice.equals("FREE")){
                mPriceList.setText(event.mPrice);
                mPriceList.setTextColor(Color.parseColor("#4CAF50"));
            }
            else
                mPriceList.setText(event.mPrice+ "€");
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

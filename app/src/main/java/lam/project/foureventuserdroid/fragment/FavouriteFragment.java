package lam.project.foureventuserdroid.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.DetailsEventActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    RecyclerView mRecyclerView;
    FavouriteAdapter mAdapter;

    ImageView sadEmoticon;
    TextView notEvents;
    ImageView imgEvent;

    public static List<Event> mModel;
    List<Event> favouriteEvents;

    public FavouriteFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mModel = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Preferiti");

        sadEmoticon = (ImageView) rootView.findViewById(R.id.sad_emoticon);
        notEvents = (TextView) rootView.findViewById(R.id.not_events);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.favourite_recycler_view);

        favouriteEvents = FavouriteManager.get(rootView.getContext()).getFavouriteEvents();

        if(favouriteEvents.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);

            mModel.addAll(favouriteEvents);

            sadEmoticon.setVisibility(View.INVISIBLE);
            notEvents.setVisibility(View.INVISIBLE);

        }
        else {
            sadEmoticon.setVisibility(View.VISIBLE);
            notEvents.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        mAdapter = new FavouriteAdapter(mModel);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public final class FavouriteViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitleList;
        private TextView mAddressList;
        private TextView mDateList;
        private TextView mTagList;

        private ImageView mFavouriteList;
        private TextView mPriceList;

        private FavouriteViewHolder(View itemView) {

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
                    Intent intent = new Intent(v.getContext(), DetailsEventActivity.class);
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

            mFavouriteList.setTag(R.drawable.ic_star);
            mFavouriteList.setBackgroundResource(R.drawable.ic_star);

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
        }
    }

    public final class FavouriteAdapter extends RecyclerView.Adapter<FavouriteViewHolder>{

        private final List<Event> mModel;

        FavouriteAdapter(final List<Event> model){

            this.mModel = model;
        }

        @Override
        public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View layout = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_events_list,parent,false);

            return new FavouriteViewHolder(layout);

        }

        @Override
        public void onBindViewHolder(FavouriteViewHolder holder, int position) {

            holder.bind(mModel.get(position));

        }

        @Override
        public int getItemCount() {
            return mModel.size();
        }
    }

}

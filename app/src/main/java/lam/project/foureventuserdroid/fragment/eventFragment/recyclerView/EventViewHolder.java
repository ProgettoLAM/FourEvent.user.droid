package lam.project.foureventuserdroid.fragment.eventFragment.recyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lam.project.foureventuserdroid.DetailsEventActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;

/**
 * Created by spino on 21/08/16.
 */

public final class EventViewHolder extends RecyclerView.ViewHolder {

    private List<Event> mModel;
    private Activity mSenderActivity;

    private TextView mTitleList;
    private TextView mAddressList;
    private TextView mDateList;
    private TextView mTagList;

    private ImageView mFavouriteList;
    private TextView mPriceList;


    EventViewHolder(final Activity activity, final List<Event> model, final View itemView) {

        super(itemView);

        mSenderActivity = activity;
        mModel = model;

        mTitleList = (TextView) itemView.findViewById(R.id.title_list);
        mAddressList = (TextView) itemView.findViewById(R.id.address_list);
        mDateList = (TextView) itemView.findViewById(R.id.date_list);
        mTagList = (TextView) itemView.findViewById(R.id.tag_list);

        mFavouriteList = (ImageView) itemView.findViewById(R.id.favourite_list);
        mPriceList = (TextView) itemView.findViewById(R.id.price_list);

        ImageView imgEvent = (ImageView) itemView.findViewById(R.id.img_event);

        //TODO completare
        String uri = FourEventUri.Builder.create(FourEventUri.Keys.EVENT)
                .appendPath("img")
                .appendPath("img00.jpg")
                .getUri();

        Picasso.with(itemView.getContext()).load(uri).resize(1200,600).into(imgEvent);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(itemView.getContext(),DetailsEventActivity.class);

                intent.putExtra(Event.Keys.EVENT,mModel.get(getAdapterPosition()));
                mSenderActivity.startActivity(intent);
            }
        });

        mFavouriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Event selectedEvent = mModel.get(getAdapterPosition());

                selectedEvent = FavouriteManager.get(mSenderActivity.getBaseContext()).saveOrRemoveEvent(selectedEvent);

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

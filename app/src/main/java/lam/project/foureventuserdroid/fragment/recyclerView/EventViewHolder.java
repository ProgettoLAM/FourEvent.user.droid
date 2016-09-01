package lam.project.foureventuserdroid.fragment.recyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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
    private TextView mDistanceList;

    private ImageView mFavouriteList;
    private TextView mPriceList;
    private ImageView mImgEvent;


    EventViewHolder(final Activity activity, final List<Event> model, final View itemView) {

        super(itemView);

        mSenderActivity = activity;
        mModel = model;

        mTitleList = (TextView) itemView.findViewById(R.id.title_list);
        mAddressList = (TextView) itemView.findViewById(R.id.address_list);
        mDateList = (TextView) itemView.findViewById(R.id.date_list);
        mTagList = (TextView) itemView.findViewById(R.id.tag_list);
        mPriceList = (TextView) itemView.findViewById(R.id.price_list);
        mDistanceList = (TextView) itemView.findViewById(R.id.distance_list);

        mFavouriteList = (ImageView) itemView.findViewById(R.id.favourite_list);
        mImgEvent = (ImageView) itemView.findViewById(R.id.img_event);



        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(itemView.getContext(),DetailsEventActivity.class);

                Event event = mModel.get(getAdapterPosition());

                intent.putExtra(Event.Keys.EVENT,event);
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

    void bind(Event event){

        mTitleList.setText(event.mTitle);
        mAddressList.setText(event.mAddress);
        mDateList.setText(event.mStartDate);
        mTagList.setText(event.mTag);

        String price;

        if(event.isFree()){
            price = event.mPrice;
            mPriceList.setTextColor(Color.parseColor("#4CAF50"));
        }
        else
            price = event.mPrice+ "€";

        mPriceList.setText(price);

        mFavouriteList.setBackgroundResource((event.mIsPreferred)?R.drawable.ic_star:R.drawable.ic_star_empty);

        if(event.hasDistance()) {

            String distance = new DecimalFormat("#0.00").format(event.mDistance)+" km";
            mDistanceList.setText(distance);
        }



        String url = FourEventUri.Builder.create(FourEventUri.Keys.EVENT)
                .appendPath("img").appendPath(event.mId).getUri();

        Picasso.with(itemView.getContext()).load(url).resize(1200,600).into(mImgEvent);
    }
}

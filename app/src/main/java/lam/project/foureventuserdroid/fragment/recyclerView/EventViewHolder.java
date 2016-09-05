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

final class EventViewHolder extends RecyclerView.ViewHolder {

    private List<Event> mModel;
    private Activity mSenderActivity;

    private TextView mTitleList;
    private TextView mAddressList;
    private TextView mDateList;
    private TextView mTagList;
    private TextView mDistanceList;

    private ImageView mFavouriteList;
    private ImageView mParticipationList;
    private TextView mPriceList;
    private ImageView mImgEvent;

    /**
     * Metodo per inizializzare i campi di un evento
     * @param activity activity dalla quale proviene
     * @param model lista di eventi
     * @param itemView la view del singolo item
     */
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
        mParticipationList = (ImageView) itemView.findViewById(R.id.participation_list);

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

    /**
     * Bind degli elementi di un item con i dati raccolti dal server per quell'evento
     * @param event evento singolo della recycler view
     */
    void bind(Event event){

        mTitleList.setText(event.mTitle);
        mAddressList.setText(event.mAddress);
        mDateList.setText(event.mStartDate);
        mTagList.setText(event.mTag);

        String price;

        //Se l'utente partecipa a questo evento si setta l'icona della partecipazione
        if(event.willPartecipate()) {
            mParticipationList.setVisibility(View.VISIBLE);
        }

        //Se l'evento è gratuito si setta un colore diverso e si toglie il simbolo dell'euro
        if(event.isFree()){
            price = event.mPrice;
            mPriceList.setTextColor(Color.parseColor("#4CAF50"));
        }
        else
            price = event.mPrice+ "€";

        mPriceList.setText(price);

        //Se l'evento è tra i preferiti si colora l'icona relativa
        mFavouriteList.setBackgroundResource((event.mIsPreferred)?R.drawable.ic_star:R.drawable.ic_star_empty);

        //Se si conosce la distanza dell'evento dall'utente, si settano i km
        if(event.hasDistance()) {

            String distance = new DecimalFormat("#0.00").format(event.mDistance)+" km";
            mDistanceList.setText(distance);
        }

        //Caricamento dell'immagine dell'evento
        String url = FourEventUri.Builder.create(FourEventUri.Keys.EVENT)
                .appendPath("img").appendPath(event.mId).getUri();

        Picasso.with(itemView.getContext()).load(url).resize(500,200).centerCrop().into(mImgEvent);
    }
}

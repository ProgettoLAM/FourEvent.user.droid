package lam.project.foureventuserdroid.fragment.recyclerView;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.TicketDetailsActivity;
import lam.project.foureventuserdroid.model.Record;

/**
 * Created by Vale on 27/08/2016.
 */

class TicketViewHolder extends RecyclerView.ViewHolder {

    private Activity mSenderActivity;

    private TextView titleEvent;
    private TextView dateEvent;
    private TextView priceEvent;


    TicketViewHolder(final Activity activity, final View itemView) {

        super(itemView);
        mSenderActivity = activity;

        titleEvent = (TextView) itemView.findViewById(R.id.title_event);
        dateEvent = (TextView) itemView.findViewById(R.id.date_event);
        priceEvent = (TextView) itemView.findViewById(R.id.price_event);

    }

    void bind(final Record record) {

        titleEvent.setText(record.mEvent);
        dateEvent.setText(record.mDate);
        priceEvent.setText(String.valueOf(record.mAmount));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), TicketDetailsActivity.class);
                intent.putExtra(Record.Keys.ID,record.mId);
                mSenderActivity.startActivity(intent);
            }
        });
    }
}

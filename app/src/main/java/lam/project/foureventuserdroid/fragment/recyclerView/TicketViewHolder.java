package lam.project.foureventuserdroid.fragment.recyclerView;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.TicketDetailsActivity;
import lam.project.foureventuserdroid.model.Record;


class TicketViewHolder extends RecyclerView.ViewHolder {

    private Activity mSenderActivity;

    private TextView titleEvent;
    private TextView dateEvent;
    private TextView priceEvent;

    /**
     * Metodo per inizializzare i campi di un biglietto
     * @param activity activity dalla quale proviene
     * @param itemView view del singolo item
     */
    TicketViewHolder(final Activity activity, final View itemView) {

        super(itemView);

        mSenderActivity = activity;

        titleEvent = (TextView) itemView.findViewById(R.id.title_event);
        dateEvent = (TextView) itemView.findViewById(R.id.date_event);
        priceEvent = (TextView) itemView.findViewById(R.id.price_event);

    }

    /**
     * Bind degli elementi di un item con i dati raccolti dal server
     * @param record record dalla quale si prendono i dati
     */
    void bind(final Record record) {

        titleEvent.setText(record.mEvent);
        dateEvent.setText(record.mDate);
        priceEvent.setText((int) Math.abs(record.mAmount) +" €");

        //Al click di un biglietto, si apre l'Activity relativa ai dettagli, nel quale si passa l'id
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

package lam.project.foureventuserdroid.fragment.recyclerView;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Record;

public class TicketAdapter extends RecyclerView.Adapter<TicketViewHolder> {

    private final Activity mSenderActivity;
    private final List<Record> mRecords;
    private View divider;

    public TicketAdapter(final Activity senderActivity, final List<Record> records){

        this.mSenderActivity = senderActivity;
        this.mRecords = records;
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_ticket_list,parent,false);

        divider = layout.findViewById(R.id.divider);

        return new TicketViewHolder(mSenderActivity,layout);
    }

    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {

        //Se l'item è l'ultimo elemento della lista, non si setta il divider al di sotto
        if(position == getItemCount() -1) {
            divider.setVisibility(View.INVISIBLE);
        }
        holder.bind(mRecords.get(position));

    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }
}

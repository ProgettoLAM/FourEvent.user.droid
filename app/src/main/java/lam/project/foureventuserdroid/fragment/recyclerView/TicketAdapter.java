package lam.project.foureventuserdroid.fragment.recyclerView;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Record;

/**
 * Created by Vale on 27/08/2016.
 */

public class TicketAdapter extends RecyclerView.Adapter<TicketViewHolder> {
    private final Activity mSenderActivity;
    private final List<Record> mRecords;


    public TicketAdapter(final Activity senderActivity, final List<Record> records){

        this.mSenderActivity = senderActivity;
        this.mRecords = records;
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_ticket_list,parent,false);

        return new TicketViewHolder(mSenderActivity,mRecords,layout);
    }

    @Override
    public void onBindViewHolder(TicketViewHolder holder, int position) {

        holder.bind(mRecords.get(position));

    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }
}

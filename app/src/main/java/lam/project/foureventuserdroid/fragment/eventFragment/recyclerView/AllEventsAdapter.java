package lam.project.foureventuserdroid.fragment.eventFragment.recyclerView;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Event;

/**
 * Created by spino on 21/08/16.
 */

public final class AllEventsAdapter extends RecyclerView.Adapter<AllEventsViewHolder>{


    private final Activity mSenderActivity;
    private final List<Event> mModel;


    public AllEventsAdapter(final Activity senderActivity, final List<Event> model){

        this.mSenderActivity = senderActivity;
        this.mModel = model;
    }

    @Override
    public AllEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_events_list,parent,false);

        return new AllEventsViewHolder(mSenderActivity,mModel,layout);
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

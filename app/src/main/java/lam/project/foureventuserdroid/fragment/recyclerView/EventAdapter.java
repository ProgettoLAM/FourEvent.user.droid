package lam.project.foureventuserdroid.fragment.recyclerView;

import android.app.Activity;
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

public final class EventAdapter extends RecyclerView.Adapter<EventViewHolder>{


    private final Activity mSenderActivity;
    private final List<Event> mModel;


    public EventAdapter(final Activity senderActivity, final List<Event> model){

        this.mSenderActivity = senderActivity;
        this.mModel = model;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_events_list,parent,false);

        return new EventViewHolder(mSenderActivity,mModel,layout);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {

        holder.bind(mModel.get(position));

    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }
}

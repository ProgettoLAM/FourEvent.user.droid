package lam.project.foureventuserdroid.fragment.eventFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.utils.EventListRequest;
import lam.project.foureventuserdroid.utils.VolleyRequest;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllEventsFragment extends Fragment {

    RecyclerView mRecyclerView;
    AllEventsAdapter mAdapter;

    List<Event> mModel;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mModel = new ArrayList<>();
        setModel();

        final View rootView = inflater.inflate(R.layout.fragment_all_events, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_events_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new AllEventsAdapter(mModel);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void setModel(){

        EventListRequest request = new EventListRequest(getString(R.string.url_service),
                new Response.Listener<List<Event>>() {
                    @Override
                    public void onResponse(List<Event> response) {
                        mModel.clear();
                        mModel.addAll(response);
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(getView(), "Error: " + error.getLocalizedMessage(), Snackbar.LENGTH_SHORT)
                                .setAction("action", null)
                                .show();
                        mModel = new ArrayList<>();
                        mModel.add(Event.Builder.create("title", "date")
                               .withAddress("address")
                                .withTag("tag")
                                .build());
                    }
        });

        VolleyRequest.get(getContext()).add(request);

    }

    /*Si attacca il fragment al MainActivity tramite l'interfaccia
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof VolleyRequest.QueueProvider) {
            mQueueProvider = (VolleyRequest.QueueProvider) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mQueueProvider = null;
    }*/

    public final static class AllEventsViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitleList;
        private TextView mAddressList;
        private TextView mDateList;
        private TextView mTagList;

        private ImageView mFavouriteList;
        private ImageView mShareList;

        public AllEventsViewHolder(View itemView) {
            super(itemView);

            mTitleList = (TextView) itemView.findViewById(R.id.title_list);
            mAddressList = (TextView) itemView.findViewById(R.id.address_list);
            mDateList = (TextView) itemView.findViewById(R.id.date_list);
            mTagList = (TextView) itemView.findViewById(R.id.tag_list);

            mFavouriteList = (ImageView) itemView.findViewById(R.id.favourite_list);
            mShareList = (ImageView) itemView.findViewById(R.id.share_list);
        }

        public void bind(Event event){

            mTitleList.setText(event.title);
            mAddressList.setText(event.address);
            mDateList.setText(event.date);
            mTagList.setText(event.tag);
        }
    }

    public final static class AllEventsAdapter extends RecyclerView.Adapter<AllEventsViewHolder>{

        private final List<Event> mModel;

        AllEventsAdapter(final List<Event> model){

            this.mModel = model;
        }

        @Override
        public AllEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View layout = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_events_list,parent,false);

            return new AllEventsViewHolder(layout);

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

}

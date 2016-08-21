package lam.project.foureventuserdroid.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vipul.hp_hp.timelineview.TimelineView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {

    private FloatingActionButton fab;

    private RecyclerView mRecyclerView;

    private TimeLineAdapter mTimeLineAdapter;

    private List<TimeLineModel> mDataList = new ArrayList<>();


    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wallet, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Portafoglio");

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_wallet);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        initView();

        return rootView;

    }

    private void initView() {

        for(int i = 0;i <20;i++) {
            TimeLineModel model = new TimeLineModel();
            model.setName("Random"+i);
            model.setAge(i);
            mDataList.add(model);
        }

        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }


    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public  TimelineView mTimelineView;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tx_name);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
            mTimelineView.initLine(viewType);
        }
    }

    public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

        private List<TimeLineModel> mFeedList;
        private Context mContext;

        public TimeLineAdapter(List<TimeLineModel> feedList) {
            mFeedList = feedList;
        }

        @Override
        public int getItemViewType(int position) {
            return TimelineView.getTimeLineViewType(position,getItemCount());
        }

        @Override
        public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            mContext = parent.getContext();

            View view;

            view = View.inflate(parent.getContext(), R.layout.item_timeline, null);

            return new TimeLineViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(TimeLineViewHolder holder, int position) {

            TimeLineModel timeLineModel = mFeedList.get(position);

            holder.name.setText("name：" + timeLineModel.getName() + "    age：" + timeLineModel.getAge());

        }

        @Override
        public int getItemCount() {
            return (mFeedList!=null? mFeedList.size():0);
        }

    }

    public class TimeLineModel implements Serializable {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}

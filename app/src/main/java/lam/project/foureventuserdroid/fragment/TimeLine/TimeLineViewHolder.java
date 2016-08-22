package lam.project.foureventuserdroid.fragment.TimeLine;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.vipul.hp_hp.timelineview.TimelineView;

import lam.project.foureventuserdroid.R;

/**
 * Created by spino on 22/08/16.
 */

public class TimeLineViewHolder extends RecyclerView.ViewHolder {
    public TextView name;

    TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.tx_name);
        TimelineView mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}
package lam.project.foureventuserdroid.fragment.eventFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lam.project.foureventuserdroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearEventsFragment extends Fragment {


    public NearEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_near_events,container,false);

        rootView.findViewById(R.id.near_events_recycler_view)
                .setVisibility(View.INVISIBLE);

        return rootView;
    }

}

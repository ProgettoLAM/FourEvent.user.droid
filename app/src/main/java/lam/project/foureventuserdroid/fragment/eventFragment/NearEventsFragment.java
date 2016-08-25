package lam.project.foureventuserdroid.fragment.eventFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

        return initView(inflater.inflate(R.layout.fragment_all_events, container, false));
    }


    private View initView(View rootView) {


        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.INVISIBLE);
        progressBar.clearAnimation();

        rootView.findViewById(R.id.events_recycler_view).setVisibility(View.INVISIBLE);

        rootView.findViewById(R.id.events_sad_emoticon).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.events_not_found).setVisibility(View.VISIBLE);

        return rootView;
    }

}

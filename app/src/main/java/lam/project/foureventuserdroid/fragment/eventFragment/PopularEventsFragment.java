package lam.project.foureventuserdroid.fragment.eventFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lam.project.foureventuserdroid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PopularEventsFragment extends Fragment {


    public PopularEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_popular_events, container, false);

        return rootView;

    }

}

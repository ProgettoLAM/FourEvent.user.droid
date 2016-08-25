package lam.project.foureventuserdroid.fragment;


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
public class TicketsFragment extends Fragment {

    private final String NAME = "Biglietti";

    public TicketsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setTitle();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_participation, container, false);
    }

    private void setTitle() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }

}

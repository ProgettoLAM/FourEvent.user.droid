package lam.project.foureventuserdroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.utils.tabs.SlidingTabLayout;
import lam.project.foureventuserdroid.utils.tabs.ViewPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    private static final int NUMBER_OF_TABS = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Eventi");


        final String[] mTabNames = getResources().getStringArray(R.array.tab_names);

        SlidingTabLayout tabLayout = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), mTabNames, NUMBER_OF_TABS);

        pager.setAdapter(adapter);

        tabLayout.setDistributeEvenly(true);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }
        });

        tabLayout.setViewPager(pager);

        return rootView;
    }

}

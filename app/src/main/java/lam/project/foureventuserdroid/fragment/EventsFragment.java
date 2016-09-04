package lam.project.foureventuserdroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.utils.tabs.ViewPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    private static final int NUMBER_OF_TABS = 3;
    private static final String NAME = "Eventi";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_category_events, container, false);

        setTitle();

        final String[] mTabNames = getResources().getStringArray(R.array.tab_names);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mTabNames, NUMBER_OF_TABS));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        /*SlidingTabLayout tabLayout = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mTabNames, NUMBER_OF_TABS);

        pager.setAdapter(adapter);

        tabLayout.setDistributeEvenly(true);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }
        });

        tabLayout.setViewPager(pager);*/

        return rootView;
    }

    private void setTitle() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }


}

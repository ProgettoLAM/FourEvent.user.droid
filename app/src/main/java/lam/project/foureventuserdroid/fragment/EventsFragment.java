package lam.project.foureventuserdroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.fragment.eventFragment.CategoriesEventsFragment;
import lam.project.foureventuserdroid.fragment.eventFragment.NearEventsFragment;
import lam.project.foureventuserdroid.fragment.eventFragment.PopularsEventsFragment;
import lam.project.foureventuserdroid.utils.tabs.ViewPagerAdapter;

public class EventsFragment extends Fragment {

    private static final int NUMBER_OF_TABS = 3;
    private static final String NAME = "Eventi";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_category_events, container, false);

        setTitle();

        final String[] mTabNames = getResources().getStringArray(R.array.tab_names);

        //Si ricava il ViewPager e si setta l'adapter per visualizzare gli items
        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), mTabNames, mTabNames.length));
        viewPager.setVisibility(View.VISIBLE);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //Si ricava il PagerSliding e si attacca il ViewPager a questo
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        tabsStrip.setViewPager(viewPager);

        return rootView;
    }

    /**
     * Si setta il titolo del fragment
     */
    private void setTitle() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }
}

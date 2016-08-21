package lam.project.foureventuserdroid.utils.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import lam.project.foureventuserdroid.fragment.eventFragment.AllEventsFragment;
import lam.project.foureventuserdroid.fragment.eventFragment.NearEventsFragment;
import lam.project.foureventuserdroid.fragment.eventFragment.PopularEventsFragment;


/**
 * Created by Vale on 19/08/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private String[] mTitles;
    private int numOfTabs;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, String[] mTitles, int numOfTabs) {
        super(fm);
        this.mTitles = mTitles;
        this.numOfTabs = numOfTabs;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;

        switch (position) {
            case 0:
                selectedFragment = new AllEventsFragment();
                break;

            case 1:
                selectedFragment = new PopularEventsFragment();
                break;

            case 2:
                selectedFragment = new NearEventsFragment();
                break;

            default:
                throw new IllegalArgumentException("Tab inesistente");
        }

        return selectedFragment;

    }

    @Override
    public String getPageTitle(int position) {

        return mTitles[position];
    }


    @Override
    public int getCount() {
        return mTitles.length;
    }
}

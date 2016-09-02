package lam.project.foureventuserdroid.utils.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import lam.project.foureventuserdroid.fragment.eventFragment.NearEventsFragment;
import lam.project.foureventuserdroid.fragment.eventFragment.PopularsEventsFragment;
import lam.project.foureventuserdroid.fragment.eventFragment.CategoriesEventsFragment;


/**
 * Created by Vale on 19/08/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

        private String mTitles[];
        private int numOfTabs;

        public ViewPagerAdapter(FragmentManager fm, String[] mTitles, int numOfTabs) {
            super(fm);
            this.mTitles = mTitles;
            this.numOfTabs = numOfTabs;
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return PopularsEventsFragment.newInstance(position +1);

                case 1:
                    return NearEventsFragment.newInstance(position +2);

                case 2:
                    return CategoriesEventsFragment.newInstance(position +3);

                default:
                    throw new IllegalArgumentException("Tab inesistente");
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return mTitles[position];
        }

}

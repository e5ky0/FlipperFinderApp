package com.pinmyballs.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pinmyballs.metier.Flipper;

public class InfoFlipperPagerAdapter extends FragmentPagerAdapter{

    final int PAGE_COUNT = 3;
    private Flipper flipperToDisplay;

    /** Constructor of the class */
    public InfoFlipperPagerAdapter(FragmentManager fm, Flipper flipper) {
        super(fm);
        flipperToDisplay = flipper;
    }
    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int arg0) {
        switch(arg0){

            /** tab1 is selected */
            case 0:
                FragmentCarteFlipper fragment1 = new FragmentCarteFlipper();
                fragment1.setFlipperToDisplay(flipperToDisplay);
                return fragment1;

                /** tab2 is selected */
            case 1:
                return new FragmentActionsFlipper();

                /** tab2 is selected */
            case 2:
                return new FragmentCommentaireFlipper();
        }
        return null;
    }

    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}

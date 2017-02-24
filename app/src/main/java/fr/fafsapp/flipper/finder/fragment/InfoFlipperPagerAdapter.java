package fr.fafsapp.flipper.finder.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.fafsapp.flipper.finder.metier.Flipper;

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
                FragmentActionsFlipper fragment2 = new FragmentActionsFlipper();
                return fragment2;

                /** tab2 is selected */
            case 2:
                FragmentCommentaireFlipper fragment3 = new FragmentCommentaireFlipper();
                return fragment3;
        }
        return null;
    }

    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}

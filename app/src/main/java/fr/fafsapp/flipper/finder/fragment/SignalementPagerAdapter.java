package fr.fafsapp.flipper.finder.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

public class SignalementPagerAdapter extends FragmentPagerAdapter{

    public static final int PAGE_COUNT = 3;
    SparseArray<SignalementWizardFragment> registeredFragments = new SparseArray<SignalementWizardFragment>();

    public SignalementPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int arg0) {
        switch(arg0){
            case 0:
                FragmentSignalementModele fragment1 = new FragmentSignalementModele();
                return fragment1;
            case 1:
                FragmentSignalementAdresse fragment2 = new FragmentSignalementAdresse();
                return fragment2;
            case 2:
                FragmentSignalementMap fragment3 = new FragmentSignalementMap();
                return fragment3;
        }
        return null;
    }
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SignalementWizardFragment fragment = (SignalementWizardFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public SignalementWizardFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
package co.edu.unal.bookswapp;

/**
 * Created by vr on 11/29/17.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyOfferPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MyOfferPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MyOfferAllTab tab1 = new MyOfferAllTab();
                return tab1;
            case 1:
                MyOfferOpenTab tab2 = new MyOfferOpenTab();
                return tab2;
            case 2:
                MyOfferReservedTab tab3 = new MyOfferReservedTab();
                return tab3;
            case 3:
                MyOfferClosedTab tab4 = new MyOfferClosedTab();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

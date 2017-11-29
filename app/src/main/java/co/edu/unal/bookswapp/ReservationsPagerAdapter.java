package co.edu.unal.bookswapp;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by vr on 11/28/17.
 */

public class ReservationsPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ReservationsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ReservationsNotClosedTab tab1 = new ReservationsNotClosedTab();
                return tab1;
            case 1:
                ReservationsClosedTab tab2 = new ReservationsClosedTab();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

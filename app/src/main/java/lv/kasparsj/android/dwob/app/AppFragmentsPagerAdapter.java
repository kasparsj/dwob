package lv.kasparsj.android.dwob.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import lv.kasparsj.android.app.AppFragment;
import lv.kasparsj.android.dwob.R;

public class AppFragmentsPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private AppFragment dailyWordsFragment;
    private AppFragment paliWordFragment;
    private AppFragment dhammaVersesFragment;

    public AppFragmentsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                dailyWordsFragment = new DailyWordsFragment();
                return dailyWordsFragment;
            case 1:
                paliWordFragment = new PaliWordFragment();
                return paliWordFragment;
            case 2:
                dhammaVersesFragment = new DhammaVersesFragment();
                return dhammaVersesFragment;
            default:
                throw new RuntimeException("Invalid tab requested");
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.tab_dwob);
            case 1:
                return context.getResources().getString(R.string.tab_pali);
            case 2:
                return context.getResources().getString(R.string.tab_goenka);
            default:
                throw new RuntimeException("Invalid tab requested");
        }
    }
}
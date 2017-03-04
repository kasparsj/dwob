package lv.kasparsj.android.dwob.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;

import lv.kasparsj.android.app.AppFragment;
import lv.kasparsj.android.dwob.R;

public class AppFragmentsPagerAdapter extends FragmentPagerAdapter {

    private final Context context;
    private final FragmentManager fragmentManager;
    private HashMap<Integer, String> fragmentTags;

    public AppFragmentsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fragmentManager = fm;
        fragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Fragment getItem(int position) {
        Class fragmentClass;
        switch (position) {
            case 0:
                fragmentClass = DailyWordsFragment.class;
                break;
            case 1:
                fragmentClass = PaliWordFragment.class;
                break;
            case 2:
                fragmentClass = DhammaVersesFragment.class;
                break;
            default:
                throw new RuntimeException("Invalid tab requested");
        }
        return Fragment.instantiate(context, fragmentClass.getName(), null);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            fragmentTags.put(position, tag);
        }
        return obj;
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

    public Fragment getFragment(int position) {
        String tag = fragmentTags.get(position);
        if (tag == null)
            return null;
        return fragmentManager.findFragmentByTag(tag);
    }
}
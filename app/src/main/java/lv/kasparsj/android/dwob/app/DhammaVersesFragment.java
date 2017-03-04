package lv.kasparsj.android.dwob.app;

import android.os.Bundle;

import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.DhammaVerses;

public class DhammaVersesFragment extends BaseFragment {

    public DhammaVersesFragment() {
        super(R.layout.fragment_dhamma_verses);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = mainActivity.getDhammaVerses();
    }
}

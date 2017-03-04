package lv.kasparsj.android.dwob.app;

import android.os.Bundle;

import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.PaliWord;

public class PaliWordFragment extends BaseFragment {

    public PaliWordFragment() {
        super(R.layout.fragment_pali_word);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = mainActivity.getPaliWord();
    }
}

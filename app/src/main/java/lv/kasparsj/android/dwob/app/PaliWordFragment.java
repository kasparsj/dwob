package lv.kasparsj.android.dwob.app;

import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.PaliWord;

public class PaliWordFragment extends BaseFragment {

    public PaliWordFragment() {
        super(R.layout.fragment_pali_word);
        model = PaliWord.getInstance();
    }
}

package lv.kasparsj.android.dwob.app;

import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.util.Strings;

public class DailyWordsFragment extends BaseFragment {

    public DailyWordsFragment() {
        super(R.layout.fragment_dwob);
        model = DailyWords.getInstance();
    }

    @Override
    public String buildBodyHtml() {
        DailyWords dailyWords = (DailyWords) model;
        return dailyWords.getHtml() + "<br><br>" +
                (!Strings.isEmpty(dailyWords.getSource()) ? dailyWords.getSource() + "<br><br>" : "") +
                (!Strings.isEmpty(dailyWords.getTranslator()) ? dailyWords.getTranslator() + "<br><br>" : "") +
                (!Strings.isEmpty(dailyWords.getListenLink()) ? "<a href='" + dailyWords.getListenLink() + "'>Listen to Pāli</a><br><br>" : "") +
                (!Strings.isEmpty(dailyWords.getBookLink()) ? "<a href='" + dailyWords.getBookLink() + "'>View Book<br><br>" : "") +
                (!Strings.isEmpty(dailyWords.getTipitakaLink()) ? "<a href='" + dailyWords.getTipitakaLink() + "'>View Pāli in Tipitaka</a><br><br>" : "");
    }
}
package lv.kasparsj.android.dwob.model;

import lv.kasparsj.android.dwob.App;
import lv.kasparsj.android.dwob.feed.DhammaVersesFeedParser;

public class DhammaVerses extends BaseModel {

    private static DhammaVerses instance;

    private DhammaVerses() {
        super();
    }

    public static DhammaVerses getInstance() {
        if (instance == null) {
            instance = new DhammaVerses();
        }
        return instance;
    }

    @Override
    protected String getSaveNS() {
        return "dhammaVerses.";
    }

    @Override
    public void update() {
        App app = App.applicationContext;
        String feedUrl = app.getDhammaVersesUrl();
        update(new DhammaVersesFeedParser(feedUrl));
    }
}

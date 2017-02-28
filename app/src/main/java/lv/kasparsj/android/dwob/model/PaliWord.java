package lv.kasparsj.android.dwob.model;

import lv.kasparsj.android.dwob.App;
import lv.kasparsj.android.dwob.feed.PaliWordFeedParser;

public class PaliWord extends BaseModel {

    private static PaliWord instance;

    private PaliWord() {
        super();
    }

    public static PaliWord getInstance() {
        if (instance == null) {
            instance = new PaliWord();
        }
        return instance;
    }

    @Override
    protected String getSaveNS() {
        return "paliWord.";
    }

    @Override
    public void update() {
        App app = App.applicationContext;
        String feedUrl = app.getPaliWordUrl();
        update(new PaliWordFeedParser(feedUrl));
    }
}

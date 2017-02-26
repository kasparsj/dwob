package lv.kasparsj.android.dwob;

import android.content.Context;

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
        update(app.getDhammaVersesUrl());
    }

    @Override
    public void refresh() {

    }

    private void update(String feedUrl) {
        Context context = App.applicationContext;
        new LoadFeedTask(context, this, new DhammaVersesFeedParser(feedUrl)).execute();
    }
}

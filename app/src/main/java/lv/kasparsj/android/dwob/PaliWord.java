package lv.kasparsj.android.dwob;

import android.content.Context;

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
        update(app.getPaliWordUrl());
    }

    @Override
    public void refresh() {

    }

    private void update(String feedUrl) {
        Context context = App.applicationContext;
        new LoadFeedTask(this, new PaliWordFeedParser(feedUrl)).execute();
    }
}

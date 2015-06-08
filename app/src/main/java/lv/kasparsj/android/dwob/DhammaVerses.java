package lv.kasparsj.android.dwob;

public class DhammaVerses extends BaseModel {

    private static DhammaVerses instance;

    private DhammaVerses(App applicationContext) {
        super(applicationContext);
    }

    public static DhammaVerses getInstance() {
        if (instance == null) {
            instance = new DhammaVerses(App.applicationContext);
        }
        return instance;
    }

    @Override
    protected String getSaveNS() {
        return "dhammaVerses.";
    }

    @Override
    public void update() {
        App app = (App) context.getApplicationContext();
        update(app.getDhammaVersesUrl());
    }

    private void update(String feedUrl) {
        new LoadFeedTask(context, this, new DhammaVersesFeedParser(feedUrl)).execute();
    }
}

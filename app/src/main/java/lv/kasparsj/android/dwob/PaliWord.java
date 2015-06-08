package lv.kasparsj.android.dwob;

public class PaliWord extends BaseModel {

    private static PaliWord instance;

    private PaliWord(App applicationContext) {
        super(applicationContext);
    }

    public static PaliWord getInstance() {
        if (instance == null) {
            instance = new PaliWord(App.applicationContext);
        }
        return instance;
    }

    @Override
    protected String getSaveNS() {
        return "paliWord.";
    }

    @Override
    public void update() {
        App app = (App) context.getApplicationContext();
        update(app.getPaliWordUrl());
    }

    private void update(String feedUrl) {
        new LoadFeedTask(context, this, new PaliWordFeedParser(feedUrl)).execute();
    }
}

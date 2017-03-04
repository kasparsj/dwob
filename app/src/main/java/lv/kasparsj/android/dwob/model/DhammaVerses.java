package lv.kasparsj.android.dwob.model;

import android.content.Context;

import lv.kasparsj.android.dwob.App;
import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.feed.DhammaVersesFeedParser;

public class DhammaVerses extends BaseModel {

    public DhammaVerses(Context context) {
        super(context);
    }

    @Override
    protected String getSettingsNs() {
        return "dhammaVerses.";
    }

    @Override
    public void update() {
        update(new DhammaVersesFeedParser());
    }

    @Override
    public String getFeedUrl() {
        return context.getString(R.string.dhamma_verses_url);
    }
}

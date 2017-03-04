package lv.kasparsj.android.dwob.model;

import android.content.Context;

import lv.kasparsj.android.dwob.App;
import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.feed.PaliWordFeedParser;

public class PaliWord extends BaseModel {

    public PaliWord(Context context) {
        super(context);
    }

    @Override
    protected String getSettingsNs() {
        return "paliWord.";
    }

    @Override
    public void update() {
        update(new PaliWordFeedParser());
    }

    @Override
    public String getFeedUrl() {
        return context.getString(R.string.pali_word_url);
    }
}

package lv.kasparsj.android.dwob.feed;

import android.os.AsyncTask;

import java.util.List;

import lv.kasparsj.android.dwob.model.BaseModel;
import lv.kasparsj.android.feed.FeedItem;
import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.android.util.OneLog;

public class LoadFeedTask extends AsyncTask<String, Void, Boolean>
{
    protected BaseModel model;
    protected SaxFeedParser feedParser;

    public LoadFeedTask(BaseModel model, SaxFeedParser feedParser) {
        this.model = model;
        this.feedParser = feedParser;
    }

    @Override
    protected void onPreExecute() {
        model.setLoading(true);
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        model.setLoading(false, success);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            List<? extends FeedItem> feedItems = feedParser.parse();
            model.update(feedItems);
            return true;
        } catch (RuntimeException e) {
            OneLog.e(e.getMessage(), e);
            return false;
        }
    }
}
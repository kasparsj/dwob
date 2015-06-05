package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.android.util.OneLog;

public class LoadDhammaVersesTask extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private Resources r;

    public LoadDhammaVersesTask(Context context) {
        this.context = context;
        this.r = context.getResources();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            // Try querying Pariyatti API for today's word
            App app = (App) context.getApplicationContext();
            OneLog.i("Will load from: " + app.getDhammaVersesUrl());
            SaxFeedParser rssParser = new DhammaVersesFeedParser(app.getDhammaVersesUrl());
            List<DhammaVersesFeedItem> feedItems = rssParser.parse(DhammaVersesFeedItem.class);
            DhammaVersesFeedItem feedItem = feedItems.get(0);
            return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
    }
}

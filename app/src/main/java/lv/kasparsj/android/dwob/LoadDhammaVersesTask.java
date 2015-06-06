package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.android.util.OneLog;

public class LoadDhammaVersesTask extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private Resources r;
    private DhammaVerses dhammaVerses;

    public LoadDhammaVersesTask(Context context) {
        this.context = context;
        this.r = context.getResources();
        this.dhammaVerses = DhammaVerses.getInstance();
    }

    protected void onPreExecute() {
        dhammaVerses.setLoading(true);
    }

    protected void onPostExecute(final Boolean success) {
        dhammaVerses.setLoading(false, success);
//        Intent broadcastIntent = new Intent(context, DhammaVersesWidget.class);
//        broadcastIntent.setAction(r.getString(R.string.action_refresh));
//        context.sendBroadcast(broadcastIntent);
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
            long date = feedItem.getDate().getTime();
            String description = feedItem.getDescription();
            if (dhammaVerses.getPubDate() != date || dhammaVerses.getDescription() != description) {
                dhammaVerses.setDescription(description);
                dhammaVerses.setPubDate(date);
            }
            return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
    }
}

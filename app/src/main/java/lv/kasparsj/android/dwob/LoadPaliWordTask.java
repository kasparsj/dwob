package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.android.util.OneLog;

public class LoadPaliWordTask extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private Resources r;
    private PaliWord paliWord;

    public LoadPaliWordTask(Context context) {
        this.context = context;
        this.r = context.getResources();
        this.paliWord = PaliWord.getInstance();
    }

    protected void onPreExecute() {
        paliWord.setLoading(true);
    }

    protected void onPostExecute(final Boolean success) {
        paliWord.setLoading(false, success);
//        Intent broadcastIntent = new Intent(context, PaliWordWidget.class);
//        broadcastIntent.setAction(r.getString(R.string.action_refresh));
//        context.sendBroadcast(broadcastIntent);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            // Try querying Pariyatti API for today's word
            App app = (App) context.getApplicationContext();
            OneLog.i("Will load from: " + app.getPaliWordUrl());
            SaxFeedParser rssParser = new PaliWordFeedParser(app.getPaliWordUrl());
            List<PaliWordFeedItem> feedItems = rssParser.parse(PaliWordFeedItem.class);
            PaliWordFeedItem feedItem = feedItems.get(0);
            long date = feedItem.getDate().getTime();
            String description = feedItem.getDescription();
            if (paliWord.getPubDate() != date || paliWord.getDescription() != description) {
                paliWord.setDescription(description);
                paliWord.setPubDate(date);
            }
            return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
    }
}

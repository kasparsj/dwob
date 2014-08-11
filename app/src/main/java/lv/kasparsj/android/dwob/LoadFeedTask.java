package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.android.util.OneLog;

public class LoadFeedTask extends AsyncTask<String, Void, Boolean> {
	
	private Context context;
	private Resources r;
	
	public LoadFeedTask(Context context) {
		this.context = context;
		this.r = context.getResources();
	}
	
	protected void onPreExecute() {
		OneLog.i("LoadFeedTask::onPreExecute");
		((App) context.getApplicationContext()).setLoading(true);
    }
	
	protected void onPostExecute(final Boolean success) {
		((App) context.getApplicationContext()).setLoading(false, success);
		Intent broadcastIntent = new Intent(context, DwobWidget.class);
		broadcastIntent.setAction(r.getString(R.string.action_refresh));
		context.sendBroadcast(broadcastIntent);
	}
	
	protected Boolean doInBackground(final String... args) {
    	try {
            // Try querying Pariyatti API for today's word
    		App app = (App) context.getApplicationContext();
            OneLog.i("Will load from: "+app.getFeedUrl());
        	SaxFeedParser rssParser = new DwobFeedParser(app.getFeedUrl());
        	List<DwobFeedItem> feedItems = rssParser.parse(DwobFeedItem.class);
            // todo: now 7 items
            DwobFeedItem feedItem = feedItems.get(0);
        	long date = feedItem.getDate().getTime();
            String translated = feedItem.getTranslated();
        	if (app.getPubDate() != date || app.getTranslated() != translated) {
                app.setTitle(feedItem.getDate().toLocaleString());
                app.setTranslated(translated);
                app.setPali(feedItem.getPali());
                app.setPubDate(date);
        	}
        	return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
	}
}
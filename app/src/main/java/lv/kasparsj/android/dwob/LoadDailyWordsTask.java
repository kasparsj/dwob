package lv.kasparsj.android.dwob;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import lv.kasparsj.android.feed.SaxFeedParser;
import lv.kasparsj.android.util.OneLog;

public class LoadDailyWordsTask extends AsyncTask<String, Void, Boolean> {

	private Context context;
	private Resources r;
    private DailyWords dailyWords;
	
	public LoadDailyWordsTask(Context context) {
		this.context = context;
		this.r = context.getResources();
        this.dailyWords = DailyWords.getInstance();
	}
	
	protected void onPreExecute() {
		OneLog.i("LoadDailyWordsTask::onPreExecute");
        dailyWords.setLoading(true);
    }
	
	protected void onPostExecute(final Boolean success) {
        dailyWords.setLoading(false, success);
		Intent broadcastIntent = new Intent(context, DailyWordsWidget.class);
		broadcastIntent.setAction(r.getString(R.string.action_refresh));
		context.sendBroadcast(broadcastIntent);
	}
	
	protected Boolean doInBackground(final String... args) {
    	try {
            // Try querying Pariyatti API for today's word
    		App app = (App) context.getApplicationContext();
            OneLog.i("Will load from: "+app.getDailyWordsUrl());
        	SaxFeedParser rssParser = new DailyWordsFeedParser(app.getDailyWordsUrl());
        	List<DailyWordsFeedItem> feedItems = rssParser.parse(DailyWordsFeedItem.class);
            // todo: now 7 items
            DailyWordsFeedItem feedItem = feedItems.get(0);
        	long date = feedItem.getDate().getTime();
            String translated = feedItem.getTranslated();
        	if (dailyWords.getPubDate() != date || dailyWords.getTranslated() != translated) {
                dailyWords.setTitle(feedItem.getDate().toLocaleString());
                dailyWords.setTranslated(translated);
                dailyWords.setPali(feedItem.getPali());
                dailyWords.setPubDate(date);
        	}
        	return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
	}
}
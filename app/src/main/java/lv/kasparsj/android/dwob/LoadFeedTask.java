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
    private BaseModel model;
    private SaxFeedParser feedParser;
    private Class widgetClass;
	
	public LoadFeedTask(Context context, BaseModel model, SaxFeedParser feedParser, Class widgetClass) {
		this.context = context;
		this.r = context.getResources();
        this.model = model;
        this.feedParser = feedParser;
        this.widgetClass = widgetClass;
	}

    public LoadFeedTask(Context context, BaseModel model, SaxFeedParser feedParser) {
        this(context, model, feedParser, null);
    }
	
	protected void onPreExecute() {
		OneLog.i("LoadFeedTask::onPreExecute");
        model.setLoading(true);
    }
	
	protected void onPostExecute(final Boolean success) {
        model.setLoading(false, success);
        if (widgetClass != null) {
            Intent broadcastIntent = new Intent(context, widgetClass);
            broadcastIntent.setAction(r.getString(R.string.action_refresh));
            context.sendBroadcast(broadcastIntent);
        }
	}
	
	protected Boolean doInBackground(final String... args) {
    	try {
        	List<DailyWordsFeedItem> feedItems = feedParser.parse();
			model.update(feedItems);
        	return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
	}
}
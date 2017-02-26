package lv.kasparsj.android.dwob;

import android.content.Context;
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
	
	public LoadFeedTask(Context context, BaseModel model, SaxFeedParser feedParser, LoadFeedTaskListener loadFeedTaskListener) {
		this.context = context;
		this.r = context.getResources();
        this.model = model;
        this.feedParser = feedParser;
        this.loadFeedTaskListener = loadFeedTaskListener;
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
        if (loadFeedTaskListener != null) {
            loadFeedTaskListener.onFeedLoaded();
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

    private LoadFeedTaskListener loadFeedTaskListener;
    public interface LoadFeedTaskListener {
        public void onFeedLoaded();
    }
}
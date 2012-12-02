package org.pariyatti.dwob;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

public class LoadFeedTask extends AsyncTask<String, Void, Boolean> {
	
	private Context context;
	private Resources r;
	
	public LoadFeedTask(Context context) {
		this.context = context;
		this.r = context.getResources();
	}
	
	protected void onPreExecute() {
		((DwobApp) context.getApplicationContext()).setLoading(true);
    }
	
	protected void onPostExecute(final Boolean success) {
		((DwobApp) context.getApplicationContext()).setLoading(false, success);
		Intent broadcastIntent = new Intent(context, DwobWidget.class);
		broadcastIntent.setAction(r.getString(R.string.action_refresh));
		context.sendBroadcast(broadcastIntent);
	}
	
	protected Boolean doInBackground(final String... args) {
    	try {
            // Try querying Pariyatti API for today's word
        	AndroidSaxFeedParser rssParser = new AndroidSaxFeedParser(r.getString(R.string.feed_url));
        	List<Message> messages = rssParser.parse();
        	String title = messages.get(0).getTitle();
        	String description = messages.get(0).getDescription().trim().replaceAll("^<br />", "").trim();
        	DwobApp app = (DwobApp) context.getApplicationContext();
        	// Update if changed
        	if (app.getTitle() != title || app.getDescription() != description) {
        		app.setTitle(title);
        		app.setDescription(description);
        	}
        	return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
	}
}